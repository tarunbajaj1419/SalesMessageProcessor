package com.example.demo.dao;

import com.example.demo.data.Sale;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO implementation for {@link Sale} instances
 *
 * Using a local Cache for the exercise
 */
@Repository
public class SaleDao {

  private Map<String, List<Sale>> saleCache = new HashMap<>();

  /**
   * Clears Sales for the given {@code productType}
   *
   * @param productType
   */
  public void clearSalesForProductType(String productType) {
    saleCache.computeIfPresent(productType, (k, v) -> new ArrayList<>());
  }

  /**
   * Fetches all sales
   *
   * @return Map of all sales by product type
   */
  public Map<String, List<Sale>> fetchAllSales() {
    return new HashMap<>(saleCache);
  }

  /**
   * Fetches sales for the given {@code productType}
   *
   * @param productType
   * @return List of sales
   */
  public List<Sale> fetchForProductType(String productType) {
    return saleCache.getOrDefault(productType, new ArrayList<>());
  }

  /**
   * Records Sale for the given {@code productType}
   *
   * @param productType
   * @param saleValue
   */
  public void recordSale(String productType, BigDecimal saleValue) {
    saleCache.computeIfAbsent(productType, s -> new ArrayList<>()).add(new Sale(productType, saleValue));
  }
}
