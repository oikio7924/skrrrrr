```mermaid
sequenceDiagram
    participant Child as 자녀(ChildUser)
    participant FE as 프론트(Vue)
    participant Auth as AuthController/AuthService
    participant Member as MemberController/MemberService
    participant Parent as 부모(ParentUser)
    participant Repo as DB (PostgreSQL)

    %% 1. 자녀 소셜 로그인
    Child ->> FE: 카카오/구글/네이버 로그인
    FE ->> Auth: POST /api/auth/login/{provider}
    Auth ->> Repo: ChildUser 생성 (status=PENDING)
    Auth -->> FE: JWT + UserDto

    %% 2. 자녀 정보 입력 & 동의
    Child ->> FE: 이름/아이디/비밀번호/성별/생일/핸드폰<br/>자녀 목소리 녹음 + 이미지 업로드<br/>개인정보 동의
    FE ->> Member: PATCH /api/member/child/{id}/complete
    Member ->> Repo: ChildUser 업데이트 (OnboardingState=CONSENTED)

    %% 3. 부모 정보 입력
    Parent ->> FE: 이름/생일/성별/핸드폰/주소<br/>부모 이미지 업로드
    FE ->> Member: POST /api/member/parent/register
    Member ->> Repo: ParentUser 생성 (status=PENDING)
    Member ->> Auth: issueParentCode()
    Auth ->> Repo: AuthCode 저장
    Auth -->> Parent: 인증코드 카카오 알림톡 발송

    %% 4. 부모 인증코드 검증 (자녀 화면)
    Child ->> FE: 부모 인증코드 입력
    FE ->> Auth: POST /api/auth/verify-code
    Auth ->> Repo: AuthCode 검증 → 상태=USED
    Auth ->> Repo: ParentChildLink 생성
    Auth ->> Repo: ChildUser/ParentUser 상태=ACTIVE
    Auth -->> FE: 회원가입 완료

    %% 5. 부모 로그인
    Parent ->> FE: 인증코드 입력
    FE ->> Auth: POST /api/auth/parent-login
    Auth ->> Repo: AuthCode 검증
    Auth -->> Parent: 로그인 완료 (정상 서비스 이용)
