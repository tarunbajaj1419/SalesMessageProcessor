package com.example.demo;

import com.example.demo.data.Sale;
import com.example.demo.managers.SaleManager;
import com.example.demo.notifications.SaleNotification;
import com.example.demo.notifications.SaleNotificationProcessor;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SalesMessageProcessorApplicationTests {

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private SaleNotificationProcessor saleNotificationProcessor;

  @Spy
  private SaleManager saleManager;

  @Value("${report.frequency:10}")
  private int reportFrequency;

  @Value("${message.threshold:50}")
  private int messageThreshold;

  private final String TEST_PRODUCT_TYPE_APPLE = "Apple";
  private final String TEST_SALE_VALUE = ".20";
  private final String TEST_ADJUSTMENT_INVALID_OPERATOR = "INVALID";
  private final String TEST_ADJUSTMENT_OPERATOR_ADD = "ADD";
  private final String TEST_ADJUSTMENT_OPERATOR_SUBTRACT = "SUBTRACT";
  private final String TEST_ADJUSTMENT_OPERATOR_MULTIPLY = "MULTIPLY";

  @After
  public void tearDown() throws Exception {
    saleManager.clearSalesForProductType(TEST_PRODUCT_TYPE_APPLE);
  }

  @Test
  public void contextLoads() {
    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, ".20", 1);
    jmsTemplate.convertAndSend("saleNotifications", appleSaleNotification);
  }

  @Test
  public void testReceiveSaleNotificationWithoutAdjustment() {
    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(SALE_OCCURRENCES, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));
  }

  @Test
  public void testReceiveSaleNotificationWithoutAdjustmentAndInvalidOccurrences() {
    int SALE_OCCURRENCES = -1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(0, sales.size());
  }

  @Test
  public void testReceiveMultipleSaleNotificationWithoutAdjustment() {
    int SALE_OCCURRENCES = 5;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(SALE_OCCURRENCES, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));
  }

  @Test
  public void testReceiveSaleNotificationWithADDAdjustment() {
    int SALE_OCCURRENCES = 1;
    String ADJUSTMENT_VALUE = ".10";

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES,
      TEST_ADJUSTMENT_OPERATOR_ADD, ADJUSTMENT_VALUE);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(SALE_OCCURRENCES, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE).add(new BigDecimal(ADJUSTMENT_VALUE)))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));
  }

  @Test
  public void testReceiveSaleNotificationWithSUBTRACTAdjustment() {
    int SALE_OCCURRENCES = 1;
    String ADJUSTMENT_VALUE = ".10";

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES,
      TEST_ADJUSTMENT_OPERATOR_SUBTRACT, ADJUSTMENT_VALUE);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(SALE_OCCURRENCES, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE).subtract(new BigDecimal(ADJUSTMENT_VALUE)))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));
  }

  @Test
  public void testReceiveSaleNotificationWithMULTIPLYAdjustment() {
    int SALE_OCCURRENCES = 1;
    String ADJUSTMENT_VALUE = ".10";

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES,
      TEST_ADJUSTMENT_OPERATOR_MULTIPLY, ADJUSTMENT_VALUE);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(SALE_OCCURRENCES, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE).multiply(new BigDecimal(ADJUSTMENT_VALUE)))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));
  }

  @Test
  public void testReceiveSaleNotificationWithInvalidAdjustment() {
    int SALE_OCCURRENCES = 1;
    String ADJUSTMENT_VALUE = ".10";

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES,
      TEST_ADJUSTMENT_INVALID_OPERATOR, ADJUSTMENT_VALUE);

    saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(SALE_OCCURRENCES, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));
  }


  @Test
  public void testSaleReportLogging() {

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES);

    for (int i = 0; i < 20; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(20, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));

    Mockito.verify(saleManager).logSaleReport();
  }

  @Test
  public void testSaleAdjustmentReportLogging() {
//    SaleManager sm = Mockito.spy(saleManager);

    int SALE_OCCURRENCES = 1;

    SaleNotification appleSaleNotification = createSaleNotification(TEST_PRODUCT_TYPE_APPLE, TEST_SALE_VALUE, SALE_OCCURRENCES);

    for (int i = 0; i < messageThreshold + 5; i++) {
      saleNotificationProcessor.receiveSaleNotification(appleSaleNotification);
    }

    List<Sale> sales = saleManager.fetchSalesForProductType(appleSaleNotification.getProductType());

    assertEquals(messageThreshold, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(new BigDecimal(TEST_SALE_VALUE))));
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_APPLE)));

    Mockito.verify(saleManager).logSaleAdjustmentReport();
  }

  private SaleNotification createSaleNotification(String productType, String saleValue, int saleOccurrences) {
    return new SaleNotification(productType, saleValue, saleOccurrences);
  }

  private SaleNotification createSaleNotification(String productType, String saleValue, int saleOccurrences,
                                                  String adjustmentOperator, String adjustmentValue) {
    return new SaleNotification(productType, saleValue, saleOccurrences, adjustmentOperator, adjustmentValue);
  }
}
