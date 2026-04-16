package com.artinus.subscription.subscription.adapter.out;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "subscription_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class SubscriptionHistoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private Long channelId;

    @Column(nullable = false, length = 100)
    private String channelName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus newStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
