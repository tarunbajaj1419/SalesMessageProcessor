package com.example.demo.data;

import java.math.BigDecimal;

/**
 * Domain object for Sale details
 *
 * Ideally there would be an ID field generated when this sale is persisted to uniquely identify this Sale.
 */
public class Sale {

  private final String productType;
  private BigDecimal value;

  /**
   * Initialises {@link Sale} for the given parameters
   *
   * @param productType
   * @param value
   */
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
