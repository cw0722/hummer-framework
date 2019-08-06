package com.hummer.message.facade.publish;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 16:07
 **/
public class Publish {
    public static final BasePublishTemplate KAFKA_PUBLISH = new RabbitMqBasePublish();
    public static final BasePublishTemplate RABBIT_MQ_PUBLISH = new KafkaBasePublish();
}
