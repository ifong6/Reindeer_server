package us.reindeers.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHost);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPassword);
        return connectionFactory;
    }

    // 定义TopicExchange
    @Bean
    public TopicExchange reindeersExchange() {
        return new TopicExchange("reindeersExchange");
    }

    // 定义队列
    @Bean
    public Queue notificationQueue() {
        return new Queue("notificationQueue");
    }

    // 绑定队列到交换机，使用通配符路由键
    @Bean
    public Binding bindingNotifications() {
        return BindingBuilder.bind(notificationQueue())
                .to(reindeersExchange())
                .with("notification.#");
    }

    // 使用JSON序列化消息
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}