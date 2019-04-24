package com.example.demo.notifications;

/**
 * Created by tarunbajaj on 23/04/2019.
 */
public final class SaleNotification {

  private String productType;
  private String saleValue;
  private int noOfOccurrences = 1;
  private String adjustmentOperation;
  private String adjustmentValue;

  public SaleNotification() {
  }

  public SaleNotification(String productType, String saleValue, int noOfOccurrences) {
    this.productType = productType;
    this.saleValue = saleValue;
    this.noOfOccurrences = noOfOccurrences;
  }

  public SaleNotification(String productType, String saleValue, int noOfOccurrences, String adjustmentOperation, String adjustmentValue) {
    this.productType = productType;
    this.saleValue = saleValue;
    this.noOfOccurrences = noOfOccurrences;
    this.adjustmentOperation = adjustmentOperation;
    this.adjustmentValue = adjustmentValue;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getSaleValue() {
    return saleValue;
  }

  public void setSaleValue(String saleValue) {
    this.saleValue = saleValue;
  }

  public int getNoOfOccurrences() {
    return noOfOccurrences;
  }

  public void setNoOfOccurrences(int noOfOccurrences) {
    this.noOfOccurrences = noOfOccurrences;
  }

  public String getAdjustmentOperation() {
    return adjustmentOperation;
  }

  public void setAdjustmentOperation(String adjustmentOperation) {
    this.adjustmentOperation = adjustmentOperation;
  }

  public String getAdjustmentValue() {
    return adjustmentValue;
  }

  public void setAdjustmentValue(String adjustmentValue) {
    this.adjustmentValue = adjustmentValue;
  }
}
