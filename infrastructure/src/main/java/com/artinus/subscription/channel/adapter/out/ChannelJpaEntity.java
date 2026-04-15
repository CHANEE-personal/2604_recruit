package com.artinus.subscription.channel.adapter.out;

import com.artinus.subscription.channel.domain.enums.ChannelType;
import lombok.*;

import jakarta.persistence.*;

@Getter
@Entity
@Builder
@Table(name = "channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class ChannelJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType;
}
