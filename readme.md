# ARTINUS Backend Engineer (5~8년) 과제

---

## 개요

- 구독 서비스 백엔드 API를 설계하고 구현하는 과제입니다.
- 도메인 설계, API 구현, LLM 연동, 외부 API 장애 대응, 그리고 해당 시스템을 클라우드 환경에 배포/운영하기 위한 아키텍처 설계를 포함합니다.

---

## 도메인 정의

### 구독 상태

- 회원은 아래 3가지 구독 상태 중 1개의 상태만 가질 수 있습니다.

| 상태      | 설명            |
|---------|---------------|
| 구독 안함   | 구독하지 않은 상태    |
| 일반 구독   | 일반 등급 구독 상태   |
| 프리미엄 구독 | 프리미엄 등급 구독 상태 |

### 채널

- 채널이란 구독 및 해지를 수행할 수 있는 창구(접점)를 의미합니다.
- 채널은 아래 3가지 타입으로 구분됩니다.

| 타입          | 구독 | 해지 |
|-------------|----|----|
| 구독/해지 모두 가능 | O  | O  |
| 구독만 가능      | O  | X  |
| 해지만 가능      | X  | O  |

채널 예시

- 구독 서비스의 가입 및 해지는 여러 채널을 통해 이루어질 수 있습니다.

| 채널   | 구독 | 해지 |
|------|----|----|
| 홈페이지 | O  | O  |
| 모바일앱 | O  | O  |
| 네이버  | O  | X  |
| SKT  | O  | X  |
| 콜센터  | X  | O  |
| 이메일  | X  | O  |

### 외부 API (csrng)

- 구독하기 API와 구독 해지 API는 외부 API를 호출하고, 응답 결과에 따라 트랜잭션을 처리합니다.
- 호출 예시
    ```shell
    curl -X GET https://csrng.net/csrng/csrng.php?min=0&max=1
    ```
- 응답 예시
    ```json
    [{ "status": "success", "min": 0, "max": 1, "random": 1 }]
    ```
- `random` 값에 따른 처리

  | random 값 | 처리 |
                |---|---|
  | `1` | 정상 처리 — 트랜잭션 커밋 |
  | `0` | 예외 발생 — 트랜잭션 롤백 |

---

## 요구사항

### 1. 구독하기 API

- 요청: 휴대폰번호, 채널 ID, 변경할 구독 상태
- 입력받은 채널이 구독 가능한 채널인 경우에만 구독할 수 있습니다.
- 최초 회원은 구독 안함, 일반 구독, 프리미엄 구독 중 어떤 상태로든 가입할 수 있습니다.
- 외부 API 호출 후, 응답에 따라 트랜잭션을 커밋 또는 롤백합니다.
- 구독 상태 변경 규칙

  | 현재 상태  | 변경 가능 상태              |
                |--------|-----------------------|
  | 구독 안함  | 일반 구독, 프리미엄 구독        |
  | 일반 구독  | 프리미엄 구독               |
  | 프리미엄 구독 | _(변경 불가)_             |

### 2. 구독 해지 API

- 요청: 휴대폰번호, 채널 ID, 변경할 구독 상태
- 입력받은 채널이 해지 가능한 채널인 경우에만 해지할 수 있습니다.
- 외부 API 호출 후, 응답에 따라 트랜잭션을 커밋 또는 롤백합니다.
- 해지 상태 변경 규칙

  | 현재 상태 | 변경 가능 상태 |
                |---|---|
  | 프리미엄 구독 | 일반 구독, 구독 안함 |
  | 일반 구독 | 구독 안함 |
  | 구독 안함 | _(변경 불가)_ |

### 3. 구독 이력 조회 API

- 요청: 휴대폰번호
- 응답:
    - 해당 회원의 구독하기, 구독해지 이력 목록(채널, 구독/해지날짜, 구독 상태 포함)
    - 이력 데이터를 기반으로 LLM API를 호출하여 생성한 자연어 요약
    - LLM API 선택은 자유입니다.
- 응답 예시:
    ```json
    {
      "history": [..],
      "summary": "2026년 1월 1일 홈페이지를 통해 일반 구독으로 가입한 뒤, 2월 1일 모바일앱에서 프리미엄 구독하였습니다. 3월 1일 콜센터를 통해 프리미엄 구독을 해지하여 구독 안함 상태입니다."
    }
    ```

### 기타

