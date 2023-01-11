# 웹페이지 구현하기 Project

## 1. Swagger
(1) Local Url : http://localhost:8080/swagger-ui/
<br>
(2) Ec2 Url : http://ec2-18-209-240-235.compute-1.amazonaws.com:8080/swagger-ui/index.html <br><br>


## 2. 아키텍처
![dd](/uploads/de457db5be35cd2c2e098aab67775b85/dd.png)

## 3. ERD
<img src="/uploads/7b35c8191e0e1b2efcd60b92f4ecf667/erd.png" width="400"></img>

## 4. 체크리스트
### 필수 과제
☑ 회원가입<br>
☑ Swagger<br>
☑ AWS EC2에 Docker 배포<br>
☑ Gitlab CI & Crontab CD<br>
☑ 로그인<br>
☑ 포스트 작성, 수정, 삭제, 리스트<br>
☑ 댓글 작성, 수정, 삭제, 리스트<br>
☑ 좋아요 누르기, 취소하기, 해당 게시물의 총 개수 구하기<br>
☑ 마이피드<br>
☑ 알림<br>
☑ Swagger에 ApiOperation을 써서 Controller 간단 설명하기<br>

### 도전 과제
❌ 화면 UI 개발 - 회원가입, 로그인, 글쓰기, 조회, 댓글, 좋아요, 알림<br>
☑ADMIN 회원으로 등급업하는 기능<br>
☑초기 ADMIN 회원은 하나가 존재하고 ADMIN 회원은 일반회원의 권한을 ADMIN으로 승격시킬 수 있다.<br>
☑ADMIN 회원이 일반 회원을 ADMIN으로 승격시키는 기능
    - POST /users/{id}/role/change
        - Body {”role”:”admin” | “user”} admin 또는 user로 변경할 수 있습니다.<br>
☑ ADMIN 회원이 로그인 시 자신이 쓴 글이 아닌 글과 댓글에 수정, 삭제를 할 수 있는 기능

## 5. 일차별 구현한 서비스 종류 
https://gitlab.com/qowl880/finalproject-baejiwon/-/issues<br> [일차별 세부 내용은 Issues에 작성했습니다.]

> #### 1일차
(1) Swagger
    - Swagger와 연동하였습니다.
    - Swagger를 통해 api 통신을 하였습니다.
    - 정리글 : https://velog.io/@qowl880/Rest-API-Swagger

(2) DOCKER를 통한 AWS EC2 배포
   - Docker file 추가
   - EC2 주소 연동
   - EC2를 통해 서버 배포
   - 정리글 : https://velog.io/@qowl880/Xshell%EB%A1%9C-Docker-%EC%84%A4%EC%B9%98%ED%9B%84-MYSQL-%EC%8B%A4%ED%96%89

(3) Gitlab CI/CD
   - Merge를 하게 되면 자동으로 Docker에서 해당 내용을 최신화하도록 함
   - 최신화 후 image를 반환하도록 함
   - 정리글 : https://velog.io/@qowl880/Gitlab-CICD

> #### 2일차
(1) 회원 인증,인가<br>- 모든 회원은 회원가입을 통해 회원이 됩니다.
- 로그인을 하지 않으면 SNS 기능 중 피드를 보는 기능만 가능합니다.
- 로그인한 회원은 글쓰기, 수정, 댓글, 좋아요, 알림 기능이 가능합니다.
- 정리글 : https://velog.io/@qowl880/SpringBoot4-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0-%EC%9D%B8%EA%B0%80-%EC%84%A4%EC%A0%95

> #### 3일차
(1) 글쓰기
   - 토큰을 받아 인가가 된 유저만 작성이 가능하도록 합니다
   - 예외처리


(2) 세부 게시물 출력
   - 게시물 Id를 입력받으면 해당 Id의 데이터를 불러와 출력시킵니다.

> #### 4일차
(1) 게시물 전체 출력
   - 모든 게시물을 출력합니다
   - 페이징을 통해 게시물을 20개씩 출력합니다

> #### 5일차
(1) Update 기능 구현
   - 게시물 Id와 토큰을 입력받아 해당 유저Id를 추출합니다
   - 게시물 userName과 토큰 userName이 같을시 수정할 수 있는 권한을 부여합니다.

(2) ADMIN 회원에게는 모든 권한 부여
   - ADMIN 회원에게는 모든 권한을 가질 수 있도록 설정하였습니다.

> #### 6일차
(1) Delete 기능 구현
   - 게시물 Id와 토큰을 입력받아 해당 유저Id를 추출합니다
   - 게시물 userName과 토큰 userName이 같을시 삭제할 수 있는 권한을 부여합니다.
   - 이때 Role이 ADMIN이라면 삭제가 가능합니다.

(2) TestCase
   - UserController TestCase 작성
   - PostController TestCase 작성
   - PostService TestCase 작성

