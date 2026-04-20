ALTER TABLE subscription_history
    ADD CONSTRAINT fk_subscription_history_member
        FOREIGN KEY (member_id) REFERENCES member (id),
    ADD CONSTRAINT fk_subscription_history_channel
        FOREIGN KEY (channel_id) REFERENCES channel (id);
