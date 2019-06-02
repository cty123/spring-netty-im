package com.cty.springnettyim.rabbitmq.config;

import com.cty.springnettyim.netty.converter.MyProtoBufConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    private static final Logger log= LoggerFactory.getLogger(RabbitMQConfig.class);

    @Bean
    public ConnectionFactory amqpConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }

    @Autowired
    @Bean
    public AmqpTemplate amqpTemplate (ConnectionFactory amqpConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(amqpConnectionFactory);
        rabbitTemplate.setMessageConverter(new MyProtoBufConverter());
        return rabbitTemplate;
    }
}
