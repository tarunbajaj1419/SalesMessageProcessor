package com.example.demo.notifications;

import com.example.demo.data.Sale;

import java.math.BigDecimal;

/**
 * Created by tarunbajaj on 23/04/2019.
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

  public abstract void adjust(Sale sale, BigDecimal adjustmentValue);
}
