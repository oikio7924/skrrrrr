// src/jspdf-korean-font.js
// 이 파일은 폰트 변환 도구(예: jspdf-fontconverter)를 사용하여
// 생성된 코드를 담고 있습니다.



// 여기에 폰트 데이터가 Base64 문자열 형태로 들어갑니다.
// 아래 코드는 예시입니다. 실제 폰트 데이터로 대체해야 합니다.
export function addKoreanFont(doc) {
    // 폰트를 'KoreanFont'라는 이름으로 등록합니다.
    doc.addFileToVFS('KoreanFont-normal.ttf', fontBase64);
    doc.addFont('KoreanFont-normal.ttf', 'KoreanFont', 'normal');
    // 볼드체를 사용하고 싶다면 아래 코드를 추가합니다.
    doc.addFont('KoreanFont-normal.ttf', 'KoreanFont', 'bold');
}