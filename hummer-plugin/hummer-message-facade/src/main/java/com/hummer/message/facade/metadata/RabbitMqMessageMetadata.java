package com.hummer.message.facade.metadata;

import com.hummer.message.facade.publish.PublishMessageExceptionCallback;
import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 14:28
 **/
@Getter
public class RabbitMqMessageMetadata extends MessagePublishMetadata {
    public RabbitMqMessageMetadata(String appId
            , int perSecondSemaphore
            , String address
            , PublishMessageExceptionCallback callback) {
        super(appId, perSecondSemaphore, address, callback);
    }
}
