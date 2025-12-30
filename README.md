# Demo SSR v1.1

Spring Boot 기반의 서버 사이드 렌더링(SSR) 웹 애플리케이션입니다. 게시판, 사용자 인증, 댓글, 유료 게시글 구매 등의 기능을 제공합니다.

## 기술 스택

### Backend
- Java 17
- Spring Boot 3.4.12
- Spring Data JPA
- Spring Security (암호화)
- Mustache (템플릿 엔진)

### Database
- MySQL
- H2 Database (개발 환경)

### 기타
- Lombok
- Gradle
- BCrypt (비밀번호 암호화)

## 주요 기능

### 사용자 관리
- 회원가입 및 로그인
- 카카오 소셜 로그인 연동
- 프로필 이미지 업로드 및 관리
- 회원 정보 수정
- 포인트 충전 및 관리
- 역할 기반 권한 관리 (ADMIN, USER)

### 게시판
- 게시글 작성, 수정, 삭제
- 게시글 목록 조회 (페이징 처리)
- 게시글 검색 기능
- 게시글 상세 조회
- 유료 게시글 기능 (프리미엄 게시글)
- 게시글 소유자 인가 검사

### 댓글
- 댓글 작성 및 삭제
- 댓글 목록 조회
- 댓글 소유자 인가 검사

### 구매 시스템
- 유료 게시글 구매 기능
- 포인트 차감 처리
- 구매 내역 관리
- 중복 구매 방지

### 관리자
- 관리자 대시보드
- 관리자 권한 검사

### 보안 및 인증
- 세션 기반 인증
- 인터셉터를 통한 인증/인가 처리
- 비밀번호 BCrypt 암호화
- 예외 처리 및 에러 페이지

## 프로젝트 구조

```
src/main/java/org/example/demo_ssr_v1_1/
├── _api/                    # REST API 테스트 컨트롤러
├── _core/                   # 공통 기능
│   ├── config/             # 설정 클래스
│   ├── errors/              # 예외 처리
│   ├── interceptor/         # 인터셉터 (인증, 인가, 세션)
│   └── utils/               # 유틸리티 클래스
├── admin/                   # 관리자 기능
├── board/                   # 게시판 기능
├── purchase/                # 구매 기능
├── reply/                   # 댓글 기능
└── user/                    # 사용자 기능
```

## 데이터베이스 구조

### 주요 엔티티
- User: 사용자 정보, 포인트, 프로필 이미지, 역할
- Board: 게시글 정보, 프리미엄 여부
- Reply: 댓글 정보
- Purchase: 구매 내역
- UserRole: 사용자 역할 (ADMIN, USER)

### 연관관계
- User 1:N Board
- User 1:N Reply
- User 1:N Purchase
- Board 1:N Reply
- Board 1:N Purchase

## 실행 방법

### 환경 요구사항
- Java 17 이상
- MySQL 8.0 이상 (또는 H2 Database)
- Gradle

### 환경 변수 설정
다음 환경 변수를 설정해야 합니다:

```bash
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
TENCO_KEY=your_tenco_key
```

### 데이터베이스 설정
`src/main/resources/application-dev.yml` 파일에서 데이터베이스 연결 정보를 수정합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myblog?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: root
    password: root
```

### 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

또는 IDE에서 `DemoSsrV11Application.java`를 실행합니다.

### 접속
- 애플리케이션: http://localhost:8080
- H2 콘솔: http://localhost:8080/h2-console

## 주요 엔드포인트

### 사용자
- `GET /join` - 회원가입 화면
- `POST /join` - 회원가입 처리
- `GET /login` - 로그인 화면
- `POST /login` - 로그인 처리
- `GET /logout` - 로그아웃
- `GET /user/kakao` - 카카오 소셜 로그인 콜백
- `GET /user/detail` - 마이페이지
- `GET /user/update` - 회원정보 수정 화면
- `POST /user/update` - 회원정보 수정 처리

### 게시판
- `GET /` 또는 `GET /board/list` - 게시글 목록 (페이징)
- `GET /board/save` - 게시글 작성 화면
- `POST /board/save` - 게시글 작성 처리
- `GET /board/{id}` - 게시글 상세 조회
- `GET /board/{id}/update` - 게시글 수정 화면
- `POST /board/{id}/update` - 게시글 수정 처리
- `POST /board/{id}/delete` - 게시글 삭제
- `POST /board/{boardId}/purchase` - 유료 게시글 구매

### 댓글
- `POST /reply/save` - 댓글 작성
- `POST /reply/{id}/delete` - 댓글 삭제

### 관리자
- `GET /admin/dashboard` - 관리자 대시보드

## 주요 설계 특징

### 계층 구조
- Controller: HTTP 요청 처리 및 뷰 반환
- Service: 비즈니스 로직 처리
- Repository: 데이터 접근 계층
- Entity: 도메인 모델

### 보안
- 인터셉터를 통한 인증/인가 처리
- 세션 기반 사용자 인증
- BCrypt를 이용한 비밀번호 암호화
- 역할 기반 접근 제어

### 예외 처리
- 커스텀 예외 클래스 (Exception400, Exception401, Exception403, Exception404, Exception500)
- @ControllerAdvice를 통한 전역 예외 처리
- 사용자 친화적인 에러 페이지 제공

### 트랜잭션 관리
- @Transactional을 통한 트랜잭션 관리
- 읽기 전용 트랜잭션 최적화
- 더티 체킹을 통한 엔티티 상태 변경

### 성능 최적화
- JPA 배치 페치 사이즈 설정
- LAZY 로딩 전략
- 페이징 처리
- Open Session in View 비활성화

## 개발 환경 설정

### 프로파일
- `dev`: 개발 환경 (기본값)
- `local`: 로컬 환경
- `prod`: 운영 환경

### 로깅
- 개발 환경: DEBUG 레벨
- 프로덕션: INFO 레벨

### 파일 업로드
- 최대 파일 크기: 10MB
- 이미지 파일 저장 경로: `D:/uploads/`
- 웹 접근 경로: `/images/**`

## 초기 데이터

프로젝트 실행 시 `src/main/resources/db/data.sql` 파일을 통해 초기 데이터가 로드됩니다:
- 관리자 계정 1개 (admin/1234)
- 일반 사용자 계정 4개
- 게시글 10개
- 댓글 다수

## 라이선스

이 프로젝트는 데모 목적으로 제작되었습니다.