- 회원은 구독 및 해지를 여러 번 수행할 수 있습니다.
- 외부 API(csrng) 호출 시 발생할 수 있는 장애 상황에 대한 대응 전략을 구현해 주세요.
- 구현한 API 서버를 AWS 와 같은 클라우드 환경에 배포/운영한다고 가정하고, 아키텍처 및 구성, 보안, 확장성 등을 포함한 설계 문서를 포함해 주세요.
- 선택한 기술에 대한 근거, 분석 및 구현 내용 등은 `readme.md` 파일에 작성해 주세요.
- 요구사항에 명시되지 않은 부분은 일반적인 구독 서비스의 동작을 참고하여 자유롭게 구현해 주세요.

---

## 제약사항

- 언어, 프레임워크, 데이터베이스, 외부 API 등 모든 기술 선택에 제약이 없습니다.
- API Key 와 같은 인증 정보는 레포지토리에 포함되지 않도록 주의해 주세요.

---

## 평가 항목

- 아키텍처 설계 및 프로젝트 구성
- 요구사항 이해
- API 설계 및 구현
- 외부 API 장애 대응
- 클라우드 인프라 설계

---

## 제출 방법

- 안내 드린 마감일 전까지 github public repository URL을 아래 메일로 회신 부탁드립니다.
    - 메일: recruit@artinus.dev

---

## 클라우드 배포 아키텍처

### 기술 선택: AWS ECR + ECS Fargate + CodeDeploy

#### Amazon ECR (Elastic Container Registry)

| 장점          | 설명                               |
|-------------|----------------------------------|
| AWS 네이티브 통합 | IAM 기반 접근 제어로 별도 레지스트리 인증 서버 불필요 |
| 보안          | 이미지 취약점 자동 스캔, KMS 암호화 지원        |
| 고가용성        | AWS 관리형 서비스로 레지스트리 다운타임 없음       |
| 비용          | ECS와 같은 리전 사용 시 데이터 전송 비용 없음     |

**선택 이유:** Docker Hub 등 외부 레지스트리 대비 ECS와의 IAM 연동이 단순하고, VPC 내부 통신으로 이미지 풀 속도가 빠릅니다.

#### Amazon ECS Fargate

| 장점        | 설명                            |
|-----------|-------------------------------|
| 서버리스 컨테이너 | EC2 인스턴스 직접 관리 불필요            |
| 자동 스케일링   | CPU/메모리 기반 Auto Scaling 기본 지원 |
| 비용 효율     | 사용한 vCPU/메모리 시간만큼만 과금         |
| 보안 격리     | Task 단위로 네트워크/IAM 역할 격리       |
| 운영 부담 감소  | OS 패치, 노드 관리 불필요              |

**선택 이유:** EC2 기반 ECS 대비 인프라 관리 부담이 없고, 구독 서비스 특성상 트래픽 변동이 있을 때 Task 수 조절만으로 대응 가능합니다.

#### AWS CodeDeploy (Blue/Green 배포)

| 장점          | 설명                               |
|-------------|----------------------------------|
| 무중단 배포      | Blue/Green 전환으로 배포 중 다운타임 없음     |
| 빠른 롤백       | 문제 발생 시 이전 버전(Blue)으로 즉시 트래픽 전환  |
| 트래픽 제어      | Canary/Linear 방식으로 점진적 트래픽 이전 가능 |
| ECS 네이티브 통합 | ALB + ECS와 연동이 단순하고 안정적          |

**선택 이유:** 구독 서비스는 결제/상태 변경 등 중요 트랜잭션을 처리하므로 배포 중 서비스 중단이 있어서는 안 됩니다. Blue/Green 배포로 배포 위험을 최소화하고, 장애 시 롤백을 자동화합니다.

---

### 시스템 아키텍처

