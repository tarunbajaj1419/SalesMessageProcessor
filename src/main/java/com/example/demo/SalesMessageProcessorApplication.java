package com.example.demo;

import com.example.demo.notifications.SaleNotification;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableJms
public class SalesMessageProcessorApplication {

  @Bean
  public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                  DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    // This provides all boot's default to this factory, including the message converter
    configurer.configure(factory, connectionFactory);
    // You could still override some of Boot's default if necessary.
    return factory;
  }

  @Bean // Serialize message content to json using TextMessage
  public MessageConverter jacksonJmsMessageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(SalesMessageProcessorApplication.class, args);
    JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

    String TEST_PRODUCT_TYPE_APPLE = "Apple";
    String TEST_PRODUCT_TYPE_BANANA = "Banana";
    String TEST_SALE_VALUE = ".20";
    String TEST_ADJUSTMENT_OPERATOR_ADD = "ADD";
    String TEST_ADJUSTMENT_OPERATOR_SUBTRACT = "SUBTRACT";
    String TEST_ADJUSTMENT_OPERATOR_MULTIPLY = "MULTIPLY";
    String ADJUSTMENT_VALUE = ".10";

    SaleNotification saleNotification1 = new SaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, 1);
    SaleNotification saleNotification2 = new SaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, 5);
    SaleNotification saleNotification3 = new SaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, 1, TEST_ADJUSTMENT_OPERATOR_ADD, ADJUSTMENT_VALUE);
    SaleNotification saleNotification4 = new SaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, 2, TEST_ADJUSTMENT_OPERATOR_SUBTRACT, ADJUSTMENT_VALUE);
    SaleNotification saleNotification5 = new SaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, 3, TEST_ADJUSTMENT_OPERATOR_MULTIPLY, ADJUSTMENT_VALUE);

    jmsTemplate.convertAndSend("saleNotifications", saleNotification1);
    jmsTemplate.convertAndSend("saleNotifications", saleNotification2);
    jmsTemplate.convertAndSend("saleNotifications", saleNotification3);
    jmsTemplate.convertAndSend("saleNotifications", saleNotification4);
    jmsTemplate.convertAndSend("saleNotifications", saleNotification5);
    //Sale count of 12 so far

    //Try to sent another 50 messages, should stop after 45 as we've already processed 5
    //Total Sale count should be 45 + 12 = 57
    for (int i = 0; i < 50; i++) {
      jmsTemplate.convertAndSend("saleNotifications", saleNotification1);
    }
  }

}
