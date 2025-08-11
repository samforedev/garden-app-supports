package com.example.gardenappsupports.infraestructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /* Rabbit Config Audit Logs */
    @Value("${rabbitmq.audit.exchange}")
    private String auditExchange;
    @Value("${rabbitmq.audit.queue}")
    private String auditQueue;
    @Value("${rabbitmq.audit.routing-key}")
    private String auditRoutingKey;

    /* Rabbit Config Engine Process */
    @Value("${rabbitmq.engine.exchange}")
    private String engineExchange;
    @Value("${rabbitmq.engine.queue}")
    private String engineQueue;
    @Value("${rabbitmq.engine.routing-key}")
    private String engineRoutingKey;


    /**
     * Audit Beans
     */
    @Bean
    public TopicExchange auditExchange() {
        return new TopicExchange(auditExchange);
    }
    @Bean
    public Queue auditQueue() {
        return new Queue(auditQueue, true);
    }
    @Bean
    public Binding auditBinding(Queue auditQueue, TopicExchange auditExchange) {
        return BindingBuilder.bind(auditQueue).to(auditExchange).with(auditRoutingKey);
    }

    /**
     * Engine Beans
     */
    @Bean
    public TopicExchange engineExchange() {
        return new TopicExchange(engineExchange);
    }

    @Bean
    public Queue engineQueue() {
        return new Queue(engineQueue, true);
    }

    @Bean
    public Binding engineBinding(Queue engineQueue, TopicExchange engineExchange) {
        return BindingBuilder.bind(engineQueue).to(engineExchange).with(engineRoutingKey);
    }


}
