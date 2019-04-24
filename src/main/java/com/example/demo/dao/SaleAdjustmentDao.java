package com.example.demo.dao;

import com.example.demo.notifications.SaleAdjustment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO implementation for {@link SaleAdjustment} instances
 * <p>
 * Using a local Cache for the exercise
 */
@Repository
public class SaleAdjustmentDao {

  private Map<String, List<SaleAdjustment>> saleAdjustmentCache = new HashMap<>();

  /**
   * Clears SaleAdjustment for the given {@code productType}
   *
   * @param productType
   */
  public void clearSaleAdjustmentsForProductType(String productType) {
    saleAdjustmentCache.computeIfPresent(productType, (k, v) -> new ArrayList<>());
  }

  /**
   * Fetches all SaleAdjustments
   *
   * @return Map of all sales by product type
   */
  public Map<String, List<SaleAdjustment>> fetchAllSaleAdjustments() {
    return new HashMap<>(saleAdjustmentCache);
  }

  /**
   * Fetches SaleAdjustments for the given {@code productType}
   *
   * @param productType
   * @return List of sales
   */
  public List<SaleAdjustment> fetchForProductType(String productType) {
    return saleAdjustmentCache.getOrDefault(productType, new ArrayList<>());
  }

  /**
   * Persists SaleAdjustment for the given {@code productType}
   *
   * @param saleAdjustment
   */
  public void save(SaleAdjustment saleAdjustment) {
    saleAdjustmentCache.computeIfAbsent(saleAdjustment.getProductType(), s -> new ArrayList<>()).add(saleAdjustment);
  }
}
