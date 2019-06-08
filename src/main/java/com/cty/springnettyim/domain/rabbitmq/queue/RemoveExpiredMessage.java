package com.cty.springnettyim.domain.rabbitmq.queue;

import com.cty.springnettyim.adapter.listener.RabbitMqListener;

public class RemoveExpiredMessage implements Runnable {

    String uuid;

    public RemoveExpiredMessage(String uuid) {
        this.uuid = uuid;
    }

    public void run() {
        RabbitMqListener.ackWaitList.remove(uuid);
    }
}
