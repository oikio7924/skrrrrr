<template>
  <div id="chatbot-page" class="page" @keyup.esc="closeChat" tabindex="0">
    <div class="face-area">
      <img :src="childFaceImage" alt="자녀 얼굴" class="face-img" />
    </div>


    <main class="content">
      <button class="chat-cta" :class="showChat ? 'end' : 'start'" :aria-pressed="showChat.toString()"
        :aria-expanded="showChat.toString()" @click="toggleChat">
        <span class="label">
          <span v-if="!showChat">▶️ 대화 시작</span>
          <span v-else>⏹️ 대화 종료</span>
        </span>
      </button>



    </main>


    <footer class="bottombar">
      <button class="home" @click="goToMain">처음화면 보기</button>
    </footer>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/http'

const childFaceImage = new URL('@/assets/child/child-face.png', import.meta.url).href

const router = useRouter()
const showChat = ref(false)

let pc = null, dataChannel = null
let localStream = null, remoteStream = null
let remoteAudioEl = null

// 녹음/오디오
let audioCtx = null
let mixDest = null
let micSource = null, remoteSource = null
let recorder = null
let chunks = []
let analyser = null, micNode = null

// 상태
let isActive = false
let isPaused = false
let topicIdx = 0
let noReplyTimer = null
let lastEndedReason = null // 'manual-stop' | 'idle-timeout' | 'midnight' | null

// 타이머/세션
let midnightTimer = null
const KST_OFFSET = 9 * 3600 * 1000

// 사용자 식별자
let userId = localStorage.getItem('user_id') || (crypto.randomUUID?.() || String(Date.now()))
localStorage.setItem('user_id', userId)

// ===== 유틸 =====
function todayKeyKST() {
  const nowUtc = Date.now()
  const kst = new Date(nowUtc + KST_OFFSET)
  return kst.toISOString().slice(0, 10)
}

async function waitForIceOrTimeout(peer, ms = 1200) {
  if (peer.iceGatheringState === 'complete') return
  const p = new Promise((resolve) => {
    function check() {
      if (peer.iceGatheringState === 'complete') {
        peer.removeEventListener('icegatheringstatechange', check)
        resolve()
      }
    }
    peer.addEventListener('icegatheringstatechange', check)
  })
  await Promise.race([p, new Promise(r => setTimeout(r, ms))])
}

function msUntilNextMidnightKST() {
  const nowUtc = Date.now()
  const kstNow = new Date(nowUtc + KST_OFFSET)
  const y = kstNow.getUTCFullYear()
  const m = kstNow.getUTCMonth()
  const d = kstNow.getUTCDate()
  const nextKstMidnightUtcMs = Date.UTC(y, m, d + 1, 0, 0, 0) - KST_OFFSET
  return Math.max(1000, nextKstMidnightUtcMs - nowUtc)
}

function loadProgress() {
  topicIdx = Number(localStorage.getItem('topic_idx') || '0')
  lastEndedReason = localStorage.getItem('last_ended_reason') || null
}
function saveProgress() {
  localStorage.setItem('topic_idx', String(topicIdx))
}
function saveEndedReason(reason) {
  lastEndedReason = reason
  localStorage.setItem('last_ended_reason', reason)
}

// ✅ 화두 1개 뽑기(원래 잘못 덮인 함수 복구)
function pickOne(topics) {
  if (!topics || topics.length === 0) return null
  if (topicIdx >= topics.length) topicIdx = 0
  const t = topics[topicIdx++]
  saveProgress()
  return t
}

// ===== 토글 =====
const toggleChat = async () => {
  if (!showChat.value) {
    try {
      await startRealtimeChat()
      showChat.value = true
      isActive = true
    } catch (e) {
      console.error(e)
      alert('대화 시작 실패')
      showChat.value = false
      isActive = false
    }
  } else {
    await pauseRealtimeChat()  // 자동응답/대기 취소
    // await finishAndUpload('manual-stop') // 필요시
    stopRealtimeChat()
    showChat.value = false
    isActive = false
    saveEndedReason('manual-stop')
  }
}

