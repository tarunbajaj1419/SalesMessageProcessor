package com.example.demo.notifications;

import com.example.demo.data.Sale;
import com.example.demo.data.SaleAdjustment;

import java.math.BigDecimal;

/**
 * List of {@link SaleAdjustment} Operators
 */
public enum SaleAdjustmentOperation {

  ADD {
    @Override
    public void adjust(Sale sale, BigDecimal adjustmentValue) {
      sale.setValue(sale.getValue().add(adjustmentValue));
    }
  },
  SUBTRACT {
    @Override
    public void adjust(Sale sale, BigDecimal adjustmentValue) {
      sale.setValue(sale.getValue().subtract(adjustmentValue));
    }
  },
  MULTIPLY {
    @Override
    public void adjust(Sale sale, BigDecimal adjustmentValue) {
      sale.setValue(sale.getValue().multiply(adjustmentValue));
    }
  };

  /**
   * Applies the sale adjustment operator on the given {@link Sale}
   *
   * @param sale
   * @param adjustmentValue
   */
  public abstract void adjust(Sale sale, BigDecimal adjustmentValue);
}
