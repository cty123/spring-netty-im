package com.cty.springnettyim.domain.rabbitmq.util;

import com.cty.springnettyim.domain.rabbitmq.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitMQUtil {

    public static RabbitMQUtil rabbitMQUtil;

    @Autowired
    private RabbitMQService rabbitService;

    public RabbitMQService getRabbitService() {
        return rabbitService;
    }

    @PostConstruct
    public void init() {
        rabbitMQUtil = this;
    }
}

