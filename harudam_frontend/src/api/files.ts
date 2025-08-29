// src/api/files.ts
import http from '@/api/http'

// -------------------------
// 자녀 사진 업로드
// -------------------------
export async function uploadChildPicture(childId: number, file: File | Blob): Promise<string> {
  const fd = new FormData()
  fd.append('file', file)

  const { data } = await http.post(`/api/files/child/${childId}/picture`, fd, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return data.path as string
}

// -------------------------
// 자녀 목소리 업로드
// -------------------------
export async function uploadChildVoice(childId: number, file: File | Blob): Promise<string> {
  const fd = new FormData()
  fd.append('file', file)

  const { data } = await http.post(`/api/files/child/${childId}/voice`, fd, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return data.path as string
}

// -------------------------
// 부모 사진 업로드
// -------------------------
export async function uploadParentPicture(parentId: number, file: File | Blob): Promise<string> {
  const fd = new FormData()
  fd.append('file', file)

  const { data } = await http.post(`/api/files/parent/${parentId}/picture`, fd, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })

  return data.path as string
}
