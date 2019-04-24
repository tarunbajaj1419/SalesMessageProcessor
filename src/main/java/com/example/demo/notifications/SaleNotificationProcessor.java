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
 * Processor class for the {@link SaleNotification} messages
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

  /**
   * An implementation of async JMS Listener for {@link SaleNotification} messages
   *
   * @param saleNotification
   */
  @JmsListener(destination = "saleNotifications", containerFactory = "myFactory")
  public void receiveSaleNotification(SaleNotification saleNotification) {
    LOGGER.info("Received SaleNotification <{}>", saleNotification);

    if (messageCount < messageThreshold) {

      try {

        //Record sales
        if (saleNotification.getNoOfOccurrences() >= 1 && isNotBlank(saleNotification.getProductType())) {
          for (int i = 0; i < saleNotification.getNoOfOccurrences(); i++) {
            saleManager.recordSale(saleNotification.getProductType(), new BigDecimal(saleNotification.getSaleValue()));
          }
        } else {
          LOGGER.warn("Failed to process SaleNotification <{}>. Invalid details provided", saleNotification);
        }

        //Record sale adjustments
        if (isNotBlank(saleNotification.getAdjustmentOperation())) {
          SaleAdjustment saleAdjustment = SaleAdjustment.from(saleNotification.getAdjustmentOperation(),
            saleNotification.getAdjustmentValue());

          saleManager.recordSaleAdjustment(saleNotification.getProductType(), saleAdjustment);
        }

        LOGGER.debug("Consumed SaleNotification <{}>", saleNotification);
        messageCount++;

        //Log Sale report for messages processed.
        if (messageCount % reportFrequency == 0) {
          saleManager.logSaleReport();
        }

        //Stop message processing and log sale adjustment report
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

  public int getReportFrequency() {
    return reportFrequency;
  }

  public void setReportFrequency(int reportFrequency) {
    this.reportFrequency = reportFrequency;
  }

  public int getMessageThreshold() {
    return messageThreshold;
  }

  public void setMessageThreshold(int messageThreshold) {
    this.messageThreshold = messageThreshold;
  }
}
