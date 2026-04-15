# MapStruct 매핑 가이드

리뷰 대상에 **MapStruct 파일이 있을 경우에만** 적용한다.

## 매핑 대상

| 변환                       | MapStruct | 이유                       |
|--------------------------|-----------|--------------------------|
| JpaEntity ↔ Domain       | ✅         | 필드 수 많고 변환 복잡            |
| 외부 API Response → Domain | 상황에 따라    | Response DTO가 별도 존재하는 경우 |
| FeignClient JSON 역직렬화    | ❌         | Jackson이 직접 처리           |

## 필수 설정

```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
```

- `unmappedTargetPolicy = ERROR` — 매핑 누락 시 컴파일 에러로 조기 발견

## 체크리스트

- 무시할 필드 누락 확인 (`@Mapping(target = "id", ignore = true)`)
- snake_case JSON 필드는 `@JsonProperty` 로 처리 (MapStruct 책임 아님)
- 소스 null 시 대응 전략 정의 여부
- 부분 업데이트 시 `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)` 적용 여부