// ===== 메인 플로우 =====
async function startRealtimeChat(){
  let EPHEMERAL = ''
  loadProgress()

  // 세션 발급 & 마이크 병렬
  const sessionPromise = (async () => {
    const qs = new URLSearchParams({ user_id: userId })

    try {
      const { data } = await http.post('/api/session', null, { params: qs })
      EPHEMERAL = data?.client_secret?.value

      if (!EPHEMERAL) {
        throw new Error('no ephemeral in session')
      }

      window.__todayTopics = Array.isArray(data.today_topics) ? data.today_topics : []
    } catch (error) {
      console.error("Session fetch error", error)
    }
  })()

  const micPromise = navigator.mediaDevices.getUserMedia({
    audio: {
      channelCount: 1, sampleRate: 48000,
      echoCancellation: true, noiseSuppression: true, autoGainControl: true,
    }
  })
  await Promise.all([sessionPromise, micPromise])
  localStream = await micPromise

  // RTCPeerConnection
  pc = new RTCPeerConnection({ iceServers: [{ urls: ['stun:stun.l.google.com:19302'] }] })
  dataChannel = pc.createDataChannel('oai-events')

  // 원격 오디오
  remoteAudioEl = document.createElement('audio')
  remoteAudioEl.autoplay = true
  remoteAudioEl.playsInline = true
  document.body.appendChild(remoteAudioEl)

  pc.ontrack = (e) => {
    remoteStream = e.streams[0]
    remoteAudioEl.srcObject = remoteStream
    if (audioCtx && !remoteSource && remoteStream) {
      remoteSource = audioCtx.createMediaStreamSource(remoteStream)
      remoteSource.connect(mixDest)
    }
  }

  localStream.getTracks().forEach(t => pc.addTrack(t, localStream))

  const offer = await pc.createOffer()
  await pc.setLocalDescription(offer)
  await waitForIceOrTimeout(pc, 1200)

  const answerSDP = await fetch('https://api.openai.com/v1/realtime?model=gpt-4o-mini-realtime-preview', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${EPHEMERAL}`,
      'Content-Type': 'application/sdp',
      'OpenAI-Beta': 'realtime=v1',
    },
    body: pc.localDescription.sdp,
  }).then(r => r.text()).catch(() => '')
  if (!answerSDP) { throw new Error('Realtime 연결 실패') }
  await pc.setRemoteDescription({ type: 'answer', sdp: answerSDP })

  // 녹음/분석 구성
  setupRecording()

  // 첫 화두 1개로 바로 시작
  dataChannel.onopen = () => {
    try {
      const topics = window.__todayTopics || []
      const t = pickOne(topics) || '오늘 감사했던 일 한 가지'
      const prompt =
        '반드시 한국어(존댓말)로, 1~2문장으로 말해 주세요.\n' +
        '지금부터 아래 한 개 화두로 대화를 시작하세요. 목록을 나열하지 마세요.\n' +
        `화두: ${t}\n` +
        '질문을 하고 사용자의 응답을 기다리세요. 무응답/요청 시 다음 화두 1개로 전환하세요.'

      dataChannel.send(JSON.stringify({
        type: 'response.create',
        response: { modalities: ['audio'], instructions: prompt }
      }))
      startNoReplyTimer(60000) // 1분 무응답 자동 종료
    } catch (e) {
      console.warn('초기 화두 전송 실패', e)
    }
  }

  startMidnightTimer()
}

function setupRecording(){
  audioCtx = new (window.AudioContext || window.webkitAudioContext)()
  mixDest = audioCtx.createMediaStreamDestination()

  micSource = audioCtx.createMediaStreamSource(localStream)
  micSource.connect(mixDest)

  if (remoteStream && !remoteSource) {
    remoteSource = audioCtx.createMediaStreamSource(remoteStream)
    remoteSource.connect(mixDest)
  }

  // 분석기 (무응답 감지)
  analyser = audioCtx.createAnalyser()
  analyser.fftSize = 512
  micNode = audioCtx.createMediaStreamSource(localStream)
  micNode.connect(analyser)

  chunks = []
  try {
    recorder = new MediaRecorder(mixDest.stream, { mimeType: 'audio/webm;codecs=opus' })
  } catch (e) {
    console.error('MediaRecorder 생성 실패', e)
    alert('오디오 녹음을 초기화할 수 없습니다.')
    return
  }
  recorder.ondataavailable = (e) => { if (e.data && e.data.size > 0) chunks.push(e.data) }
  recorder.start()
}

function startNoReplyTimer(ms = 60000) {
  cancelNoReplyTimer()
  if (!analyser) return
  const buf = new Uint8Array(analyser.frequencyBinCount)
  let spoke = false
  const threshold = 28
  const check = () => {
    if (!analyser) return
    analyser.getByteFrequencyData(buf)
    const avg = buf.reduce((a, b) => a + b, 0) / buf.length
    if (avg > threshold) spoke = true
    if (spoke) { cancelNoReplyTimer(); return }
    noReplyTimer = requestAnimationFrame(check)
  }
  noReplyTimer = requestAnimationFrame(check)
  setTimeout(async () => {
    if (!spoke) {
      await pauseRealtimeChat()
      stopRealtimeChat()
      await finishAndUpload('idle-timeout')
      saveEndedReason('idle-timeout')
      alert('1분 동안 응답이 없어 대화를 종료했어요.')
      showChat.value = false
      isActive = false
    }
  }, ms)
}
function cancelNoReplyTimer() {
  if (noReplyTimer) { cancelAnimationFrame(noReplyTimer); noReplyTimer = null }
}

async function pauseRealtimeChat() {
  try {
    if (dataChannel && dataChannel.readyState === 'open') {
      dataChannel.send(JSON.stringify({ type: 'response.cancel' }))
      dataChannel.send(JSON.stringify({
        type: 'session.update',
        session: { turn_detection: { type: 'server_vad', create_responses: false } }
      }))
    }
  } catch (e) { console.warn('pauseRealtimeChat fail', e) }
  try { remoteAudioEl?.pause(); if (remoteAudioEl) remoteAudioEl.muted = true } catch { }
  isPaused = true
  cancelNoReplyTimer()
}

function startMidnightTimer(){
  clearMidnightTimer()
  midnightTimer = setTimeout(async () => {
    await finishAndUpload('midnight')
    stopRealtimeChat()
    saveEndedReason('midnight')
    alert('오늘의 대화를 저장했어요. 내일 다시 새 이야기로 만나요.')
    showChat.value = false
    isActive = false
  }, msUntilNextMidnightKST())
}
function clearMidnightTimer() { if (midnightTimer) { clearTimeout(midnightTimer); midnightTimer = null } }

async function finishAndUpload(reason = 'manual') {
  try {
    if (!recorder) return
    const day = todayKeyKST()
    const stopped = new Promise(res => { recorder.onstop = res })
    recorder.stop(); await stopped
    const blob = new Blob(chunks, { type: 'audio/webm' })
    if (!blob.size) return

    const { data } = await http.post('/audio/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    if (!r.ok) { const t = await r.text().catch(() => ''); console.error('upload failed', r.status, t) }
  } catch (e) { console.error(e) } finally { chunks = [] }
}

function stopRealtimeChat() {
  clearMidnightTimer()
  cancelNoReplyTimer()
  try { dataChannel?.close() } catch { }
  try { pc?.close() } catch { }

  if (localStream) localStream.getTracks().forEach(t => t.stop())
  if (remoteAudioEl) { try { remoteAudioEl.pause() } catch { }; remoteAudioEl.srcObject = null; remoteAudioEl.remove() }

  try { audioCtx?.close() } catch { }
  pc = null; dataChannel = null
  localStream = null; remoteStream = null
  remoteAudioEl = null
  audioCtx = null; mixDest = null; micSource = null; remoteSource = null
  recorder = null; analyser = null; micNode = null
}

onBeforeUnmount(async () => {
  if (showChat.value) {
    await pauseRealtimeChat()
    stopRealtimeChat()
  }
})

const closeChat = () => { toggleChat() }
const goToMain = () => router.push({ name: 'MainParent' })
</script>


<style scoped>
:root {
  --bg-color: #f6f7fb;
  --danger: #ef4444;
  --success: #22c55e;
  --text-light: #fff;
  --text-dark: #1f2937;
  --border: #e5e7eb;
  --shadow-md: 0 6px 18px rgba(0, 0, 0, .08);
  --radius-xl: 22px;
}

.page {
  min-height: 100dvh;
  max-width: 640px;
  margin: 0 auto;
  display: grid;
  grid-template-rows: 1fr auto auto;
  background: var(--bg-color);
}

.face-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.face-img {
  width: 80vw;
  height: 80vw;
  max-width: 360px;
  max-height: 360px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px;
}

.chat-cta {
  width: 100%;
  max-width: 620px;
  min-height: 80px;
  appearance: none;
  border: none;
  border-radius: 24px;
  font-size: 30px;
  font-weight: bold;
  color: var(--text-light);
  cursor: pointer;
  transition: all .2s;
  box-shadow: var(--shadow-md);
}

.chat-cta.start {
  background-color: #2ecc71;
}

.chat-cta.end {
  background-color: #e74c3c;
}

.chat-cta:hover {
  filter: brightness(1.1);
}

.chat-panel {
  width: 100%;
  max-width: 620px;
  height: min(56vh, 520px);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  background: #fff;
  box-shadow: var(--shadow-md);
  overflow: auto;
  display: grid;
  grid-template-rows: auto 1fr;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border);
  font-weight: 700;
}

.mini-close {
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
  color: #666;
}

.panel-body {
  padding: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.bottombar {
  position: sticky;
  bottom: 0;
  background: #fff;
  padding: 14px 16px 18px;
  box-shadow: 0 -1px 6px rgba(0, 0, 0, 0.08);
}

.home {
  width: 100%;
  min-height: 80px;
  border: none;
  border-radius: 22px;
  font-size: 30px;
  font-weight: bold;
  cursor: pointer;
  background: linear-gradient(135deg, #ff8a4d, #ff6b2c);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 44, .35);
}

.home:hover {
  filter: brightness(1.1);
}
</style>
