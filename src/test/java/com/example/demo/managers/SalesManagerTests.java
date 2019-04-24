package com.example.demo.managers;

import com.example.demo.dao.SaleAdjustmentDao;
import com.example.demo.dao.SaleDao;
import com.example.demo.data.Sale;
import com.example.demo.exceptions.SaleProcessingException;
import com.example.demo.notifications.SaleAdjustment;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SaleManager}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SalesManagerTests {

  @InjectMocks
  private SaleManager saleManager;

  @Spy
  private SaleDao saleDao;

  @Spy
  private SaleAdjustmentDao saleAdjustmentDao;

  private final String TEST_PRODUCT_TYPE_CHIPS = "Chips";
  private final String TEST_PRODUCT_TYPE_ORANGE = "Orange";
  private final String TEST_SALE_VALUE = "1.20";
  private final String TEST_ADJUSTMENT_VALUE = ".20";
  private final String TEST_ADJUSTMENT_OPERATOR_ADD = "ADD";

  @After
  public void tearDown() throws Exception {
    saleManager.clearSalesForProductType(TEST_PRODUCT_TYPE_CHIPS);
  }

  @Test
  public void testFetchSalesForProductType() {
    Sale TEST_SALE = new Sale(TEST_PRODUCT_TYPE_CHIPS, new BigDecimal(TEST_SALE_VALUE));

    Mockito.when(saleDao.fetchForProductType(TEST_PRODUCT_TYPE_CHIPS)).thenReturn(Arrays.asList(TEST_SALE));
    Mockito.when(saleDao.fetchForProductType(TEST_PRODUCT_TYPE_ORANGE)).thenCallRealMethod();

    assertEquals(Arrays.asList(TEST_SALE), saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_CHIPS));
    assertTrue(saleManager.fetchSalesForProductType(TEST_PRODUCT_TYPE_ORANGE).isEmpty());

    Mockito.verify(saleDao, Mockito.times(1)).fetchForProductType(TEST_PRODUCT_TYPE_CHIPS);
    Mockito.verify(saleDao, Mockito.times(1)).fetchForProductType(TEST_PRODUCT_TYPE_ORANGE);
  }

  @Test
  public void testRecordSale() throws SaleProcessingException {
    BigDecimal TEST_SALE_VALUE = new BigDecimal(this.TEST_SALE_VALUE);

    saleManager.recordSale(TEST_PRODUCT_TYPE_CHIPS, TEST_SALE_VALUE);

    Mockito.verify(saleDao, Mockito.times(1)).recordSale(TEST_PRODUCT_TYPE_CHIPS, TEST_SALE_VALUE);
  }

  @Test(expected = SaleProcessingException.class)
  public void testRecordSaleForException() throws SaleProcessingException {
    BigDecimal TEST_SALE_VALUE = new BigDecimal(this.TEST_SALE_VALUE);

    saleManager.recordSale(StringUtils.EMPTY, TEST_SALE_VALUE);
  }

  @Test
  public void testRecordSaleAdjustment() throws SaleProcessingException {
    Sale TEST_SALE = new Sale(TEST_PRODUCT_TYPE_CHIPS, new BigDecimal(TEST_SALE_VALUE));
    SaleAdjustment saleAdjustment = SaleAdjustment.from(TEST_PRODUCT_TYPE_CHIPS, TEST_ADJUSTMENT_OPERATOR_ADD, TEST_ADJUSTMENT_VALUE);

    Mockito.when(saleDao.fetchForProductType(TEST_PRODUCT_TYPE_CHIPS)).thenReturn(Arrays.asList(TEST_SALE));
    Mockito.doCallRealMethod().when(saleAdjustmentDao).save(saleAdjustment);

    saleManager.recordSaleAdjustment(saleAdjustment);

    assertEquals(new BigDecimal(TEST_SALE_VALUE).add(new BigDecimal(TEST_ADJUSTMENT_VALUE)), TEST_SALE.getValue());

    Mockito.verify(saleDao, Mockito.times(1)).fetchForProductType(TEST_PRODUCT_TYPE_CHIPS);
    Mockito.verify(saleAdjustmentDao, Mockito.times(1)).save(saleAdjustment);
  }

  @Test(expected = SaleProcessingException.class)
  public void testRecordSaleAdjustmentForException() throws SaleProcessingException {
    SaleAdjustment saleAdjustment = SaleAdjustment.from(StringUtils.EMPTY, TEST_ADJUSTMENT_OPERATOR_ADD, TEST_ADJUSTMENT_VALUE);

    saleManager.recordSaleAdjustment(saleAdjustment);
  }

  @Test
  public void testLogSaleReport() {
    Mockito.when(saleDao.fetchAllSales()).thenCallRealMethod();

    saleManager.logSaleReport();

    Mockito.verify(saleDao, Mockito.times(1)).fetchAllSales();
  }

  @Test
  public void testLogSaleAdjustmentReport() {
    Mockito.when(saleDao.fetchAllSales()).thenCallRealMethod();

    saleManager.logSaleReport();

    Mockito.verify(saleDao, Mockito.times(1)).fetchAllSales();
  }
}
