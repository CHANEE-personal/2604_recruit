# 패키지/파일 생성 가이드

## 패키지 구조 템플릿

```
src/main/java/com/artinus/subscription/{feature}/
├── adapter/
│   ├── in/
│   │   ├── {Feature}Controller.java            # public  — REST API 엔드포인트
│   │   └── {Feature}Request.java               # default — 요청 DTO, toCommand() 포함
│   └── out/
│       ├── {Feature}JpaEntity.java             # default — JPA 엔티티
│       ├── {Feature}JpaRepository.java         # default — Spring Data Repository
│       ├── {Feature}Mapper.java                # default — MapStruct 매핑
│       ├── {Feature}PersistenceAdapter.java    # default — Port Out 구현체 (영속성)
│       ├── {Feature}FeignClient.java           # default — OpenFeign 인터페이스
│       ├── {Feature}FeignConfig.java           # default — Feign 설정 (인증 등)
│       ├── {Feature}ApiAdapter.java            # default — Port Out 구현체 (외부 API)
│       ├── {Feature}Request.java               # default — 외부 API 요청 DTO
│       └── {Feature}Response.java              # default — 외부 API 응답 DTO
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── {Feature}UseCase.java           # public — 저장/수정 Port In
│   │   │   ├── Get{Feature}Query.java          # public — 조회 Port In
│   │   │   └── {Action}{Feature}Command.java   # public — 입력 객체, validate() 포함
│   │   └── out/
│   │       ├── Load{Feature}Port.java          # public — 조회 Port Out
│   │       ├── Save{Feature}Port.java          # public — 저장 Port Out
│   │       └── Get{External}Port.java          # public — 외부 API Port Out
│   └── service/
│       ├── {Feature}Service.java               # default — UseCase 구현체
│       └── Get{Feature}Service.java            # default — Query 구현체
└── domain/
    ├── {Feature}.java                          # public — 도메인 모델
    └── {Feature}Status.java                    # public — 도메인 열거형 (필요 시)
```

## 생성 원칙

- 외부 API 연동 없으면 `FeignClient`, `FeignConfig`, `ApiAdapter` 생략
- 조회 전용이면 `UseCase`, `SavePort` 생략
- 도메인 간 공유 로직은 `common/` 패키지로 분리
