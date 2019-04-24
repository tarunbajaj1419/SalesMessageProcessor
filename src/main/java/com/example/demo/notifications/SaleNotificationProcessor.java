package com.example.demo.notifications;

import com.example.demo.managers.SaleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by tarunbajaj on 23/04/2019.
 */
@Component
public class SaleNotificationProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleNotificationProcessor.class);

  @Autowired
  private SaleManager saleManager;

  @Autowired
  private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

  @Value("${report.frequency:10}")
  private int reportFrequency;

  @Value("${message.threshold:50}")
  private int messageThreshold;

  private int messageCount = 0;

  @JmsListener(destination = "saleNotifications", containerFactory = "myFactory")
  public void receiveSaleNotification(SaleNotification saleNotification) {
    LOGGER.info("Received SaleNotification <{}>", saleNotification);

    if (messageCount < messageThreshold) {

      try {

        if (saleNotification.getNoOfOccurrences() >= 1 && isNotBlank(saleNotification.getProductType())) {
          for (int i = 0; i < saleNotification.getNoOfOccurrences(); i++) {
            saleManager.recordSale(saleNotification.getProductType(), new BigDecimal(saleNotification.getSaleValue()));
          }
        } else {
          LOGGER.warn("Failed to process SaleNotification <{}>. Invalid details provided", saleNotification);
        }

        if (isNotBlank(saleNotification.getAdjustmentOperation())) {
          SaleAdjustment saleAdjustment = SaleAdjustment.from(saleNotification.getAdjustmentOperation(),
            saleNotification.getAdjustmentValue());

          saleManager.recordSaleAdjustment(saleNotification.getProductType(), saleAdjustment);
        }

        LOGGER.debug("Consumed SaleNotification <{}>", saleNotification);
        messageCount++;

        if (messageCount % reportFrequency == 0) {
          saleManager.logSaleReport();
        }

        if (messageCount >= messageThreshold) {
          LOGGER.info("Message Threshold Reached. Pausing Message Processing");
          saleManager.logSaleAdjustmentReport();
          jmsListenerEndpointRegistry.stop();
        }

      } catch (Exception e) {
        LOGGER.error("Failed to process SaleNotification <{}>. {}", saleNotification, e.getMessage());
      }
    }
  }
}
