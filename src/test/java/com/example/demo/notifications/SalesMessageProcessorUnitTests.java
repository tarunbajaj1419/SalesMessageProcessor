package com.example.demo.notifications;

import com.example.demo.exceptions.SaleProcessingException;
import com.example.demo.managers.SaleManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import java.math.BigDecimal;

/**
 * Tests for {@link SaleNotificationProcessor} JMS Listener
 */
@RunWith(JUnit4.class)
public class SalesMessageProcessorUnitTests {

  private final SaleManager saleManager = Mockito.mock(SaleManager.class);
  private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry = Mockito.mock(JmsListenerEndpointRegistry.class);
  private final SaleNotificationProcessor saleNotificationProcessor = new SaleNotificationProcessor(saleManager, jmsListenerEndpointRegistry);

  private final int reportFrequency = 10;
  private final int messageThreshold = 50;

  private final String TEST_PRODUCT_TYPE_BANANA = "Banana";
  private final String TEST_SALE_VALUE = ".20";

  @Before
  public void setUp() throws Exception {
    saleNotificationProcessor.setReportFrequency(reportFrequency);
    saleNotificationProcessor.setMessageThreshold(messageThreshold);
  }

  @Test
  public void testReceiveSaleNotification() throws SaleProcessingException {

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, SALE_OCCURRENCES);

    for (int i = 0; i < reportFrequency - 2; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    Mockito.verify(saleManager, Mockito.times(reportFrequency - 2)).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.verify(saleManager, Mockito.never()).logSaleReport();
    Mockito.verify(saleManager, Mockito.never()).logSaleAdjustmentReport();
    Mockito.verify(jmsListenerEndpointRegistry, Mockito.never()).stop();
  }

  @Test
  public void testReceiveSaleNotificationWithInvalidOccurrences() throws SaleProcessingException {

    int SALE_OCCURRENCES = -1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, SALE_OCCURRENCES);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    Mockito.verify(saleManager, Mockito.never()).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.verify(saleManager, Mockito.never()).logSaleReport();
    Mockito.verify(saleManager, Mockito.never()).logSaleAdjustmentReport();
    Mockito.verify(jmsListenerEndpointRegistry, Mockito.never()).stop();
  }

  @Test
  public void testReceiveSaleNotificationWithInvalidProduct() throws SaleProcessingException {

    int SALE_OCCURRENCES = 2;

    SaleNotification appleSaleNotification = createSaleNotification(StringUtils.EMPTY, TEST_SALE_VALUE, SALE_OCCURRENCES);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    Mockito.verify(saleManager, Mockito.never()).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.verify(saleManager, Mockito.never()).logSaleReport();
    Mockito.verify(saleManager, Mockito.never()).logSaleAdjustmentReport();
    Mockito.verify(jmsListenerEndpointRegistry, Mockito.never()).stop();
  }

  @Test
  public void testReceiveSaleNotificationWithSaleReportLogging() throws SaleProcessingException {

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, SALE_OCCURRENCES);

    for (int i = 0; i < reportFrequency * 2; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    Mockito.verify(saleManager, Mockito.times(reportFrequency * 2)).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.verify(saleManager, Mockito.times(2)).logSaleReport();
    Mockito.verify(saleManager, Mockito.never()).logSaleAdjustmentReport();
    Mockito.verify(jmsListenerEndpointRegistry, Mockito.never()).stop();
  }

  @Test
  public void testReceiveSaleNotificationWithSaleAdjustmentReportLogging() throws SaleProcessingException {

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_BANANA, TEST_SALE_VALUE, SALE_OCCURRENCES);

    for (int i = 0; i < messageThreshold + 5; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    Mockito.verify(saleManager, Mockito.times(messageThreshold)).recordSale(Mockito.isA(String.class), Mockito.isA(BigDecimal.class));
    Mockito.verify(saleManager, Mockito.times(5)).logSaleReport();
    Mockito.verify(saleManager).logSaleAdjustmentReport();
    Mockito.verify(jmsListenerEndpointRegistry).stop();
  }

  private SaleNotification createSaleNotification(String productType, String saleValue, int saleOccurrences) {
    return new SaleNotification(productType, saleValue, saleOccurrences);
  }
}
