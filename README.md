# Repository & Project Convention

## 1. Git Branch 전략
- 기본 브랜치: `develop`  
- 새로운 기능 개발 시 `develop`에서 파생 브랜치 생성  
  - 브랜치명 규칙: `#이슈번호/기능명`  
  - 예: `#1/user-login`, `#24/order-service`  
- Pull Request(PR) 필수  
  - PR 생성 → 코드 리뷰 및 검토 → `develop`에 merge  
- 브랜치는 merge 후에도 **삭제하지 않는다** (추적 및 변경 이력 보존 목적)

---

## 2. 프로젝트 폴더 구조

### 최상위 구조
````code
src
├── domain
│ └── [Entity명]
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
└── global
````
### domain 규칙
- `domain`은 비즈니스 단위(Entity 기준)로 관리  
- Entity명 폴더 내부에 controller, entity, dto, repository, service 포함  
- Service와 Repository는 인터페이스를 우선 정의하고, 구현체는 `impl` 폴더에 둔다.  
  - 예:  
    - `UserService` (interface)  
    - `impl/UserServiceImpl` (구현체)  
- QueryDSL 사용 시 repository에서 `CustomRepository` + `Impl` 형태로 관리  
- 단일 Entity가 특정 부모 Entity에서만 사용된다면 부모 Entity 폴더에 함께 배치  
  - 예: `OrderItem`이 `Order`에서만 사용되는 경우 → `domain/order/entity/OrderItem.java`

---

## 3. Global 규칙

`global` 디렉토리는 **공통 모듈 및 전역 설정**을 담당한다.  
도메인과 직접적으로 연결되지 않는, 애플리케이션 전반에서 재사용되는 요소를 둔다.

### global 내부 구성
````code
global
├── config # 전역 환경 설정(Spring Config, Security, JPA 등)
├── exception # 예외 처리 (Custom Exception, GlobalExceptionHandler)
├── common # 공통 유틸, 상수, 공용 response wrapper 등
├── interceptor # Interceptor, Filter, AOP
├── annotation # Custom Annotation 정의
└── util # 문자열 처리, 날짜 변환, 암호화 등 범용 유틸
````

- **config**: 애플리케이션 레벨 설정(Spring Boot Config, CORS, Security, Swagger 등)  
- **exception**: 
  - `BusinessException`, `ErrorCode` 정의  
  - 전역 예외 핸들러(`@ControllerAdvice`)에서 API Response Convention 준수  
- **common**:  
  - `ApiResponse`, `Meta` 객체 등 API 응답 공통 구조  
  - 상수(`Constants`)와 enum 정의  
- **interceptor**: 요청/응답 로깅, 인증/인가 체크 등  
- **annotation**: 반복 코드 최소화를 위한 custom annotation (예: `@LogExecutionTime`)  
- **util**: 비즈니스 로직과 무관한 순수 유틸 클래스 모음  

---

# API Response Convention

## 1. 규칙

- 모든 API 응답은 동일한 포맷을 따른다.  
  - **성공 응답**: `data` + `meta`  
  - **실패 응답**: `code`, `message`, `status`, `details`, `meta`  
- `meta` 객체는 모든 응답에 포함된다.  
  - `timestamp`: ISO 8601 형식의 응답 생성 시각  
  - `request_id`: UUID, 로그 추적 및 디버깅에 활용  
- HTTP Status Code와 `status` 필드는 반드시 일치해야 한다.  
  - 예: HTTP 404 → `"status": "NOT_FOUND"`  
- 에러 메시지는 사용자 친화적으로 작성한다.  
  - 민감한 내부 정보는 `details`에 포함하고, 외부 응답에는 최소화  
- `INTERNAL_SERVER_ERROR`의 경우, `details`는 응답에 포함하지 않고 서버 로그에서만 확인 가능하게 처리한다.  

---

## 2. 응답 형식

### ✅ 성공 응답
```json
{
  "data": {
    "user_id": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "partner_id": 15,
    "partner_name": "길동상점",
    "category_ids": [2, 5, 7],
    "created_at": "2025-08-13T12:34:56"
  },
  "meta": {
    "timestamp": "2025-08-13T15:32:10.123",
    "request_id": "550e8400-e29b-41d4-a716-446655440000"
  }
}
````
---
### ❌ 실패 응답 (예: 유효성 검증 실패)
```json
{
  "code": "VALIDATION_ERROR",
  "message": "이메일 형식이 올바르지 않습니다.",
  "status": 422,
  "details": "email 필드 값이 RFC 5322 형식에 맞지 않음",
  "meta": {
    "timestamp": "2025-08-13T15:33:10.456",
    "request_id": "660e8400-e29b-41d4-a716-446655440111"
  }
}
````
### ❌ 실패 응답 (예: 서버 내부 오류, 민감 정보 미노출)
```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "message": "서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요.",
  "status": 503,
  "meta": {
    "timestamp": "2025-08-13T15:34:22.789",
    "request_id": "770e8400-e29b-41d4-a716-446655440222"
  }
}
````
### 주의: details는 서버에서만 볼 수 있게 로그만 남기고 클라이언트 응답에는 포함하지 않는다.
