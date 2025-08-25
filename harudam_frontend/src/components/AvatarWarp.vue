<template>
  <div ref="host" class="warp-host"></div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, watch, ref } from 'vue'
import * as PIXI from 'pixi.js'

const props = defineProps({
  imgSrc:   { type: String, required: true },
  openness: { type: Number, default: 0 },                           // 0~1
  mouthRect:{ type: Object, default: () => ({ x:50,y:78,w:28,h:14 })}, // %
  grid:     { type: Number, default: 24 },
  width:    { type: Number, default: 480 }
})

const host = ref(null)
let app, mesh, geometry, verticesBase, texture, raf

onMounted(async () => {
  // ✅ PIXI v7 초기화
  app = new PIXI.Application({
    width: props.width,
    height: props.width,
    antialias: true,
    backgroundAlpha: 0,    // 디버깅하려면 background: 0x222222
  })
  if (host.value) host.value.appendChild(app.view)

  // 텍스처 로드
  try {
    texture = await PIXI.Assets.load(props.imgSrc)
  } catch (e) {
    console.error('PIXI load error:', e)
    return
  }

  const spriteW = app.renderer.width
  const spriteH = app.renderer.height

  // cover UV 계산 (이미지 비율 보정)
  const texRatio  = texture.width / texture.height
  const viewRatio = spriteW / spriteH
  let u0=0, v0=0, u1=1, v1=1
  if (texRatio > viewRatio) {
    const pad = (1 - viewRatio/texRatio) / 2
    u0 = pad; u1 = 1 - pad
  } else {
    const pad = (1 - texRatio/viewRatio) / 2
    v0 = pad; v1 = 1 - pad
  }

  // 격자 생성
  const cols = props.grid, rows = props.grid
  const verts = [], uvs = [], indices = []
  for (let j=0; j<=rows; j++){
    for (let i=0; i<=cols; i++){
      const x = (i/cols) * spriteW
      const y = (j/rows) * spriteH
      verts.push(x, y)

      const u = u0 + (i/cols) * (u1 - u0)
      const v = v0 + (j/rows) * (v1 - v0)
      uvs.push(u, v)
    }
  }
  for (let j=0; j<rows; j++){
    for (let i=0; i<cols; i++){
      const a = j*(cols+1)+i
      const b = a+1
      const c = a+(cols+1)
      const d = c+1
      indices.push(a,b,c, b,d,c)
    }
  }

  geometry = new PIXI.MeshGeometry(
    new Float32Array(verts),
    new Float32Array(uvs),
    new Uint32Array(indices)
  )

  // ✅ v7: 텍스처로 머티리얼 생성
  const material = new PIXI.MeshMaterial(texture)
  mesh = new PIXI.Mesh(geometry, material)
  app.stage.addChild(mesh)

  // 원본 정점 백업
  verticesBase = geometry.getBuffer('aPosition').data.slice()

  animate()
})

onBeforeUnmount(() => {
  cancelAnimationFrame(raf)
  app?.destroy(true)
})

watch(() => props.openness, () => deform())
watch(() => props.mouthRect, () => deform(), { deep: true })

function animate(){
  deform()
  raf = requestAnimationFrame(animate)
}

function deform(){
  if (!geometry || !verticesBase || !app) return

  const cols = props.grid, rows = props.grid
  const buf = geometry.getBuffer('aPosition').data

  // 입 사각형(뷰 좌표)
  const cx = (props.mouthRect.x/100) * app.renderer.width
  const cy = (props.mouthRect.y/100) * app.renderer.height
  const rw = (props.mouthRect.w/100) * app.renderer.width
  const rh = (props.mouthRect.h/100) * app.renderer.height

  const open = props.openness        // 0~1
  const maxDrop = rh * 0.9

  for (let j=0; j<=rows; j++){
    for (let i=0; i<=cols; i++){
      const idx = (j*(cols+1)+i)*2
      const x0 = verticesBase[idx]
      const y0 = verticesBase[idx+1]

      // 중심/피벗 기준 가중치(윗입술 거의 고정, 아래턱 위주 변형)
      const dx = (x0 - cx) / (rw*0.5)
      const dy = (y0 - (cy - rh*0.05)) / (rh*0.6)
      const w = Math.exp(-(dx*dx + dy*dy) * 2.0)

      const drop  = maxDrop * open * w * Math.max(0, dy)
      const widen = 1 + open * 0.12 * w

      buf[idx]   = cx + (x0 - cx) * widen
      buf[idx+1] = y0 + drop
    }
  }
  geometry.getBuffer('aPosition').update()
}
</script>

<style scoped>
.warp-host{ width:100%; display:flex; justify-content:center; }
</style>
