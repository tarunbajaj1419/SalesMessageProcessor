package com.example.demo.dao;

import com.example.demo.data.Sale;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tarunbajaj on 23/04/2019.
 */
@Repository
public class SaleDao {

  private Map<String, List<Sale>> saleCache = new HashMap<>();

  public void clearSalesForProductType(String productType) {
    saleCache.computeIfPresent(productType, (k, v) -> new ArrayList<>());
  }

  public Map<String, List<Sale>> fetchAllSales() {
    return new HashMap<>(saleCache);
  }

  public List<Sale> fetchForProductType(String productType) {
    return saleCache.getOrDefault(productType, new ArrayList<>());
  }

  public void recordSale(String productType, BigDecimal saleValue) {
    saleCache.computeIfAbsent(productType, s -> new ArrayList<>()).add(new Sale(productType, saleValue));
  }
}
