# eolma-payment Development Guide

## 서비스 개요

결제 처리 및 정산 관리 서비스. TossPayments API를 통한 결제 연동.

- 포트: 8084
- 프레임워크: Spring MVC (Servlet)
- DB: PostgreSQL (`eolma_payment`), JPA
- 외부 API: TossPayments (테스트 모드)

## 핵심 도메인

### Payment 엔티티

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| auctionId | Long | 경매 ID |
| productId | Long | 상품 ID |
| buyerId | Long | 구매자 ID (낙찰자) |
| sellerId | Long | 판매자 ID |
| amount | Long | 결제 금액 (원) |
| status | PaymentStatus | 결제 상태 |
| tossPaymentKey | String | Toss 결제 키 (nullable) |
| tossOrderId | String | Toss 주문 ID |
| paymentMethod | String | 결제 수단 (nullable) |
| confirmedAt | LocalDateTime | 결제 확인 시각 |
| cancelledAt | LocalDateTime | 취소 시각 |
| failureReason | String | 실패 사유 |
| deadlineAt | LocalDateTime | 결제 기한 (생성 + 24시간) |

### 결제 상태 머신

```
PENDING → CONFIRMED
   ↓         ↓
EXPIRED   REFUNDED
   ↓
(종료)
```

- **PENDING**: 결제 대기 (경매 낙찰 시 자동 생성)
- **CONFIRMED**: 결제 완료 (Toss 결제 확인)
- **EXPIRED**: 기한 만료 (24시간 내 미결제)
- **CANCELLED**: 결제 취소
- **REFUNDED**: 환불

### Settlement 엔티티

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| paymentId | Long | 결제 ID |
| sellerId | Long | 판매자 ID |
| totalAmount | Long | 결제 총액 |
| feeRate | BigDecimal | 수수료율 (3.5%) |
| feeAmount | Long | 수수료 금액 |
| settlementAmount | Long | 정산 금액 |
| status | SettlementStatus | PENDING / SETTLED |

## API 엔드포인트

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| GET | `/api/v1/payments/auction/{auctionId}` | O | 경매별 결제 조회 |
| POST | `/api/v1/payments/confirm` | O | 결제 확인 (Toss 연동) |
| POST | `/api/v1/payments/{id}/cancel` | O | 결제 취소 |
| GET | `/api/v1/payments/me` | O | 내 결제 목록 |
| GET | `/api/v1/payments/{id}` | O | 결제 상세 조회 |

## 결제 흐름

```
1. [Auction] --AUCTION_COMPLETED--> [Payment] 결제 생성 (PENDING, 24시간 deadline)
2. [Frontend] Toss 결제 위젯으로 결제 진행
3. [Frontend] 결제 성공 콜백 -> POST /api/v1/payments/confirm
4. [Payment] Toss API로 결제 확인 (confirmPayment)
5. [Payment] Payment 상태 CONFIRMED, Settlement 생성
6. [Payment] PAYMENT_CONFIRMED 이벤트 발행
```

## TossPayments 연동

### TossPaymentsClient
- `PaymentGateway` 포트 인터페이스 구현
- Base URL: `https://api.tosspayments.com/v1`
- 인증: Basic Auth (Secret Key base64 인코딩)
- 테스트 키 사용 (실제 결제 발생 안 함)

### 주요 API
- `POST /payments/confirm` - 결제 확인 (paymentKey, orderId, amount)
- `POST /payments/{paymentKey}/cancel` - 결제 취소

### 에러 처리
Toss API 에러 응답을 파싱하여 적절한 EolmaException으로 변환.

## UseCase 목록

| UseCase | 설명 | 이벤트 |
|---------|------|--------|
| CreatePaymentUseCase | AUCTION_COMPLETED 수신 -> 결제 생성 | - |
| ConfirmPaymentUseCase | Toss 결제 확인, 정산 생성 | PAYMENT_CONFIRMED 발행 |
| CancelPaymentUseCase | 결제 취소 | PAYMENT_CANCELLED 발행 |
| ExpirePaymentUseCase | 만료 결제 처리 | PAYMENT_EXPIRED 발행 |

## 스케줄러

`PaymentDeadlineScheduler`: 60초마다 실행
- deadlineAt이 현재 시각 이전인 PENDING 결제를 조회
- `ExpirePaymentUseCase`로 만료 처리

## Kafka 이벤트

**수신 (`eolma.auction.events`):**
- `AUCTION_COMPLETED` -> 결제 생성 (IdempotencyChecker 사용)

**발행 (`eolma.payment.events`):**
- `PAYMENT_CONFIRMED`: 결제 확인 시
- `PAYMENT_FAILED`: 결제 실패 시
- `PAYMENT_EXPIRED`: 결제 만료 시
- `PAYMENT_CANCELLED`: 결제 취소 시

## 설정값

- 수수료율: 3.5% (`payment.commission.fee-rate`)
- 결제 기한: 24시간 (`payment.deadline-hours`)

## 주의사항

- 결제 확인 시 금액 검증 필수 (프론트에서 보낸 amount와 DB amount 비교)
- tossOrderId는 결제 생성 시 고유하게 생성 (UUID 기반)
- 결제 접근 권한: 구매자 또는 판매자만 조회/취소 가능
