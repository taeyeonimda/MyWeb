# 기존에 만들었던 Spring Legacy프로젝트를 리팩토링 </br> SpringBoot, JPA 기타등등...
### SpringBoot, Java , Spring, SpringSecurity
###

# 회원기능
***
## * 구현 완료
### 1. 회원가입 & 로그인 기능
#### -> 카카오, 네이버, 구글 연동 로그인 구현완료 </br> 구글은 정책상 https를 사용해야해서 localhost로 도메인 변경 후 가능
### 2. 회원 정보 & 프로필 사진 수정 
#### -> 회원 정보 수정

## * 미완성 & 수정 해야할 목록
### 1. 비밀번호 찾기 
#### -> 기능은 구현했는데 디자인 및 추가 작업 필요 
***


# 채팅기능
***
## * 구현 완료  
### 1. 일반 채팅
### 2. 채팅 + 파일
### 3. 이전 채팅 목록 불러오기 
***
## * 미완성 & 수정 해야할 목록

### 1. HTML 디자인 (수정완료)
#### -> 카톡 처럼 한명이 여러번의 채팅을 쓸 경우 가장 위 채팅의 프로필 사진만 가져오지만 </br> 디자인 수정이 필요할듯 하다. 

### 2. 채팅없이 파일(사진)만 전송할 경우 전송되게하기 (수정완료)
#### -> 프론트단에서 보낼때 조건만 수정하면될것같다.

### 3. 세션관리 
#### -> 입장, 퇴장시 해당 메시지는 보내지만 예를들어 1번방에 몇명이있는지 같은걸 체크하고싶다.

### 4. 사진 미리보기 디자인
#### -> 파일 전송 전 사진을 미리 볼수있는 Preview를 만들었는데 디자인이 이상하다.

***



https://github.com/taeyeonimda/MyWeb/issues/3#issue-2450296093

<img src="./src/main/resources/gitImage/chat1.jpg">
<img src="./src/main/resources/gitImage/chat2.jpg">

# 게시판 기능
***
## * 구현 완료
### 1. 게시판 목록 불러오기
### 2. 페이징
### 3. 댓글 , 좋아요 수 같이 불러오기
### 4. 댓글 수정
### 5. 댓글 삭제
#### ==> 댓글에 대댓글이 있는 경우 삭제하지못하고 [삭제된 내용입니다] 라는 내용이 보이도록 ,<br> 대댓글이 없는 경우 삭제, 이미 [삭제된 내용입니다] 라고 변경된 경우 comDelete라는 새로운 필드를 만들어 <br> 체크 후 알람이 가게 구현
### 6. 게시물 조회시 조회수 증가
#### ==> Redis를 사용하여 게시물 조회시 게시물 ID, 회원 ID를 저장하여 조회수 중복증가 체크 <br> 해당 게시물을 읽은 뒤 1시간 뒤에 저장된 값이 사라져 조회수 증가가 가능하게 만듬
### 7. 게시물 작성 & 게시물 불러오기 수정 
#### ===> 작성시는 Summernote 라이브러리 사용 , 파일 첨부시 첨부파일 목록 및 다운로드 구현 <br>
### 8. 게시물 수정 & 삭제
#### ===> 게시물 수정시 첨부파일 삭제(저장시에만 실제로 삭제)
### 9. 게시물 SSE, 좋아요 기능 추가
#### ===> 게시물에 좋아요를 누를시 SSE를 활용하여 실시간 변경값 적용 
### 10. 게시판 댓글 관련 SSE로 리팩토링 
#### ===> 기존 댓글 등록 후 불러오는 과정에서 있던 중복 코드 제거를 위해 SSE로 구현 

***
## * 미완성 & 수정 해야할 목록

### 1. 디자인 & 검색기능
#### ===> 디자인
### 2. 게시물 댓글 관련 SSE로 리팩토링 하기 (완료)
#### ===> 실시간 같은 화면으로 불러오고자 중복된 JS가 많아서 코드가 복잡한 상태 
### 3. 게시물 댓글 & 대댓글 생길시 SSE로 알람 기능

<img src="./src/main/resources/gitImage/board1.jpg">
<img src="./src/main/resources/gitImage/board2.jpg">
<img src="./src/main/resources/gitImage/commentModi1.jpg">
<img src="./src/main/resources/gitImage/commentModi2.jpg">
<img src="./src/main/resources/gitImage/commentModi3.jpg">
<img src="./src/main/resources/gitImage/commentDel.jpg">
<img src="./src/main/resources/gitImage/redis.jpg">
<img src="./src/main/resources/gitImage/redis2.jpg">
<img src="./src/main/resources/gitImage/boardWrite1.jpg">
<img src="./src/main/resources/gitImage/boardFileDown.jpg">
<img src="./src/main/resources/gitImage/boardLikes1.jpg">
<img src="./src/main/resources/gitImage/boardLikes2.jpg">