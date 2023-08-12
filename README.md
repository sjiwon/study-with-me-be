# 여기서 구해볼래? `Backend` (Refactoring)

- [Origin Project](https://github.com/kgu-capstone/study-with-me-be)


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

## 🛠 Tech Stacks
### Backend

![Backend](https://github.com/sjiwon/study-with-me-be/assets/51479381/e102ebe6-6790-407b-839b-7cfa0e539fab)

<br>

## ⚙️ Infrastructure

> TBU...

<br>

## 🔀 CI/CD Pipeline

> TBU...

<br>

## 🚩 실행 방식
### 1) MySQL DB & Redis

- `docker-compose.yml` 실행

### 2) API Server (Local Profile)

##### settings/application-mail.yml

- `GOOGLE_EMAIL` = 구글 계정 이메일
- `GOOGLE_APP_PASSWORD` = 구글 앱 비밀번호

##### settings/application-cloud.yml
- `S3_BUCKET` = AWS S3 Bucket

  - > S3 Credentials 관련 `{localUser}/.aws`에 AccessKey & SecretKey 정보 필수

##### settings/application-oauth.yml

- `OAUTH_GOOGLE_CLIENT_ID` = Google OAuth Application Client ID
- `OAUTH_GOOGLE_CLIENT_SECRET` = Google OAuth Application Client Secret
- `OAUTH_GOOGLE_REDIRECT_URI` = Google OAuth Application Redirect Uri
- `OAUTH_NAVER_CLIENT_ID` = Naver OAuth Application Client ID
- `OAUTH_NAVER_CLIENT_SECRET` = Naver OAuth Application Client Secret
- `OAUTH_NAVER_REDIRECT_URI` = Naver OAuth Application Redirect Uri
- `OAUTH_KAKAO_CLIENT_ID` = Kakao OAuth Application Client ID
- `OAUTH_KAKAO_CLIENT_SECRET` = Kakao OAuth Application Client Secret
- `OAUTH_KAKAO_REDIRECT_URI` = Kakao OAuth Application Redirect Uri

##### settings/application-external.yml
- `SLACK_WEBHOOK_URL` = Slack Webhook Url

#### (실행-1) 빌드된 JAR 파일 실행
```shell
java -jar \
    -Dfile.encoding=UTF-8 \
    -Dspring.mail.username="구글 계정 이메일" \
    -Dspring.mail.password="구글 앱 비밀번호" \
    -Dspring.cloud.aws.s3.bucket="AWS S3 Bucket" \
    -Doauth2.google.client-id="Google OAuth Application Client Id" \
    -Doauth2.google.client-secret="Google OAuth Application Client Secret" \
    -Doauth2.google.redirect-uri="Google OAuth Application Redirect Uri" \
    -Doauth2.naver.client-id="Naver OAuth Application Client Id" \
    -Doauth2.naver.client-secret="kakao OAuth Application Client Secret" \
    -Doauth2.naver.redirect-uri="kakao OAuth Application Redirect Uri" \
    -Doauth2.kakao.client-id="Kakao OAuth Application Client Id" \
    -Doauth2.kakao.client-secret="Kakao OAuth Application Client Secret" \
    -Doauth2.kakao.redirect-uri="Kakao OAuth Application Redirect Uri" \
    -Dslack.webhook.url="Slack Webhook Url" \
./build/libs/StudyWithMe.jar
```

#### (실행-2) IntelliJ 환경변수 설정 & 서버 ON

### [3) Swagger](http://localhost:8080/swagger-ui.html)

- API 테스트 시 필요한 `Token`은 `src/main/resources/DummyToken.txt`에서 사용

> Google OAuth `Authorization Code`를 파싱한 후 `/api/oauth/login/google` 요청 시 Authorization Code에 존재하는 `%2F -> /`로 수정
> - `%2F`는 HTTP URL Encoding으로 인한 결과로써 OAuth Provider가 인코딩된 값 인식 불가능
> - 4%2F0Adeu5BXgIJvUdjU090jAGQEwm8WPb8VidZzvmS9OjNFLrfeTIW9B-jGB292k5PRf73w4XA -> 4/0Adeu5BXgIJvUdjU090jAGQEwm8WPb8VidZzvmS9OjNFLrfeTIW9B-jGB292k5PRf73w4XA
>   - %2F -> /

### [4) REST Docs](http://localhost:8080/docs/index.html)

- Gradle `build` Task 실행 후 접속
