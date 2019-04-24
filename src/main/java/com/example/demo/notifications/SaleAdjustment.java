package com.example.demo.notifications;

import com.example.demo.data.Sale;

import java.math.BigDecimal;

/**
 * Domain object for Sale adjustment details
 */
public class SaleAdjustment {

  private final SaleAdjustmentOperation saleAdjustmentOperation;
  private final BigDecimal adjustmentValue;


  /**
   * Initialises {@link SaleAdjustment} for the given parameters
   *
   * @param saleAdjustmentOperation
   * @param adjustmentValue
   */
  public SaleAdjustment(SaleAdjustmentOperation saleAdjustmentOperation, BigDecimal adjustmentValue) {
    this.saleAdjustmentOperation = saleAdjustmentOperation;
    this.adjustmentValue = adjustmentValue;
  }

  /**
   * Initialises {@link SaleAdjustment} for the given parameters
   *
   * @param saleAdjustmentOperation
   * @param adjustmentValue
   * @return a new instance of {@link SaleAdjustment}
   */
  public static SaleAdjustment from(String saleAdjustmentOperation, String adjustmentValue) {
    return new SaleAdjustment(SaleAdjustmentOperation.valueOf(saleAdjustmentOperation), new BigDecimal(adjustmentValue));
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
