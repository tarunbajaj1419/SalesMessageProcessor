package com.example.demo.managers;

import com.example.demo.dao.SaleAdjustmentDao;
import com.example.demo.dao.SaleDao;
import com.example.demo.data.Sale;
import com.example.demo.data.SaleAdjustment;
import com.example.demo.exceptions.SaleProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Manager for {@link Sale} instances.
 */
@Service
public class SaleManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleManager.class);

  private final SaleDao saleDao;
  private final SaleAdjustmentDao saleAdjustmentDao;

  /**
   * Initialises a new {@link SaleManager} with the given parameters
   *
   * @param saleDao
   * @param saleAdjustmentDao
   */
  @Autowired
  public SaleManager(SaleDao saleDao, SaleAdjustmentDao saleAdjustmentDao) {
    this.saleDao = saleDao;
    this.saleAdjustmentDao = saleAdjustmentDao;
  }

  /**
   * Clears Sales for the given {@code productType}
   *
   * @param productType
   */
  public void clearSalesForProductType(String productType) {
    saleDao.clearSalesForProductType(productType);
  }

  /**
   * Clears SaleAdjustments for the given {@code productType}
   *
   * @param productType
   */
  public void clearSalesAdjustmentsForProductType(String productType) {
    saleAdjustmentDao.clearSaleAdjustmentsForProductType(productType);
  }

  /**
   * Fetches SaleAdjustments for the given {@code productType}
   *
   * @param productType
   * @return list of Sales
   */
  public List<SaleAdjustment> fetchSaleAdjustmentForProductType(String productType) {
    return saleAdjustmentDao.fetchForProductType(productType);
  }

  /**
   * Fetches Sales for the given {@code productType}
   *
   * @param productType
   * @return list of Sales
   */
  public List<Sale> fetchSalesForProductType(String productType) {
    return saleDao.fetchForProductType(productType);
  }

  /**
   * Records Sale adjustments
   *
   * @param saleAdjustment
   * @throws SaleProcessingException
   */
  public void recordSaleAdjustment(SaleAdjustment saleAdjustment) throws SaleProcessingException {
    if (isNotBlank(saleAdjustment.getProductType())) {
      fetchSalesForProductType(saleAdjustment.getProductType()).forEach(saleAdjustment::apply);
      saleAdjustmentDao.save(saleAdjustment);
    } else {
      LOGGER.warn("Unable to Record Sale. Invalid ProductType");
      throw new SaleProcessingException("Invalid ProductType");
    }
  }

  /**
   * Records Sale for the given {@code productType}
   *
   * @param productType
   * @param saleValue
   * @throws SaleProcessingException
   */
  public void recordSale(String productType, BigDecimal saleValue) throws SaleProcessingException {
    if (isNotBlank(productType) && saleValue != null) {
      saleDao.recordSale(productType, saleValue);
    } else {
      LOGGER.warn("Unable to Record Sale. Invalid Product/Value -> {}:{}", productType, saleValue);
      throw new SaleProcessingException("Invalid ProductType");
    }
  }

  /**
   * Logs a report for the Sales already recorded
   */
  public void logSaleReport() {

    //Logging on console for the exercise.

    LOGGER.info("Logging Sale Report");
    System.out.println("=============================================");
    System.out.println("Product Type\tSale Count\tTotal Sale Vale");
    System.out.println("=============================================");
    saleDao.fetchAllSales().forEach((productType, sales) -> {
      System.out.println(productType + "\t\t:\t\t" + sales.size() + "\t\t:\t\t" + sales.stream().map(Sale::getValue).reduce(BigDecimal.ZERO, BigDecimal::add));
    });
  }

  /**
   * Logs a report for the adjustments made to stored sales
   */
  public void logSaleAdjustmentReport() {

    //Logging on console for the exercise.

    LOGGER.info("Logging Sale Adjustment Report");
    System.out.println("=============================================");
    System.out.println("Product Type\tAdjustment Operator\tAdjustment Vale");
    System.out.println("=============================================");
    saleAdjustmentDao.fetchAllSaleAdjustments().forEach((productType, saleAdjustments) -> {
      saleAdjustments.forEach(saleAdjustment -> {
        System.out.println(productType + "\t\t:\t\t" + saleAdjustment.getSaleAdjustmentOperation().name() + "\t\t:\t\t" + saleAdjustment.getAdjustmentValue());
      });
    });
  }
}
