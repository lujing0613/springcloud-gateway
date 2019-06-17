package com.teemor.plugin.activemq;


import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @Description:
 * @Auther: yq
 */

@Resource
@Component
public class Producer {

    @Resource
    private JmsTemplate jmsQueueTemplate;
    public void send(String destination, final Object message) {
        jmsQueueTemplate.send(destination, session -> jmsQueueTemplate.getMessageConverter().toMessage(message, session));
    }

}