> #### 7~8일차
(1) Comment 기능 구현
   - 댓글 목록 조회
      - 댓글 조회는 모든 회원이 권한을 가진다.
      - 제목, 글쓴이, 작성날짜가 표시된다.
      - 목록 기능은 페이징 기능이 포함된다.
         - 한 페이지당 default 피드 갯수는 10개이다.
         - 총 페이지 갯수가 표시된다.
         - 작성날짜 기준으로 최신순으로 sort한다.
   - 댓글 작성
      - 댓글 작성은 로그인 한 사람만 쓸 수 있습니다.
   - 댓글 수정 / 삭제
      - 댓글 수정은 댓글을 작성한 회원만이 권한을 가집니다.

> #### 8~9일차
(1) 좋아요 기능 구현
   - 좋아요 누르기
   - ‘좋아요’는 한번만 누를 수 있습니다. 중복으로 누르는 경우는 좋아요 해제됨
   - 해당 게시물에 대한 좋아요 총 개수 구하기

> #### 9~10일차
(1) 알림 기능 구현
   - 특정 User의 알람 목록을 받아옴
   - 특정 포스트에 새 댓글이 달리고, 좋아요가 눌리면 알람이 등록됨
   - Like, Comment Entity와 연관매핑 해줌

(2) 코드 리팩토링
   - 예외처리 하는 부분이 중보코드가 발생하여 따로 클래스를 만들어서 분리함

> #### 11~12일차
(1) Junit
   - PostController, PostService Test Code Refactoring
   - CommentService Test Code Add

## 6. EndPoint 

> 로그인

Api url : POST /api/v1/users/login
<br>ex) http://localhost:8080/api/v1/users/login

> 회원가입

Api url : POST /api/v1/users/join
<br>ex) http://localhost:8080/api/v1/users/join

> 관리자 권한 부여

Api url : POST /api/v1/users/{id}/role/change
<br>ex) http://localhost:8080/api/v1/users/{id}/role/change

> 게시물 상세 조회 기능

Api url : GET /api/v1/posts/{postsId}
<br>ex) http://localhost:8080/api/v1/posts/{postsId}

> 게시물 전체 조회 기능

Api url : GET /api/v1/posts
<br>ex) http://localhost:8080/api/v1/posts

> 게시물 작성

Api url : POST /api/v1/posts
<br>ex) http://localhost:8080/api/v1/posts

> 게시물 수정

Api url : PUT /api/v1/posts/{id}
<br>ex) http://localhost:8080/api/v1/posts/{id}

> 게시물 삭제

Api url : DELETE /api/v1/posts/{id}
<br>ex) http://localhost:8080/api/v1/posts/{id}

> 댓글 작성

Api url : POST /api/v1/posts/{postsId}/comments
<br>ex) http://localhost:8080/api/v1/posts/{postsId}/comments

> 댓글 리스트

Api url : GET /api/v1/posts/{postId}/comments
<br>ex) http://localhost:8080/api/v1/posts/{postId}/comments

> 댓글 수정

Api url : PUT /api/v1/posts/{postsId}/comments/{id}
<br>ex) http://localhost:8080/api/v1/posts/{postsId}/comments/{id}

> 댓글 삭제

Api url : DELETE /api/v1/posts/{postsId}/comments/{id}
<br>ex) http://localhost:8080/api/v1/posts/{postsId}/comments/{id}

> 좋아요 누르기

Api url : POST /api/v1/posts/{postId}/likes
<br>ex) http://localhost:8080/api/v1/posts/{postId}/likes

> 해당 게시물 좋아요 총 개수 출력

Api url : GET /api/v1/posts/{postId}/likes
<br>ex) http://localhost:8080/api/v1/posts/{postId}/likes

> 마이 피드

Api url : GET /api/v1/posts/my
<br>ex) http://localhost:8080/api/v1/posts/my

> 알림 리스트 출력

Api url : GET /api/v1/alarms
<br>ex) http://localhost:8080/api/v1/alarms

## 🙏 Commit 규칙

```jsx
소문자로 작성!
    git commit -m "[feat]갤러리 무한스크롤 기능 구현"
```

|  | 유형 | 설명 |
| ------ | ------ | ------ |
| 🚀 | feat | 새로운 기능 추가, 수정 |
| 🐞 | fix | 버그 픽스, 에러 핸들링 |
| 🎉 | build | 빌드 & 배포 |
| 🙈 | chore | 패키지 외 기타 수정 |
| 📝 | docs | 문서 수정 |
| 🧹 | style | 코드 스타일, 포맷팅 변경 |
| 😎 | refactor | 리팩토링, 코드 개선 |
| 🚪 | release | 버전 릴리즈 |
| 🥪 | merge | 파일 병합 |
