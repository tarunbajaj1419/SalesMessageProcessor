package com.example.demo.notifications;

import com.example.demo.exceptions.SaleProcessingException;
import com.example.demo.managers.SaleManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * Tests for {@link SaleNotificationProcessor} JMS Listener
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SalesMessageJMSListenerTests {

  @Autowired
  private JmsTemplate jmsTemplate;

  @InjectMocks
  private SaleNotificationProcessor saleNotificationProcessor;

  @Spy
  private SaleManager saleManager;

  @Spy
  private JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

  @Value("${report.frequency:10}")
  private int reportFrequency;

  @Value("${message.threshold:50}")
  private int messageThreshold;

  private final String TEST_PRODUCT_TYPE_BANANA = "Banana";
  private final String TEST_SALE_VALUE = ".20";

  @Before
  public void setUp() throws Exception {
    saleNotificationProcessor.setReportFrequency(reportFrequency);
    saleNotificationProcessor.setMessageThreshold(messageThreshold);
  }

  @Test
  public void contextLoads() {
    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, ".20", 1);
    jmsTemplate.convertAndSend("saleNotifications", appleSaleNotification);
  }

  @Test
  public void testSaleReportLogging() throws SaleProcessingException {

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, SALE_OCCURRENCES);

    Mockito.doNothing().when(saleManager).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.doNothing().when(saleManager).logSaleReport();

    for (int i = 0; i < reportFrequency * 2; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    Mockito.verify(saleManager, Mockito.times(2)).logSaleReport();
  }

  @Test
  public void testSaleAdjustmentReportLogging() throws SaleProcessingException {

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, SALE_OCCURRENCES);

    Mockito.doNothing().when(saleManager).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.doNothing().when(saleManager).logSaleReport();
    Mockito.doNothing().when(saleManager).logSaleAdjustmentReport();

    for (int i = 0; i < messageThreshold + 5; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    Mockito.verify(saleManager, Mockito.times(5)).logSaleReport();
    Mockito.verify(saleManager).logSaleAdjustmentReport();
    Mockito.verify(jmsListenerEndpointRegistry).stop();
  }

  private SaleNotification createSaleNotification(String productType, String saleValue, int saleOccurrences) {
    return new SaleNotification(productType, saleValue, saleOccurrences);
  }
}
