package com.artinus.subscription.channel.adapter.out;

import com.artinus.subscription.channel.domain.ChannelType;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "channel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
