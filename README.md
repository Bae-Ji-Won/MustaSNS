# 웹페이지 구현하기 Project

## 1. Swagger
(1) Local Url : http://localhost:8080/swagger-ui/
<br>
(2) Ec2 Url : http://ec2-44-199-204-56.compute-1.amazonaws.com:8080/swagger-ui/index.html <br><br>


## 2. 아키텍처
![dd](/uploads/de457db5be35cd2c2e098aab67775b85/dd.png)

## 3. ERD
<img src="/uploads/67f53a918ebedbbeae4fc4069db185e5/image.png" width="400"></img>

## 4. 개발 환경
- 에디터 : Intellij Ultimate
- 개발 툴 : SpringBoot 2.7.5
- 자바 : JAVA 11
- 빌드 : Gradle 6.8
- 서버 : AWS EC2
- 배포 : Docker
- 데이터베이스 : MySql 8.0
- 필수 라이브러리 : SpringBoot Web, MySQL, Spring Data JPA, Lombok, Spring Security


## 5. 서비스 종류
(1) 회원 인증,인가
- 모든 회원은 회원가입을 통해 회원이 됩니다.
- 로그인을 하지 않으면 SNS 기능 중 피드를 보는 기능만 가능합니다.
- 로그인한 회원은 글쓰기, 수정, 댓글, 좋아요, 알림 기능이 가능합니다.

(2) 글쓰기
- 포스트를 쓰려면 회원가입 후 로그인(Token받기)을 해야 합니다.
- 포스트의 길이는 총 300자 이상을 넘을 수 없습니다.
- 포스트의 한 페이지는 20개씩 보이고 총 몇 개의 페이지인지 표시가 됩니다.
- 로그인 하지 않아도 글 목록을 조회 할 수 있습니다.
- 수정 기능은 글을 쓴 회원만이 권한을 가집니다.
- 포스트의 삭제 기능은 글을 쓴 회원만이 권한을 가집니다.

(3) 피드
로그인 한 회원은 자신이 작성한 글 목록을 볼 수 있습니다.

(4) 댓글
- 댓글은 회원만이 권한을 가집니다.
- 글의 길이는 총 100자 이상을 넘을 수 없습니다.
- 회원은 다수의 댓글을 달 수 있습니다.

(5) 좋아요
- 좋아요는 회원만 권한을 가집니다.
- 좋아요 기능은 취소가 가능합니다.

(6) 알림
- 알림은 회원이 자신이 쓴 글에 대해 다른회원의 댓글을 올리거나 좋아요시 받는 기능입니다.
- 알림 목록에서 자신이 쓴 글에 달린 댓글과 좋아요를 확인할 수 있습니다.


## 6. EndPoint
https://gitlab.com/qowl880/finalproject-baejiwon/-/issues  [API별 세부 내용은 IssueS에 작성했습니다.] 



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
