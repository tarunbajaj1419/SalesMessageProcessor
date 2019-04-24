package com.example.demo.managers;

import com.example.demo.dao.SaleDao;
import com.example.demo.data.Sale;
import com.example.demo.exceptions.SaleProcessingException;
import com.example.demo.notifications.SaleAdjustment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tarunbajaj on 23/04/2019.
 */
@Service
public class SaleManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleManager.class);

  @Autowired
  private SaleDao saleDao;

  public void clearSalesForProductType(String productType) {
    saleDao.clearSalesForProductType(productType);
  }

  public List<Sale> fetchSalesForProductType(String productType) {
    return saleDao.fetchForProductType(productType);
  }

  public void recordSaleAdjustment(String productType, SaleAdjustment saleAdjustment) throws SaleProcessingException {
    if (StringUtils.isNoneBlank(productType)) {
      fetchSalesForProductType(productType).forEach(saleAdjustment::apply);
    } else {
      LOGGER.warn("Unable to Record Sale. Invalid ProductType");
      throw new SaleProcessingException("Invalid ProductType");
    }
  }

  public void recordSale(String productType, BigDecimal saleValue) throws SaleProcessingException {
    if (StringUtils.isNoneBlank(productType)) {
      saleDao.recordSale(productType, saleValue);
    } else {
      LOGGER.warn("Unable to Record Sale. Invalid ProductType");
      throw new SaleProcessingException("Invalid ProductType");
    }
  }

  public void logSaleReport() {
    LOGGER.info("Logging Sale Report");
    System.out.println("=============================================");
    System.out.println("Product Type\tSale Count\tTotal Sale Vale");
    System.out.println("=============================================");
    saleDao.fetchAllSales().forEach((productType, sales) -> {
      System.out.println(productType + "\t\t:\t\t" + sales.size() + "\t\t:\t\t" + sales.stream().map(Sale::getValue).reduce(BigDecimal.ZERO, BigDecimal::add));
    });
  }

  public void logSaleAdjustmentReport() {

  }
}
