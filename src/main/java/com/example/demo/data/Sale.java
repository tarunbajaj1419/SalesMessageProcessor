package com.example.demo.data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain object for Sale details
 * <p>
 * Ideally there would be an ID field generated when this sale is persisted to uniquely identify this Sale.
 * Also a set of audit fields i.e createdBy, createdTs, UpdatedBy, UpdatedTs
 */
public class Sale {

  private int saleId;
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

  public int getSaleId() {
    return saleId;
  }

  public void setSaleId(int saleId) {
    this.saleId = saleId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Sale sale = (Sale) o;
    return getSaleId() == sale.getSaleId() &&
      Objects.equals(getProductType(), sale.getProductType()) &&
      Objects.equals(getValue(), sale.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSaleId(), getProductType(), getValue());
  }
}