```mermaid
graph LR
    DEV(["DEVELOPER"])
    CLIENT(["CLIENT"])

    subgraph GITHUB ["GitHub Actions"]
        direction TB
        REPO(["Repository"])
        GA_C["deploy-customer-api.yml\nGradle · Docker Build"]
        GA_A["deploy-admin-api.yml\nGradle · Docker Build"]
        REPO --> GA_C
        REPO --> GA_A
    end

    subgraph AWS [" AWS "]
        ECR(["ECR\n:customer-api / :admin-api"])
        SM(["Secrets\nManager"])

        subgraph CD ["CodeDeploy"]
            CD_C["customer-api\nDeployment Group"]
            CD_A["admin-api\nDeployment Group"]
        end

        subgraph VPC ["VPC"]
            ALB(["ALB"])

            subgraph ECS_C ["ECS — customer-api :8080"]
                BLUE_C(["Blue Task"])
                GREEN_C(["Green Task"])
            end

            subgraph ECS_A ["ECS — admin-api :8081"]
                BLUE_A(["Blue Task"])
                GREEN_A(["Green Task"])
            end

            DB[("RDS\nMySQL")]
        end
    end

    CSRNG(["csrng\nExternal API"])
    LLM(["LLM API"])
    SLACK(["Slack"])
    DEV -->|" workflow_dispatch "| REPO
    GA_C -->|" Push :customer-api "| ECR
    GA_A -->|" Push :admin-api "| ECR
    GA_C -->|" Trigger "| CD_C
    GA_A -->|" Trigger "| CD_A
    CD_C -->|" Blue/Green "| ECS_C
    CD_A -->|" Blue/Green "| ECS_A
    ECR -.->|" Pull "| GREEN_C
    ECR -.->|" Pull "| GREEN_A
    SM -.->|" 환경변수 주입 "| GREEN_C
    SM -.->|" 환경변수 주입 "| GREEN_A
    ALB -->|" /api/v1/subscriptions/** "| ECS_C
    ALB -->|" /api/v1/admin/** "| ECS_A
    ECS_C --> DB
    ECS_A --> DB
    GREEN_C -->|" 랜덤값 요청 "| CSRNG
    GREEN_C -->|" 이력 요약 요청 "| LLM
    CLIENT -->|" HTTPS "| ALB
    CD_C -->|" 배포 결과 "| SLACK
    CD_A -->|" 배포 결과 "| SLACK
    style DEV fill: #4a4a4a, color: #fff, stroke: #333
    style CLIENT fill: #4a4a4a, color: #fff, stroke: #333
    style REPO fill: #24292e, color: #fff, stroke: #111
    style GA_C fill: #24292e, color: #fff, stroke: #111
    style GA_A fill: #24292e, color: #fff, stroke: #111
    style ECR fill: #FF9900, color: #fff, stroke: #cc7a00
    style ALB fill: #FF9900, color: #fff, stroke: #cc7a00
    style CD_C fill: #FF9900, color: #fff, stroke: #cc7a00
    style CD_A fill: #FF9900, color: #fff, stroke: #cc7a00
    style SM fill: #dd344c, color: #fff, stroke: #a0192c
    style BLUE_C fill: #2980b9, color: #fff, stroke: #1a5276
    style GREEN_C fill: #27ae60, color: #fff, stroke: #1e8449
    style BLUE_A fill: #2980b9, color: #fff, stroke: #1a5276
    style GREEN_A fill: #27ae60, color: #fff, stroke: #1e8449
    style DB fill: #00758f, color: #fff, stroke: #005f75
    style CSRNG fill: #6c757d, color: #fff, stroke: #555
    style LLM fill: #6c3483, color: #fff, stroke: #512e5f
    style SLACK fill: #4A154B, color: #fff, stroke: #2c0b2e
```

### 배포 흐름 (Blue/Green)

```mermaid
graph LR
    subgraph TRIGGER ["독립 배포 트리거 (workflow_dispatch)"]
        T_C(["deploy-customer-api.yml"])
        T_A(["deploy-admin-api.yml"])
    end

    subgraph REUSABLE ["_deploy-module.yml Reusable Workflow"]
        direction TB
        S1["① Gradle Build\n:module:build -x test"]
        S2["② Docker Build & Push\nECR :module 태그"]
        S3["③ CodeDeploy Trigger\ncode-deploy-module.yaml"]
        S4{"④ Health Check\nGET /actuator/health"}
        S1 --> S2 --> S3 --> S4
    end

    subgraph OK ["배포 성공"]
        direction TB
        LIVE(["Green Task\n트래픽 100%"])
        TERM(["Blue Task\n종료"])
        LIVE --> TERM
    end

    subgraph NG ["배포 실패 — 자동 롤백"]
        direction TB
        ROLLBACK(["Blue Task\n트래픽 재전환"])
        ALERT(["Slack\n장애 알림"])
        ROLLBACK --> ALERT
    end

    T_C -->|" module=customer-api "| S1
    T_A -->|" module=admin-api "| S1
    S4 -->|" HTTP 200 "| LIVE
    S4 -->|" 타임아웃 / 오류 "| ROLLBACK
    style T_C fill: #24292e, color: #fff, stroke: #111
    style T_A fill: #24292e, color: #fff, stroke: #111
    style S4 fill: #f39c12, color: #fff, stroke: #d68910
    style LIVE fill: #27ae60, color: #fff, stroke: #1e8449
    style TERM fill: #2980b9, color: #fff, stroke: #1a5276
    style ROLLBACK fill: #2980b9, color: #fff, stroke: #1a5276
    style ALERT fill: #4A154B, color: #fff, stroke: #2c0b2e
```

