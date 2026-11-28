# 마음, 쉼

고민을 익명으로 나누는 웹서비스입니다. 게시글은 학교 MariaDB에 저장됩니다.

## 설치

JDK 17과 Node.js 20 이상이 필요합니다. 프로젝트를 클론한 뒤 루트에서 실행합니다.

```sh
npm --prefix frontend ci
```

## 실행

1. 팀에서 처음 DB를 준비할 때 [게시글 테이블 SQL](backend/backend/db/create-posts.sql)과 [작성자 테이블 SQL](backend/backend/db/add-post-authors.sql)을 순서대로 실행합니다.
2. `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`를 실행 환경에 설정하고 백엔드를 실행합니다.

   ```sh
   cd backend/backend
   SERVER_PORT=8083 ./gradlew bootRun
   ```

3. 새 터미널에서 프로젝트 루트로 이동해 프론트를 실행합니다.

   ```sh
   npm --prefix frontend run build
   npm --prefix frontend run dev
   ```

## 테스트

[프론트 화면](http://127.0.0.1:4178/)을 열어 글을 작성하고, 팀원이 같은 DB 설정으로 실행한 화면에서도 글이 보이는지 확인합니다. 글쓰기에는 [등록된 테스트 계정](backend/backend/README.md)이 필요하며, 글은 로그인 없이 볼 수 있습니다. API는 [Swagger](http://127.0.0.1:8083/swagger-ui/index.html)에서 확인할 수 있습니다.
