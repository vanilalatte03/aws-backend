# API 명세서

## 기본 정보

- 프로젝트명: `aws-backend-ch4`
- 문서 작성일: 2026-05-26
- 기준 구현: `MemberController`, `MemberService`, `GlobalExceptionHandler`
- 로컬 Base URL: `http://localhost:8080`
- 운영 Base URL: `https://api.jiholim.pro`
- 인증 방식: 없음

## 공통 규칙

### Content-Type

| 요청 유형 | Content-Type |
| --- | --- |
| JSON 요청 | `application/json` |
| 파일 업로드 | `multipart/form-data` |

### 공통 에러 응답

예외 발생 시 다음 형식의 JSON 응답을 반환한다.

```json
{
  "message": "에러 메시지"
}
```

| HTTP Status | 발생 조건 |
| --- | --- |
| `400 Bad Request` | 요청 본문 유효성 검증 실패 |
| `404 Not Found` | 회원을 찾을 수 없거나, 프로필 이미지가 없는 경우 |
| `500 Internal Server Error` | 서버 내부 오류, S3 업로드 실패 등 |

## 응답 모델

### MemberResponse

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `id` | `number` | 회원 ID |
| `name` | `string` | 회원 이름 |
| `age` | `number` | 회원 나이 |
| `mbti` | `string` | 회원 MBTI |

예시:

```json
{
  "id": 1,
  "name": "jiholim",
  "age": 20,
  "mbti": "INTJ"
}
```

### ProfileImageResponse

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `url` | `string` | S3 Presigned URL |
| `expiresAt` | `string` | URL 만료 시각. ISO-8601 UTC 문자열 |

예시:

```json
{
  "url": "https://bucket-name.s3.ap-northeast-2.amazonaws.com/members/1/image.png?X-Amz-Algorithm=...",
  "expiresAt": "2026-06-02T03:00:00Z"
}
```

## 엔드포인트 목록

| Method | Path | 설명 |
| --- | --- | --- |
| `POST` | `/api/members` | 회원 생성 |
| `GET` | `/api/members/{id}` | 회원 단건 조회 |
| `POST` | `/api/members/{id}/profile-image` | 회원 프로필 이미지 업로드 |
| `GET` | `/api/members/{id}/profile-image` | 회원 프로필 이미지 다운로드 URL 발급 |
| `GET` | `/actuator/health` | 애플리케이션 Health Check |
| `GET` | `/actuator/info` | 애플리케이션 정보 조회. 운영 프로필에서 노출 |

## 회원 생성

회원을 생성한다.

```http
POST /api/members
Content-Type: application/json
```

### Request Body

| 필드 | 타입 | 필수 | 검증 조건 | 설명 |
| --- | --- | --- | --- | --- |
| `name` | `string` | Y | 공백 불가 | 회원 이름 |
| `age` | `number` | Y | `1` 이상 | 회원 나이 |
| `mbti` | `string` | Y | 공백 불가 | 회원 MBTI |

### Request Example

```json
{
  "name": "jiholim",
  "age": 20,
  "mbti": "INTJ"
}
```

### Response

- Status: `201 Created`
- Body: `MemberResponse`

```json
{
  "id": 1,
  "name": "jiholim",
  "age": 20,
  "mbti": "INTJ"
}
```

### Error

| HTTP Status | 조건 |
| --- | --- |
| `400 Bad Request` | `name`, `mbti`가 비어 있거나 `age`가 1보다 작은 경우 |
| `500 Internal Server Error` | 저장 처리 중 서버 오류가 발생한 경우 |

## 회원 단건 조회

회원 ID로 회원 정보를 조회한다.

```http
GET /api/members/{id}
```

### Path Variables

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `id` | `number` | Y | 회원 ID |

### Response

- Status: `200 OK`
- Body: `MemberResponse`

```json
{
  "id": 1,
  "name": "jiholim",
  "age": 20,
  "mbti": "INTJ"
}
```

### Error

| HTTP Status | 조건 |
| --- | --- |
| `404 Not Found` | 해당 ID의 회원이 없는 경우 |

## 프로필 이미지 업로드

회원의 프로필 이미지를 S3에 업로드하고, 회원의 프로필 이미지 키를 갱신한다. 응답에는 업로드된 이미지 URL이나 S3 키가 포함되지 않는다.

```http
POST /api/members/{id}/profile-image
Content-Type: multipart/form-data
```

### Path Variables

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `id` | `number` | Y | 회원 ID |

### Form Data

| 필드 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `file` | `file` | Y | 업로드할 프로필 이미지 파일 |

### cURL Example

```bash
curl -X POST \
  -F "file=@profile.png" \
  http://localhost:8080/api/members/1/profile-image
```

### Response

- Status: `201 Created`
- Body: `MemberResponse`

```json
{
  "id": 1,
  "name": "jiholim",
  "age": 20,
  "mbti": "INTJ"
}
```

### Error

| HTTP Status | 조건 |
| --- | --- |
| `404 Not Found` | 해당 ID의 회원이 없는 경우 |
| `500 Internal Server Error` | 파일 읽기 또는 S3 업로드에 실패한 경우 |

## 프로필 이미지 다운로드 URL 발급

회원의 프로필 이미지에 접근할 수 있는 S3 Presigned URL을 발급한다. URL은 요청 시점마다 새로 생성되며, 현재 구현 기준 만료 시간은 7일이다.

```http
GET /api/members/{id}/profile-image
```

### Path Variables

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `id` | `number` | Y | 회원 ID |

### Response

- Status: `200 OK`
- Body: `ProfileImageResponse`

```json
{
  "url": "https://member-profile-images-jh.s3.ap-northeast-2.amazonaws.com/members/1/example.png?X-Amz-Algorithm=...",
  "expiresAt": "2026-06-02T03:00:00Z"
}
```

### Error

| HTTP Status | 조건 |
| --- | --- |
| `404 Not Found` | 해당 ID의 회원이 없는 경우 |
| `404 Not Found` | 해당 회원에게 등록된 프로필 이미지가 없는 경우 |
| `500 Internal Server Error` | Presigned URL 생성 중 서버 오류가 발생한 경우 |

## Health Check

애플리케이션 상태를 확인한다.

```http
GET /actuator/health
```

### Response

- Status: `200 OK`

```json
{
  "status": "UP"
}
```

## Application Info

애플리케이션 정보를 조회한다. `prod` 프로필에서는 `/actuator/info`가 노출되며, 로컬 기본 설정에서는 `/actuator/health`만 노출된다.

```http
GET /actuator/info
```

### Response Example

```json
{
  "app": {
    "team-name": "jiholim"
  }
}
```

## 현재 미구현 API

다음 기능은 현재 컨트롤러에 구현되어 있지 않다.

- 회원 목록 조회
- 회원 정보 수정
- 회원 삭제
- 프로필 이미지 삭제
- 회원 인증 및 권한 검증