---

## 기술 선택 및 구현 결정 사항

### 헥사고날 아키텍처 (Ports & Adapters)

도메인 로직이 프레임워크·DB·외부 API에 직접 의존하지 않도록 `core` 모듈에 비즈니스 로직을 격리했습니다.

- **Port(인터페이스)** 를 통해 의존 방향을 도메인 안쪽으로만 향하게 제어
- JPA, Feign, Redis 등 구체 기술은 `infrastructure` 모듈에서만 구현 — 기술 교체 시 도메인 코드 무변경
- `core` 모듈 단위 테스트 시 DB 없이 Mock Port만으로 빠른 테스트 가능

```
common         ← 공통 예외, 유틸, 분산락 AOP
core           ← 도메인, UseCase, Port 인터페이스 (순수 자바)
infrastructure ← JPA Entity, FeignClient, Redis Adapter (Port 구현체)
customer-api   ← 고객용 REST Controller, 설정
admin-api      ← 관리자용 REST Controller, 설정
```

---

### 멀티 모듈 구성

단일 JAR로 구성하면 고객 API와 관리자 API 사이에 의도치 않은 빈 노출·경로 공유가 발생합니다.

- `customer-api`(포트 8080) / `admin-api`(포트 8081) 를 별도 애플리케이션으로 분리해 네트워크 레벨 접근 제어 가능
- 공통 도메인·인프라 코드는 모듈 의존성으로 재사용 — 중복 없이 관리
- 배포 파이프라인도 모듈별 독립 배포(`deploy-customer-api.yml` / `deploy-admin-api.yml`)

---

### Spring Cloud OpenFeign (외부 API 통합)

csrng, OpenAI 두 외부 API를 일관된 방식으로 호출합니다.

- 인터페이스 선언만으로 HTTP 클라이언트 생성 — RestTemplate 대비 보일러플레이트 코드 최소화
- Resilience4j CircuitBreaker/Retry 어노테이션과 자연스럽게 결합
- 타임아웃을 API 특성에 맞춰 세분화: csrng(connect 3s / read 3s), LLM(connect 10s / read 30s)

---

### Resilience4j CircuitBreaker + Retry (외부 API 장애 대응)

외부 API(csrng, LLM)는 언제든 지연·장애가 발생할 수 있습니다. 단순 재시도만으로는 장애 중 요청이 계속 누적되는 문제가 있습니다.

| 패턴                | 역할                                       |
|-------------------|------------------------------------------|
| `@Retry`          | 일시적 오류에 Exponential Backoff 재시도 (csrng 최대 3회, LLM 최대 2회) |
| `@CircuitBreaker` | 연속 실패율 50% 초과 시 서킷 OPEN → 즉시 fallback 반환 |

- try-catch 로 예외를 삼키면 Resilience4j가 실패로 인식하지 못하므로, **어댑터에서 예외를 직접 잡지 않고** 프레임워크가 감지하도록 설계
- CircuitBreaker fallback 은 설정값(`csrng.fallback-result`)으로 외부화 — 운영 중 롤백 정책 변경 시 재배포 불필요
- LLM fallback은 이력 요약 생성 실패 시 기본 안내 문구 반환으로 구독 조회 자체는 중단되지 않도록 처리

---

### Redisson 분산락 (`@DistributedLock`)

동일 휴대폰번호로 동시에 구독 변경 요청이 오면 둘 다 `NONE` 상태를 읽어 각각 회원 생성을 시도하는 **중복 회원 문제**가 발생합니다.

- `@DistributedLock(key = "#command.phoneNumber")` AOP로 **휴대폰번호 단위 Redis 락** 획득
- DB UNIQUE 제약으로도 막을 수 있지만 예외 처리가 복잡해지고 재시도 로직이 별도 필요 — 락으로 선제적 직렬화가 더 명확
- `leaseTime`을 30초로 설정한 이유: csrng 재시도 대기시간(최대 ~7초) + CircuitBreaker 대기 + DB 처리를 합산해 안전 마진 확보

