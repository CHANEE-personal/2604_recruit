# MVC 아키텍처 리뷰 기준

## 계층 구분

| 계층 | 위치 | 설명 |
|---|---|---|
| Controller | `controller/` | HTTP 요청/응답 처리 |
| Service | `service/` | 비즈니스 로직 |
| Repository | `repository/` | 데이터 접근 |

## 검토 기준

- Controller → Service → Repository 계층 간 **책임 분리**가 명확한지 검토
- Controller에 비즈니스 로직이 침투하지 않았는지 확인
- Service 계층의 트랜잭션 관리가 적절한지 확인
- Repository 계층에서 불필요한 쿼리나 N+1 문제가 없는지 검토
- DTO ↔ Entity 변환이 적절한 계층에서 이루어지는지 확인
