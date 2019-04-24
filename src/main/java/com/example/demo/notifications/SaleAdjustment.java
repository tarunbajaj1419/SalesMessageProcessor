package com.example.demo.notifications;

import com.example.demo.data.Sale;

import java.math.BigDecimal;

/**
 * Domain object for Sale adjustment details
 * <p>
 * Ideally there would be an ID field generated when this sale adjustment is persisted to uniquely identify this Sale adjustment.
 */
public class SaleAdjustment {

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
}
