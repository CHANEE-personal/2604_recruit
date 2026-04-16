# 테스트 가이드

## 테스트 종류 및 어노테이션

| 종류       | 어노테이션                                 | 대상         | DB             |
|----------|---------------------------------------|------------|----------------|
| 단위 테스트   | `@ExtendWith(MockitoExtension.class)` | Service    | 불필요            |
| 슬라이스 테스트 | `@WebMvcTest`                         | Controller | 불필요            |
| 통합 테스트   | `@SpringBootTest`                     | 전체 흐름      | H2 (test 프로파일) |

## 단위 테스트 원칙 (Service)

- Port Out 인터페이스를 `@Mock` 으로 주입, Service는 `@InjectMocks`
- Port In 인터페이스(UseCase/Query)는 직접 구현체(`SubscribeService` 등)를 테스트
- 외부 API, DB 의존성은 전부 Mock 처리
- `given(...).willReturn(...)` → `when` → `then` 순서 준수

```java
@ExtendWith(MockitoExtension.class)
class SubscribeServiceTest {

    @InjectMocks private SubscribeService subscribeService;
    @Mock private LoadMemberPort loadMemberPort;
    @Mock private SaveMemberPort saveMemberPort;
    // ...
}
```

## 슬라이스 테스트 원칙 (Controller)

- `@WebMvcTest(XxxController.class)` + `@Import({GlobalExceptionHandler.class, MessageConfig.class})`
- UseCase/Query Port In 인터페이스는 `@MockBean`
- 성공/실패/검증오류 케이스 모두 작성
- `jsonPath`로 응답 구조 검증

```java
@WebMvcTest(SubscriptionController.class)
@Import({GlobalExceptionHandler.class, MessageConfig.class})
class SubscriptionControllerTest {

    @MockBean SubscribeUseCase subscribeUseCase;
    // ...
}
```

## 테스트 프로파일

- `src/test/resources/application-test.yml` 사용 (`spring.profiles.active=test`)
- H2 인메모리 DB, `ddl-auto: create-drop`, Flyway 비활성화
- `build.gradle`에 `systemProperty 'spring.profiles.active', 'test'` 설정

## 테스트 메서드 네이밍

- `{행위}_{상황}_{결과}` 형식 (예: `subscribe_channel_not_found`)
- `@DisplayName` 에 한국어로 의도 명시 (예: `"채널을 찾을 수 없으면 예외가 발생한다"`)

## 테스트 작성 기준

| 작성 O                    | 작성 X                           |
|-------------------------|--------------------------------|
| 비즈니스 로직 분기 (성공/실패/예외)   | JpaEntity, JpaRepository (인프라) |
| Command `validate()` 검증 | DTO 단순 getter/setter           |
| 예외 타입 + ErrorCode 검증    | 외부 라이브러리 동작                    |
| HTTP 상태코드 + 응답 구조       | 이미 프레임워크가 보장하는 동작              |

## 예외 검증 패턴

```java
assertThatThrownBy(() -> subscribeService.subscribe(command))
    .isInstanceOf(BusinessException.class)
    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
        .isEqualTo(ErrorCode.CHANNEL_NOT_FOUND));
```
