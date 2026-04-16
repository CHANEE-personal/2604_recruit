package com.artinus.subscription.member.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class MemberPersistenceAdapterTest {

    @InjectMocks
    private MemberPersistenceAdapter adapter;

    @Mock
    private MemberJpaRepository memberJpaRepository;
    @Mock
    private MemberMapper memberMapper;


    @Test
    @DisplayName("전화번호로 회원을 조회한다")
    void findByPhoneNumber_found() {
        MemberJpaEntity entity = MemberJpaEntity.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();
        Member member = Member.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();

        given(memberJpaRepository.findByPhoneNumber("01012345678")).willReturn(Optional.of(entity));
        given(memberMapper.toDomain(entity)).willReturn(member);

        Optional<Member> result = adapter.findByPhoneNumber("01012345678");

        assertThat(result).isPresent();
        assertThat(result.get()
                .getPhoneNumber()).isEqualTo("01012345678");
    }


    @Test
    @DisplayName("존재하지 않는 전화번호로 조회하면 빈 Optional을 반환한다")
    void findByPhoneNumber_not_found() {
        given(memberJpaRepository.findByPhoneNumber("01099998888")).willReturn(Optional.empty());

        Optional<Member> result = adapter.findByPhoneNumber("01099998888");

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("회원을 저장한다")
    void save_success() {
        Member member = Member.create("01012345678");
        MemberJpaEntity entity = MemberJpaEntity.builder()
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();
        Member saved = Member.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();

        given(memberMapper.toEntity(member)).willReturn(entity);
        given(memberJpaRepository.save(entity)).willReturn(entity);
        given(memberMapper.toDomain(entity)).willReturn(saved);

        Member result = adapter.save(member);

        assertThat(result.getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("상태를 업데이트한다")
    void updateStatus_success() {
        MemberJpaEntity entity = MemberJpaEntity.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();
        Member updated = Member.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.BASIC)
                .build();

        given(memberJpaRepository.findByPhoneNumber("01012345678")).willReturn(Optional.of(entity));
        given(memberJpaRepository.save(entity)).willReturn(entity);
        given(memberMapper.toDomain(entity)).willReturn(updated);

        Member result = adapter.updateStatus("01012345678", SubscriptionStatus.BASIC);

        assertThat(result.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.BASIC);
    }


    @Test
    @DisplayName("존재하지 않는 회원 상태 업데이트 시 MEMBER_NOT_FOUND 예외가 발생한다")
    void updateStatus_member_not_found() {
        given(memberJpaRepository.findByPhoneNumber("01099998888")).willReturn(Optional.empty());

        assertThatThrownBy(
                () -> adapter.updateStatus("01099998888", SubscriptionStatus.BASIC)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.MEMBER_NOT_FOUND));
    }


    @Test
    @DisplayName("전체 회원 목록을 조회한다")
    void findAll_success() {
        MemberJpaEntity e1 = MemberJpaEntity.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();
        MemberJpaEntity e2 = MemberJpaEntity.builder()
                .id(2L)
                .phoneNumber("01099998888")
                .subscriptionStatus(SubscriptionStatus.BASIC)
                .build();
        Member m1 = Member.builder()
                .id(1L)
                .build();
        Member m2 = Member.builder()
                .id(2L)
                .build();

        given(memberJpaRepository.findAll()).willReturn(List.of(e1, e2));
        given(memberMapper.toDomain(e1)).willReturn(m1);
        given(memberMapper.toDomain(e2)).willReturn(m2);

        List<Member> result = adapter.findAll();

        assertThat(result).hasSize(2);
    }
}
