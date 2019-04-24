package com.example.demo.data;

import java.math.BigDecimal;

/**
 * Created by tarunbajaj on 23/04/2019.
 */
public class Sale {

  private final String productType;
  private BigDecimal value;

  public Sale(String productType, BigDecimal value) {
    this.productType = productType;
    this.value = value;
  }

  public String getProductType() {
    return productType;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }
}