---

### `@TransactionalEventListener(AFTER_COMMIT)` + `REQUIRES_NEW`

구독 이력을 같은 트랜잭션에서 저장하면 csrng 랜덤 롤백 시 이력도 함께 롤백되어 **"처리 시도 자체가 기록되지 않는"** 문제가 발생합니다.

- `AFTER_COMMIT`: 메인 트랜잭션이 **성공적으로 커밋된 후에만** 이벤트 실행 → 롤백된 요청은 이력 저장 미호출
- `REQUIRES_NEW`: AFTER_COMMIT 콜백은 활성 트랜잭션이 없는 상태이므로 신규 트랜잭션을 명시적으로 열어 JPA save가 트랜잭션 컨텍스트 안에서 실행되도록 보장
- 이력 저장 실패가 메인 비즈니스 로직에 영향을 주지 않도록 이벤트 핸들러 내부에서 예외를 잡아 로그만 기록 (Graceful Degradation)

---

### `@Modifying @Query` JPQL 업데이트 (더블 쿼리 방지)

기존 `updateStatus`가 `findByPhoneNumber` → `entity.setStatus()` → `save()` 패턴이었는데, 서비스 레이어에서 이미 회원을 조회했음에도 **어댑터에서 다시 SELECT** 가 발생했습니다.

```java
@Modifying(clearAutomatically = true)
@Query("UPDATE MemberJpaEntity m SET m.subscriptionStatus = :status WHERE m.phoneNumber = :phoneNumber")
void updateSubscriptionStatus(String phoneNumber, SubscriptionStatus status);
```

- UPDATE 단건 쿼리로 불필요한 SELECT 제거
- `clearAutomatically = true`로 1차 캐시 즉시 무효화 → 이후 같은 트랜잭션 내 조회 시 stale 데이터 방지
- `SaveMemberPort`(신규 회원 저장)와 `UpdateMemberStatusPort`(상태 변경)를 포트 레벨에서 분리 — 책임 명확화

---

### 공통 예외 처리 및 API 응답 표준화

모든 API 응답을 `ApiResponse<T>` 제네릭 래퍼로 통일합니다.

- `@RestControllerAdvice`로 `BusinessException`, `MethodArgumentNotValidException`, `MissingServletRequestParameterException` 등을 일관된 형태로 변환
- `{success, message, data}` 구조로 클라이언트 파싱 일관성 확보
- `ErrorCode` enum으로 에러 코드·HTTP 상태·메시지 키를 한 곳에서 관리 — 에러 추가 시 핸들러 수정 불필요

---

### 다국어 메시지 처리 (i18n)

에러 메시지를 `MessageSource`로 관리합니다.

- `Accept-Language` 헤더 기반으로 한국어 / 영어 메시지 자동 선택
- 메시지 키(`error.channel_not_found` 등)를 `messages.properties` / `messages_en.properties`에서 관리 — 코드 변경 없이 메시지 수정 가능

---

### MapStruct 매핑

JPA Entity ↔ Domain 객체 변환을 수작업으로 관리하면 필드 추가 시 누락 위험이 있습니다.

- 컴파일 타임에 매핑 코드를 생성하므로 **런타임 리플렉션 없이** 타입 안전 변환
- Lombok Builder와 함께 사용 시 `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` 로 부분 업데이트도 안전하게 처리 가능

---

### Flyway DB 마이그레이션

스키마 변경을 코드와 함께 버전 관리합니다.

- `V1__init.sql` → 초기 스키마, `V2__insert_channels.sql` → 채널 기초 데이터 등 순번으로 이력 관리
- 애플리케이션 기동 시 자동 적용 → 환경별(local/staging/prod) 스키마 불일치 방지
- `ddl-auto: validate`로 Entity와 실제 스키마 불일치를 기동 시점에 즉시 감지

---

### Actuator 헬스체크

`/actuator/health` 엔드포인트를 노출해 Blue/Green 배포 흐름과 연결합니다.

- CodeDeploy 배포 시 새 Green Task의 헬스체크 통과 여부로 트래픽 전환 결정
- `registerHealthIndicator: true`로 Resilience4j CircuitBreaker 상태도 헬스 응답에 포함 — 운영 중 서킷 상태 모니터링 가능
