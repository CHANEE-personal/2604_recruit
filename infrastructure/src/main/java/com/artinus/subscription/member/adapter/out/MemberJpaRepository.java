package com.artinus.subscription.member.adapter.out;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByPhoneNumber(String phoneNumber);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE MemberJpaEntity m SET m.subscriptionStatus = :status WHERE m.phoneNumber ="
            + " :phoneNumber")
    void updateSubscriptionStatus(@Param("phoneNumber") String phoneNumber,
            @Param("status") SubscriptionStatus status);
}
