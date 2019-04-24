package com.example.demo.managers;

import com.example.demo.dao.SaleAdjustmentDao;
import com.example.demo.dao.SaleDao;
import com.example.demo.data.Sale;
import com.example.demo.data.SaleAdjustment;
import com.example.demo.exceptions.SaleProcessingException;
import com.example.demo.notifications.SaleAdjustmentOperation;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SaleManager}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SalesManagerTests {

  @Autowired
  private SaleManager saleManager;

  @Autowired
  private SaleDao saleDao;

  @Autowired
  private SaleAdjustmentDao saleAdjustmentDao;

  private final String TEST_PRODUCT_TYPE_CHIPS = "Chips";
  private final String TEST_PRODUCT_TYPE_COLA = "Cola";
  private final BigDecimal TEST_SALE_VALUE = new BigDecimal("1.20");
  private final BigDecimal TEST_ADJUSTMENT_VALUE = new BigDecimal(".20");

  @Before
  public void setUp() throws Exception {
    saleDao.recordSale(TEST_PRODUCT_TYPE_CHIPS, TEST_SALE_VALUE);
    saleAdjustmentDao.save(new SaleAdjustment(TEST_PRODUCT_TYPE_CHIPS, SaleAdjustmentOperation.ADD, TEST_ADJUSTMENT_VALUE));
  }

  @After
  public void tearDown() throws Exception {
    saleDao.clearSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);
    saleDao.clearSalesForProductType(TEST_PRODUCT_TYPE_COLA);
    saleAdjustmentDao.clearSaleAdjustmentsForProductType(TEST_PRODUCT_TYPE_CHIPS);
  }

  @Test
  public void testClearSalesForProductType() {
    List<Sale> sales = saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);

    assertEquals(1, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_CHIPS)));
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(TEST_SALE_VALUE)));

    saleManager.clearSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);

    sales = saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);

    assertEquals(0, sales.size());
  }

  @Test
  public void testClearSalesAdjustmentsForProductType() {
    List<SaleAdjustment> saleAdjustments = saleManager.fetchSaleAdjustmentForProductType(TEST_PRODUCT_TYPE_CHIPS);

    assertEquals(1, saleAdjustments.size());
    assertTrue(saleAdjustments.stream().allMatch(saleAdjustment -> saleAdjustment.getProductType().equals(TEST_PRODUCT_TYPE_CHIPS)));
    assertTrue(saleAdjustments.stream().allMatch(saleAdjustment -> saleAdjustment.getAdjustmentValue().equals(TEST_ADJUSTMENT_VALUE)));

    saleManager.clearSalesAdjustmentsForProductType(TEST_PRODUCT_TYPE_CHIPS);

    saleAdjustments = saleManager.fetchSaleAdjustmentForProductType(TEST_PRODUCT_TYPE_CHIPS);

    assertEquals(0, saleAdjustments.size());
  }

  @Test
  public void testFetchSalesForProductType() throws SaleProcessingException {
    List<Sale> sales = saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);

    assertEquals(1, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_CHIPS)));
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(TEST_SALE_VALUE)));
  }

  @Test
  public void testRecordSale() throws SaleProcessingException {
    saleManager.recordSale(TEST_PRODUCT_TYPE_COLA, TEST_SALE_VALUE);

    List<Sale> sales = saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_COLA);
    assertEquals(1, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_COLA)));
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(TEST_SALE_VALUE)));
  }

  @Test(expected = SaleProcessingException.class)
  public void testRecordSaleForException() throws SaleProcessingException {
    saleManager.recordSale(StringUtils.EMPTY, TEST_SALE_VALUE);
  }

  @Test
  public void testRecordSaleAdjustment() throws SaleProcessingException {
    SaleAdjustment saleAdjustment = new SaleAdjustment(TEST_PRODUCT_TYPE_CHIPS, SaleAdjustmentOperation.ADD, TEST_ADJUSTMENT_VALUE);

    saleManager.recordSaleAdjustment(saleAdjustment);

    List<Sale> sales = saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);
    assertEquals(1, sales.size());
    assertTrue(sales.stream().allMatch(sale -> sale.getProductType().equals(TEST_PRODUCT_TYPE_CHIPS)));
    assertTrue(sales.stream().allMatch(sale -> sale.getValue().equals(TEST_SALE_VALUE.add(TEST_ADJUSTMENT_VALUE))));
  }

  @Test(expected = SaleProcessingException.class)
  public void testRecordSaleAdjustmentForException() throws SaleProcessingException {
    SaleAdjustment saleAdjustment = new SaleAdjustment(StringUtils.EMPTY, SaleAdjustmentOperation.ADD, TEST_ADJUSTMENT_VALUE);

    saleManager.recordSaleAdjustment(saleAdjustment);
  }
}
