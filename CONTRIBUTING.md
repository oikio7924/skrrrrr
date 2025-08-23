# Contributing Guide
이 문서는 우리 팀의 Git 협업 규칙을 정리한 문서입니다.
모든 팀원은 아래 규칙을 따라야 합니다.

---

## Contribution Flow
1. 브랜치를 생성하여 작업한다. (teamOIK, teamNSR, teamYMJ, teamLHK)
2. 커밋 메시지 컨벤션에 맞게 커밋한다.
3. 작업이 완료되면 Pull Request(PR)를 생성한다.
4. 코드 리뷰 및 승인 후 'develop'에 병합한다.
5. 'master'에는 직접 push할 수 없으며, 반드시 PR을 통해 반영한다.
6. 병합은 **Squash and Merge** 방식을 사용한다.

---

## Commit Message Convention
**형식**
- **type 종류**
- feat : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 스타일 변경 (동작 영향 없음)
- refactor : 코드 리팩토링
- test : 테스트 코드 추가/수정
- chore : 빌드, 패키지, 설정 변경

- **예시**
feat(FE:YMJ): 회원가입 페이지 생성
fix(BE:OIK): 로그인 로직 비밀번호 해시 오류 수정
docs(DOCS:LHK): 기여 가이드 문서 업데이트
refactor(FE:NSR): 대화 페이지 컴포넌트 구조 리팩토링

**작성 원칙**
1. 한 커밋에는 하나의 의미 있는 변경만 담기
    (불필요하게 여러 기능을 한 커밋에 몰지 않기)

2. 제목(subject)은 50자 이내
    (간단하고 직관적으로)

3. 본문(body)추가 시 변경 이유, 상세 설명 작성
**예시**
feat(FE:YMJ): 회원가입 페이지 생성
- 이메일/비밀번호 입력 폼 추가
- 서버 API 연동 준비 (미완)

4. 이슈 번호 연결
- PR/이슈 트래킹 시 #이슈번호 같이 명시
**예시**
fix(BE:OIK): 회원가입 API 응답 오류 수정 (#12)

## Pull Request Rule
1. **PR 제목**은 커밋 메시지와 동일하게 작성한다.
2. **PR 설명**에는 주요 변경 내용, 테스트 방법, 관련 이슈 번호를 적는다.
3. 모든 PR은 최소 1명 이상(팀장) 리뷰 및 승인 후 병합한다.
4. 리뷰에서 발생한 모든 대화(Conversation)는 해결 후 머지한다.
5. **머지 방식은 Squash and Merge**를 사용한다.

## Code & Workflow
- **프론트엔드(Vue3)** : ESLint + Prettier 규칙 준수
- **백엔드(SpringBoot, Java)**: Checkstyle/IDE Formatter 준수
- 의미 있는 단위로 커밋하고, 너무 많은 변경을 한 PR에 몰지 않는다.
- 모든 작업은 이슈와 연결하여 진행한다. (`#이슈번호`)

`위 규칙은 우리 팀의 협업 효율성과 코드 품질 유지를 위해 반드시 준수해야 합니다.`