package com.example.demo.data;

import com.example.demo.notifications.SaleAdjustmentOperation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain object for Sale adjustment details
 * <p>
 * Ideally there would be an ID field generated when this sale adjustment is persisted to uniquely identify this Sale adjustment.
 * Also a set of audit fields i.e createdBy, createdTs, UpdatedBy, UpdatedTs
 */
public class SaleAdjustment {

  private int saleAdjustmentId;
  private final String productType;
  private final SaleAdjustmentOperation saleAdjustmentOperation;
  private final BigDecimal adjustmentValue;


  /**
   * Initialises {@link SaleAdjustment} for the given parameters
   *
   * @param productType
   * @param saleAdjustmentOperation
   * @param adjustmentValue
   */
  public SaleAdjustment(String productType, SaleAdjustmentOperation saleAdjustmentOperation, BigDecimal adjustmentValue) {
    this.productType = productType;
    this.saleAdjustmentOperation = saleAdjustmentOperation;
    this.adjustmentValue = adjustmentValue;
  }

  /**
   * Initialises {@link SaleAdjustment} for the given parameters
   *
   * @param productType
   * @param saleAdjustmentOperation
   * @param adjustmentValue
   * @return a new instance of {@link SaleAdjustment}
   */
  public static SaleAdjustment from(String productType, String saleAdjustmentOperation, String adjustmentValue) {
    return new SaleAdjustment(productType, SaleAdjustmentOperation.valueOf(saleAdjustmentOperation), new BigDecimal(adjustmentValue));
  }

  public int getSaleAdjustmentId() {
    return saleAdjustmentId;
  }

  public void setSaleAdjustmentId(int saleAdjustmentId) {
    this.saleAdjustmentId = saleAdjustmentId;
  }

  public String getProductType() {
    return productType;
  }

  public SaleAdjustmentOperation getSaleAdjustmentOperation() {
    return saleAdjustmentOperation;
  }

  public BigDecimal getAdjustmentValue() {
    return adjustmentValue;
  }

  /**
   * Applies the sale adjustment on the given {@link Sale}
   *
   * @param sale
   */
  public void apply(Sale sale) {
    saleAdjustmentOperation.adjust(sale, adjustmentValue);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SaleAdjustment that = (SaleAdjustment) o;
    return getSaleAdjustmentId() == that.getSaleAdjustmentId() &&
      Objects.equals(getProductType(), that.getProductType()) &&
      getSaleAdjustmentOperation() == that.getSaleAdjustmentOperation() &&
      Objects.equals(getAdjustmentValue(), that.getAdjustmentValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSaleAdjustmentId(), getProductType(), getSaleAdjustmentOperation(), getAdjustmentValue());
  }
}
