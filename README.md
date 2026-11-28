# 마음, 쉼

고민을 익명으로 나누고 개인 상담을 체험하는 팀 프로젝트입니다. 공유 게시판의 글은 Spring Boot API를 통해 MariaDB에 저장됩니다.

## 설치

- Git
- JDK 17
- Node.js 20 이상과 npm
- 팀에서 사용할 MariaDB 접속 정보

팀 저장소를 포크하거나 클론한 뒤 프로젝트 루트에서 아래 명령을 실행합니다. Gradle은 백엔드에 포함된 Wrapper를 사용합니다.

```sh
git clone https://github.com/WebService-TeamProject/2026_WebService_TeamProject.git
cd 2026_WebService_TeamProject
npm --prefix frontend ci
```

## 공유 게시판 실행

1. 팀이 사용할 MariaDB 스키마에 [게시글 테이블 SQL](backend/backend/db/create-posts.sql)과 [작성자 테이블·외래 키 SQL](backend/backend/db/add-post-authors.sql)을 순서대로 실행합니다. 스키마 준비는 한 명이 해도 됩니다.
2. Spring Boot 실행 환경에 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`를 설정합니다. 이 값은 Git에 올리지 않습니다. 아래 명령은 이 변수들이 설정된 터미널에서 실행합니다. IntelliJ를 사용한다면 `BackendApplication` 실행 설정의 환경 변수에 DB 변수 세 개와 `SERVER_PORT=8083`을 넣습니다.

   ```sh
   cd backend/backend
   SERVER_PORT=8083 ./gradlew bootRun
   ```

3. 새 터미널을 열고 프로젝트 루트에서 프론트를 빌드하고 실행합니다.

   ```sh
   npm --prefix frontend run build
   npm --prefix frontend run dev
   ```

4. 브라우저에서 [공유 게시판](http://127.0.0.1:4178)을 엽니다. 목록과 상세는 로그인 없이 볼 수 있습니다. 글을 작성하려면 [게시판 계정 등록 방법](backend/backend/README.md)에 따라 등록한 이메일과 비밀번호로 로그인합니다. 브라우저를 새로고침하면 다시 로그인합니다.
5. 팀원도 각자 같은 코드를 실행하고 백엔드에 **같은 MariaDB 스키마**를 지정합니다. 한 명이 글을 작성한 뒤 다른 사람이 목록을 새로고침하면 같은 글을 볼 수 있습니다.

프론트는 기본적으로 `8083`의 로컬 백엔드로 게시글 요청을 전달합니다. 다른 포트를 쓰는 경우 `BACKEND_PORT=8084 npm --prefix frontend run dev`처럼 실행합니다. [Swagger](http://127.0.0.1:8083/swagger-ui/index.html)는 백엔드 API를 따로 확인할 때 사용합니다.

## 테스트

```sh
npm --prefix frontend test
cd backend/backend
./gradlew test
```

백엔드 테스트에는 위와 같은 DB 환경 변수가 필요합니다. 공유 게시판에서는 새 글 작성, 별도 브라우저에서 조회, 작성자로 수정·삭제를 확인합니다.

로컬 시연 화면은 [데모 모드](http://127.0.0.1:4178/?demo)로 열 수 있습니다. 공유 게시판의 댓글·공감·신고·상담 기록은 브라우저에 저장되며, AI 답변은 예시입니다.
