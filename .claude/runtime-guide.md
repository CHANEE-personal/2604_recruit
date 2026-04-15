# 런타임 검증 가이드

## Spring/프록시

- **self-invocation** — `@Transactional`, `@Cacheable` 등 프록시 패턴 위반 검토
- **@Transactional** — `readOnly` 여부, 전파 속성, Checked Exception 시 `rollbackFor` 설정, `try-catch`로 롤백 누락 여부
- **@Cacheable** — serialize/deserialize 에러 가능성
- **순환 참조** — 생성자 주입 시 Circular Dependency
- **Scope 오용** — Singleton에 Prototype 빈 주입
- Java 17 + Spring Boot 3.3.5 + Jakarta EE 기준 라이브러리 확인

## DB

- **동시성** — race condition, 데드락, 낙관적 락(`@Version`) 적용 여부, 비관적 락 범위 최소화
- **인덱스** — WHERE 조건 컬럼 인덱스 누락, 복합 인덱스 컬럼 순서가 쿼리 패턴과 일치하는지
- **커넥션 풀 고갈** — 긴 트랜잭션, N+1 쿼리, 트랜잭션 내 외부 API 호출 여부
- **Flyway** — `ddl-auto: validate` 유지, 컬럼 추가 시 `DEFAULT` 또는 `NULL` 허용 전략, 컬럼 삭제는 2단계(코드 제거 → 다음 배포에서 삭제), 마이그레이션 파일 내용
  수정 금지(체크섬 불일치)

## 비동기/스레드

- **@Async** — 커스텀 Executor 빈 이름 명시(`@Async("taskExecutor")`), 컬렉션 전달 시 방어적 복사
- **스레드 로컬 유실** — `@Async`/`CompletableFuture` 사용 시 `SecurityContext`, `MDC`, `LocaleContext` 유실 방지 (`TaskDecorator` 설정
  여부)
- **스레드 풀** — Graceful Shutdown 종료 정책, Task Rejection Policy(`AbortPolicy`/`CallerRunsPolicy`) 서비스 특성 적합성
- **스레드 안정성** — 공유 자원, mutable 상태 검토
