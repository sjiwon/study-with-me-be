# 여기서 구해볼래? `Backend` (Refactoring)

> [Origin Project](https://github.com/kgu-capstone/study-with-me-be)

## 목차

1. [소개](#-소개)
2. [서비스 화면](#-서비스-화면)
3. [주요 챌린지](#-주요-챌린지)
4. [기술 스택](#-기술-스택)
5. [CI/CD Pipeline](#-cicd-pipeline)
6. [백엔드 요청 흐름도](#-백엔드-요청-흐름도)
7. [모니터링 구조도](#-모니터링-구조도)
8. [실행 방식](#-실행-방식)

<br>

## 🌙 소개

#### 스터디 모집에서 진행 관리까지 케어하는 웹 애플리케이션 플랫폼

![1  설명](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/dede40ea-94be-4577-9674-d3e35b7cb6c0)

<br>

## 🖥 서비스 화면

### 스터디 찾기

![2  스터디 찾기](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/11c38c25-1dda-45ae-8239-67f5cf5894f3)

### 스터디 만들기

![3  스터디 생성](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/e19da0b7-c882-4401-9c15-06db9af517ab)

### 스터디 활동하기

![4  스터디 활동 (1)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/9b854301-c1b3-4151-abcf-0deccd7ec8e9)

![5  스터디 활동 (2)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/f44447cb-0d0a-4ec1-afe3-2f9064f5c53c)

![6  스터디 활동 (3)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/b586a8b8-6332-4077-ab5a-1f5f043fbcc9)

![7  스터디 활동 (4)](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/3c8a2502-6192-4dc4-8e64-31201769998c)

### 스터디 졸업하기

![8  스터디 졸업](https://github.com/kgu-capstone/study-with-me-be/assets/51479381/1e9527a1-d2a0-422b-90ff-d75322f1370e)

<br>

## 🔥 주요 챌린지

- [잦은 API Call이 예상되는 쿼리를 위한 Index 적용 성능 개선기](https://sjiwon.notion.site/Study-With-Me-b4e2f768c067433b9f2f84553af83067?pvs=4)
- [ContentCachingRequestWrapper가 Request 정보를 캐싱하지 못하는 문제 해결](https://sjiwon-dev.tistory.com/27)
- [스터디 조회 인수테스트 더미 데이터 관리 최적화 (with AfterAllCallback)](https://sjiwon-dev.tistory.com/29)
- [집계성 컬럼(favorite, review) 반정규화 진행 및 쿼리 튜닝을 통한 성능 개선](https://sjiwon-dev.tistory.com/38)
- [ThreadPool & HikariCP 최적화](https://sjiwon-dev.tistory.com/41)
- [WAS 서버 Scale Out 후 스케줄링 로직이 중복 실행되는 문제 해결](https://sjiwon-dev.tistory.com/46)
- [CompletableFuture를 활용한 N건 이미지 업로드 병렬 처리](https://sjiwon-dev.tistory.com/48)

<br>

## 🛠 기술 스택

### Backend

![Tech Stack - Backend](https://github.com/sjiwon/study-with-me-be/assets/51479381/70dc9df1-517c-45c2-b816-86edf2dc5c6b)

### Infra

![Tech Stack - Infra](https://github.com/sjiwon/study-with-me-be/assets/51479381/844a1bb9-1c0e-49d6-a34d-f9d036c0c8d7)

<br>

## 🚀 CI/CD Pipeline

![CI-CD Pipeline](https://github.com/sjiwon/study-with-me-be/assets/51479381/f61d7d35-d064-40ab-afe3-500b4463ac21)

<br>

## 🛒 백엔드 요청 흐름도

> Frontend는 리팩토링 하지 않았기 때문에 Backend 요청 흐름만 명시
> - 아래 흐름도에서 DNS Resolving 흐름은 생략

### 파일 업로드/요청 흐름도

![파일 업로드, 요청 흐름도](https://github.com/sjiwon/study-with-me-be/assets/51479381/ba19e1fb-4cbd-4a7a-9403-d4cb8bd2bb45)

### 서버 API 호출 흐름도

![서버 API 호출 흐름도](https://github.com/sjiwon/study-with-me-be/assets/51479381/38a9c4e2-46d9-47ed-955b-2cf48f8bef1c)

<br>

## 💻 모니터링 구조도

![모니터링 구조도](https://github.com/sjiwon/study-with-me-be/assets/51479381/cbed8c22-0a37-426b-bad2-eb3892b44cc3)

<br>

## 🚩 실행 방식

### 1) Docker Persistence(MySQL, Redis) & Monitoring(Prometheus, Grafana, Promtail, Loki)

- `docker/docker-compose-persistence.yml` 실행
- `docker/docker-compose-monitoring.yml` 실행

<br>

### 2) 프로필 환경설정 (resources/application.yml)

- `GOOGLE_EMAIL` = 구글 계정 이메일
- `GOOGLE_APP_PASSWORD` = 구글 앱 비밀번호
- `S3_BUCKET` = AWS S3 Bucket
- `CLOUD_FRONT_URL` = AWS CloudFront Domain URL

    - > S3 Credentials 관련 `{localUser}/.aws`에 AccessKey & SecretKey 정보 필수

- `OAUTH_GOOGLE_CLIENT_ID` = Google OAuth Application Client ID
- `OAUTH_GOOGLE_CLIENT_SECRET` = Google OAuth Application Client Secret
- `OAUTH_GOOGLE_REDIRECT_URI` = Google OAuth Application Redirect Uri
- `OAUTH_NAVER_CLIENT_ID` = Naver OAuth Application Client ID
- `OAUTH_NAVER_CLIENT_SECRET` = Naver OAuth Application Client Secret
- `OAUTH_NAVER_REDIRECT_URI` = Naver OAuth Application Redirect Uri
- `OAUTH_KAKAO_CLIENT_ID` = Kakao OAuth Application Client ID
- `OAUTH_KAKAO_CLIENT_SECRET` = Kakao OAuth Application Client Secret
- `OAUTH_KAKAO_REDIRECT_URI` = Kakao OAuth Application Redirect Uri
- `SLACK_WEBHOOK_URL` = Slack Webhook Url

#### (실행-1) 빌드된 JAR 파일 실행

```shell
java -jar \
    -Dfile.encoding=UTF-8 \
    -Dspring.mail.username="구글 계정 이메일" \
    -Dspring.mail.password="구글 앱 비밀번호" \
    -Dspring.cloud.aws.s3.bucket="AWS S3 Bucket" \
    -Dspring.cloud.aws.cloudfront.url="AWS CloudFront Domain URL" \
    -Doauth2.google.client-id="Google OAuth Application Client Id" \
    -Doauth2.google.client-secret="Google OAuth Application Client Secret" \
    -Doauth2.google.redirect-uri="Google OAuth Application Redirect Uri" \
    -Doauth2.naver.client-id="Naver OAuth Application Client Id" \
    -Doauth2.naver.client-secret="Naver OAuth Application Client Secret" \
    -Doauth2.naver.redirect-uri="Naver OAuth Application Redirect Uri" \
    -Doauth2.kakao.client-id="Kakao OAuth Application Client Id" \
    -Doauth2.kakao.client-secret="Kakao OAuth Application Client Secret" \
    -Doauth2.kakao.redirect-uri="Kakao OAuth Application Redirect Uri" \
    -Dslack.webhook.url="Slack Webhook Url" \
./build/libs/StudyWithMe.jar
```

#### (실행-2) IntelliJ 환경변수 설정 & 서버 ON

<br>

### [3) Swagger](http://localhost:8080/swagger-ui.html)

- API 테스트 시 필요한 `Token`은 `src/main/resources/DummyToken.txt`에서 사용

> Google OAuth `Authorization Code`를 파싱한 후 `/api/oauth/login/google` 요청 시 Authorization Code에 존재하는 `%2F -> /`로 수정
> - `%2F`는 HTTP URL Encoding으로 인한 결과로써 OAuth Provider가 인코딩된 값 인식 불가능
> - 4%2F0Adeu5BXgIJvUdjU090jAGQEwm8WPb8VidZzvmS9OjNFLrfeTIW9B-jGB292k5PRf73w4XA ->
    4/0Adeu5BXgIJvUdjU090jAGQEwm8WPb8VidZzvmS9OjNFLrfeTIW9B-jGB292k5PRf73w4XA
    >   - %2F -> /

<br>

### [4) REST Docs](http://localhost:8080/docs/index.html)

- Gradle `build` Task 실행 후 접속
    - build를 진행해야 REST Docs의 빌드된 index.html이 resources/static에 존재
