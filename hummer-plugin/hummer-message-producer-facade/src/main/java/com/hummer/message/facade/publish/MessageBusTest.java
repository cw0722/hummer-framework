package com.hummer.message.facade.publish;

import org.testng.annotations.Test;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:45
 **/
public class MessageBusTest {
    @Test
    public void sendMessage() {
        MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().messageKey("2323").topicId("test").build())
                .appId("test")
                .build()
                .publish();
    }
}
