package com.example.demo.notifications;

import com.example.demo.data.Sale;

import java.math.BigDecimal;

/**
 * Created by tarunbajaj on 23/04/2019.
 */
public class SaleAdjustment {

  private final SaleAdjustmentOperation saleAdjustmentOperation;
  private final BigDecimal adjustmentValue;


  public SaleAdjustment(SaleAdjustmentOperation saleAdjustmentOperation, BigDecimal adjustmentValue) {
    this.saleAdjustmentOperation = saleAdjustmentOperation;
    this.adjustmentValue = adjustmentValue;
  }

  public static SaleAdjustment from(String saleAdjustmentOperation, String adjustmentValue) {
    return new SaleAdjustment(SaleAdjustmentOperation.valueOf(saleAdjustmentOperation), new BigDecimal(adjustmentValue));
  }

  public SaleAdjustmentOperation getSaleAdjustmentOperation() {
    return saleAdjustmentOperation;
  }

  public BigDecimal getAdjustmentValue() {
    return adjustmentValue;
  }

  public void apply(Sale sale) {
    saleAdjustmentOperation.adjust(sale, adjustmentValue);
  }
}
