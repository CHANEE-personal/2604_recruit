# 헥사고날 아키텍처 리뷰 기준

> **헥사고날 아키텍처에 위배되면 안 되며, 최대한 DDD에 어긋나지 않게 구현되어야 한다.**

## 접근 제어자 규칙

| `public`                        | `default` (package-private)                                                                   |
|---------------------------------|-----------------------------------------------------------------------------------------------|
| Controller                      | JpaEntity, JpaRepository, Mapper, **PersistenceAdapter**, **ApiAdapter**, Service             |
| Port In/Out 인터페이스, Command, Domain | FeignClient, FeignConfig, Request/Response DTO, SubscriptionRequest                           |

> PersistenceAdapter / ApiAdapter 는 Port Out 인터페이스로만 접근하므로 `default` 가 올바르다.
> `public` 으로 열면 인터페이스를 우회한 구현체 직접 참조가 가능해져 헥사고날 원칙 위반.

## 의존 방향

```
Controller → Port In (UseCase/Query)
                  ↓
            Service → Port Out (Load/Save/Get)
                              ↓
               PersistenceAdapter / ApiAdapter
```

- Service는 Port Out 인터페이스에만 의존, Adapter 구현체 직접 참조 금지
- Controller는 Port In 인터페이스에만 의존, Service 구현체 직접 참조 금지

## Command 규칙

- `Request.toCommand()` 로 변환 후 UseCase 전달
- Command의 `validate()` — 도메인 사전 검증 (Bean Validation과 분리)
- 실패 시 `BusinessException(ErrorCode, Object... args)` 던짐

## 외부 API (OpenFeign) 규칙

- `@CircuitBreaker`, `@Retry` 는 FeignClient가 아닌 **ApiAdapter 메서드**에 선언
- 인증 헤더 등 공통 설정은 `FeignConfig` 클래스로 분리
- 폴백 메서드: 같은 클래스 내 `public fallback{Method}(Throwable)` 시그니처

## 파일 네이밍

| 위치                   | 파일명 패턴                                                                                                    |
|----------------------|-----------------------------------------------------------------------------------------------------------|
| adapter/in           | `{Domain}Controller`, `{Domain}Request`                                                                   |
| adapter/out (영속성)    | `{Domain}JpaEntity`, `{Domain}JpaRepository`, `{Domain}Mapper`, `{Domain}PersistenceAdapter`              |
| adapter/out (외부 API) | `{Domain}FeignClient`, `{Domain}FeignConfig`, `{Domain}ApiAdapter`, `{Domain}Request`, `{Domain}Response` |
| application/port/in  | `{Action}UseCase`, `Get{Domain}Query`, `{Action}Command`                                                  |
| application/port/out | `Load{Domain}Port`, `Save{Domain}Port`, `Get{External}Port`                                               |
| application/service  | `{Action}Service`, `Get{Domain}Service`                                                                   |

## 매핑 규칙

> 상세는 `mapping-guide.md` 참조

- JpaEntity ↔ Domain 변환은 MapStruct Mapper 사용
- 외부 API Response → Jackson 역직렬화로 처리 (MapStruct 불필요)
