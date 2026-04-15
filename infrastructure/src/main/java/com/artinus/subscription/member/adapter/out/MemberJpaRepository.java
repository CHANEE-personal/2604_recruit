package com.artinus.subscription.member.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByPhoneNumber(String phoneNumber);
}
