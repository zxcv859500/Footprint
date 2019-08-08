# Footprint RESTFUL API  
  
### ENDPOINT : 203.254.143.185:3000 

### **요청 시 Auth 필요: x-access-token에 토큰 삽입 필요**
  
  
#### 로그인
**Request**  
>POST /api/auth/login
```json
{
  "username": "example",
  "password": "password"
}
```
  
**Response**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Inp4Y3Y4NTk1MDAiLCJuaWNrbmFtZSI6InNlYWd1bGwiLCJpYXQiOjE1NjMxOTAxNjUsImV4cCI6MTU2Mzc5NDk2NX0.qTjVOENdOXw5DgCFMSq14D2k7SOVqAsBCo3pQtQ-p7c"
}
```

#### 회원가입  
**Request**
>POST /api/auth/register  
```json
{
  "username": "example",
  "password": "password",
  "nickname": "nickname",
  "phone": "010-0000-0000"
}
```

**Response**
```json
{
  "message": "Register success"
}
```

#### 아이디 찾기
**Request**
>POST /api/auth/find/username
```json
{
  "phone": "010-0000-0000"
}
```

**Response**
```json
{
  "username": "exampleusername"
}
```

#### 비밀번호 찾기 인증 (휴대폰 번호 가져오기)
**Request**
>POST /api/auth/find/password/verify
```json
{
  "username": "exampleusername"
}
```

**Response**
```json
{
  "phone": "010-0000-0000"
}
```

#### 비밀번호 찾기 (비밀번호 변경)
**Request**
>POST /api/auth/find/password
```json
{
  "username": "exampleusername",
  "newPassword":"NeWpAsSwOrD"
}
```

**Response**
```json
{
  "username": "exampleusername",
  "password": "NeWpAsSwOrD"
}
```

### 마커  

#### 전체 마커 리스트  
**Request**
>GET /marker/list  

**Response**
```json
[
    {
        "latitude": "60.2",
        "longitude": "70.4",
        "type": 2
    },
    {
        "latitude": "102.293",
        "longitude": "25.923",
        "type": 0
    }
]
```

### 게시글
#### 글쓰기 
**Request (요청 시 Auth 필요)**
>POST /api/post/write  

>FORM-DATA  
title="글 제목"  
content="글 내용"  
latitude="위도"  
longitude="경도"  
road="도로명"  
type="글 타입 (A, B, C)"  
picture="사진 파일"  

**Response**
```json
{
  "title": "글 제목",
  "content": "글 내용",
  "latitude": "위도",
  "longitude": "경도",
  "road": "도로명",
  "type": "글 타입",
  "author": "작성자 닉네임",
  "date": "글 작성 날짜"
}
```
#### 해당 위도, 경도의 글 리스트
**Request**
>POST /api/post/list
```json
{
  "latitude": "70.2",
  "longitude": "60.4"
}
```
**Response**
```json
[
    {
        "postId": 38,
        "title": "test title",
        "author": "seagull",
        "pictureId": 29,
        "like": 0,
        "date": "2019-07-16T08:59:25.000Z",
        "type": 1
    },
    {
        "postId": 39,
        "title": "edit test title",
        "author": "seagull",
        "pictureId": 30,
        "like": 0,
        "date": "2019-07-16T10:26:46.000Z",
        "type": 1
    },
    {
        "postId": 40,
        "title": "edit test title",
        "author": "seagull",
        "pictureId": 31,
        "like": 0,
        "date": "2019-07-16T10:32:50.000Z",
        "type": 0
    },
    {
        "postId": 35,
        "title": "test title",
        "author": "seagull",
        "pictureId": 26,
        "like": 0,
        "date": "2019-07-16T06:06:44.000Z",
        "type": 1
    },
    {
        "postId": 36,
        "title": "test title",
        "author": "seagull",
        "pictureId": 27,
        "like": 0,
        "date": "2019-07-16T06:06:51.000Z",
        "type": 1
    },
    {
        "postId": 37,
        "title": "test title",
        "author": "seagull",
        "pictureId": 28,
        "like": 0,
        "date": "2019-07-16T06:07:28.000Z",
        "type": 2
    }
]
```
#### 해당 글 번호의 글 가져오기
**Request**
>GET /api/post/:id  

**Response**
```json
{
    "title": "test title",
    "content": "test content",
    "author": "seagull",
    "pictureId": 29,
    "like": 0,
    "date": "2019-07-16T08:59:25.000Z",
    "comments": []
}
```

#### 해당 글 번호의 글 삭제
**Request(요청 시 Auth 필요)**
>GET /api/post/:id/delete

**Response**
```json
{
  "message": "Delete complete"
}
```

### 사진
#### 해당 번호의 사진 가져오기
**Request**
>GET /api/picture/:id

**Response**  
사진 파일  

### 댓글
#### 댓글 쓰기
**Request (요청 시 Auth 필요)**
>POST /api/comment/:postId/write
```json
{
  "content": "댓글 내용입니다"
}
```

**Response**
```json
{
  "nickname": "닉네임",
  "postId": "42",
  "content": "댓글입니다."
}
```

#### 댓글 가져오기
**Request**
>GET /api/comment/:postId

**Response**
```json
{
    "result": [
        {
            "date": "2019-08-08T07:12:01.000Z",
            "content": "댓글입니다",
            "commentId": 4,
            "nickname": "seagull"
        },
        {
            "date": "2019-08-08T07:12:01.000Z",
            "content": "댓글입니다2",
            "commentId": 5,
            "nickname": "seagull"
        }
    ]
}
```

#### 댓글 삭제
**Request (요청 시 Auth 필요)**
>GET /api/comment/:commentId/delete

**Response**
```json
{
  "message": "Delete complete"
}
```