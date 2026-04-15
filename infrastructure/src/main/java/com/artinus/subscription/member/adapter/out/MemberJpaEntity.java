package com.artinus.subscription.member.adapter.out;

import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
class MemberJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus subscriptionStatus;


    public void updateStatus(SubscriptionStatus newStatus) {
        this.subscriptionStatus = newStatus;
    }
}
