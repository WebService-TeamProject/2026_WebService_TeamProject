# 게시글 API 실행

IntelliJ 실행 설정이나 터미널에 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`를 지정한다. 해당 MariaDB 스키마에 `db/create-posts.sql`, `db/add-post-authors.sql`을 차례대로 적용한다. 작성자가 없는 기존 게시글은 공개 조회할 수 있으며 수정·삭제 권한은 부여되지 않는다.

Swagger에서 쓸 테스트 계정은 `maeum_users`에 등록한다. `htpasswd -nBC 12 test@example.com`으로 테스트 비밀번호의 BCrypt 해시를 만든 뒤 출력에서 이메일과 콜론을 제외한 해시만 저장한다.

```sql
INSERT INTO maeum_users (email, password_hash)
VALUES ('test@example.com', '<BCrypt 해시>');
```

`./gradlew bootRun`으로 실행하고 `http://localhost:8080/swagger-ui/index.html`에 접속한다. 목록·상세 조회는 바로 실행할 수 있다. 작성·수정·삭제는 **Authorize**에서 테스트 계정 이메일과 비밀번호를 입력한 뒤 실행한다. 작성·수정 요청에는 `category`, `title`, `body`를 넣는다. 카테고리는 `진로`, `학업`, `관계`, `일상` 중 하나를 사용한다.

배포 환경에서는 HTTP Basic 자격 증명 전송에 HTTPS를 사용한다.
