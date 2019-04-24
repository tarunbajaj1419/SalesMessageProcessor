package com.example.demo.managers;

import com.example.demo.dao.SaleAdjustmentDao;
import com.example.demo.dao.SaleDao;
import com.example.demo.data.Sale;
import com.example.demo.data.SaleAdjustment;
import com.example.demo.exceptions.SaleProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Manager for {@link Sale} instances.
 */
@Service
public class SaleManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleManager.class);

  @Autowired
  private SaleDao saleDao;

  @Autowired
  private SaleAdjustmentDao saleAdjustmentDao;

  /**
   * Clears Sales for the given {@code productType}
   *
   * @param productType
   */
  public void clearSalesForProductType(String productType) {
    saleDao.clearSalesForProductType(productType);
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
    if (StringUtils.isNoneBlank(saleAdjustment.getProductType())) {
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
    if (StringUtils.isNoneBlank(productType)) {
      saleDao.recordSale(productType, saleValue);
    } else {
      LOGGER.warn("Unable to Record Sale. Invalid ProductType");
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
