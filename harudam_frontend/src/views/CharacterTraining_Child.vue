<template>
  <div class="page">
    <header class="topbar">
      <button class="icon-btn" @click="$router.back()" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#9378d5" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 18l-6-6 6-6" />
        </svg>
      </button>
      <h1 class="title">캐릭터 학습 완료</h1>
      <div style="width:28px"></div>
    </header>

    <main class="content">
      <div class="avatar-wrapper">
        <img :src="charImageUrl" alt="학습된 캐릭터 이미지" class="avatar" />
      </div>
      <h2 class="done-title">자녀 캐릭터 학습이 완료되었어요!</h2>
      <p class="done-desc">이제 챗봇이 이 캐릭터로 대화할 수 있습니다.</p>

      <div class="actions">
        <button class="btn primary big" @click="confirmUse = true">이 캐릭터로 사용</button>
        <button class="btn gray big" @click="goToExtraTraining">추가 학습하기</button>
      </div>
    </main>

    <!-- 확인 모달 (예/아니오) -->
    <div v-if="confirmUse" class="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="confirm-title" @keydown.esc="confirmUse=false" @click.self="confirmUse=false">
      <div class="modal" @click.stop>
        <h3 id="confirm-title" class="modal-title">이 캐릭터를 최종 선택하시겠습니까?</h3>
        <div class="modal-actions between">
          <button class="btn gray" @click="confirmUse=false">아니오</button>
          <button class="btn primary" @click="selectCharacter">예</button>
        </div>
      </div>
    </div>

    <!-- 완료 알림 모달 -->
    <div v-if="doneOpen" class="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="done-title" @keydown.esc="doneOpen=false" @click.self="doneOpen=false">
      <div class="modal" @click.stop>
        <h3 id="done-title" class="modal-title">최종 캐릭터가 선택되었습니다.</h3>
        <div class="modal-actions end">
          <button class="btn primary" @click="closeDone">확인</button>
        </div>
      </div>
    </div>

    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import FooterNav from '@/components/FooterNav.vue'

const router = useRouter()

// 실제 저장된 캐릭터 이미지 URL로 교체하세요.
const charImageUrl = ref<string>('/sample-character.png')

const confirmUse = ref<boolean>(false)
const doneOpen = ref<boolean>(false)

function goToExtraTraining(): void {
  // 요청: 추가 학습하기 → ChildCharacter 로 이동
  // 라우팅 이름/경로는 앱에 맞게 변경하세요.
  router.push('/mypage/childcharacter')
}

async function selectCharacter(): Promise<void> {
  // TODO: 실제 API에 최종 선택 저장
  // await api.post('/character/select', { url: charImageUrl.value })
  confirmUse.value = false
  doneOpen.value = true
}

function closeDone(): void {
  doneOpen.value = false
  // 필요 시 이전 화면으로 돌아가거나 챗 화면으로 이동
  // router.push('/chat')
}
</script>

<style scoped>
.page { display:flex; flex-direction:column; min-height:100vh; background:#faf9ff; }
.topbar { position:sticky; top:0; left:0; width:100%; display:flex; align-items:center; justify-content:space-between; height:64px; padding:0 20px; background:#fff; border-bottom:1px solid #eee; z-index:10; }
.title { font-size:20px; font-weight:700; }
.icon-btn { background:none; border:none; padding:4px; cursor:pointer; }

.content { flex:1; display:flex; flex-direction:column; align-items:center; justify-content:center; padding:20px; gap:20px; text-align:center; }
.avatar-wrapper { width:200px; height:200px; border-radius:50%; overflow:hidden; box-shadow:0 6px 18px rgba(0,0,0,0.15); }
.avatar { width:100%; height:100%; object-fit:cover; }
.done-title { font-size:20px; font-weight:700; margin-top:16px; }
.done-desc { font-size:15px; color:#555; margin-bottom:20px; }
.actions { display:flex; flex-direction:column; gap:12px; width:100%; max-width:360px; }
.btn { padding:14px 20px; border-radius:12px; font-weight:700; cursor:pointer; font-size:16px; }
.btn.primary { background:#9378d5; color:#fff; border:none; box-shadow:0 4px 12px rgba(147,120,213,0.35); }
.btn.gray { background:#f0f0f0; color:#333; border:none; }
.btn.big { height:56px; }

/* 모달 */
.modal-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; z-index:200; padding:16px; }
.modal { width:min(92vw, 420px); background:#fff; border-radius:16px; padding:18px; box-shadow:0 24px 60px rgba(0,0,0,0.18); border:1px solid #eee; }
.modal-title { margin:0 0 12px; font-size:18px; font-weight:700; color:#333; text-align:center; }
.modal-actions { display:flex; gap:10px; }
.modal-actions.between { justify-content:space-between; }
.modal-actions.end { justify-content:flex-end; }
</style>
