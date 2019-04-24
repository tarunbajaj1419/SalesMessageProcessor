package com.example.demo.notifications;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * External Message Container for a Sale Notification
 *
 * Using primitive fields since the message is expected to come from an external source
 */
public final class SaleNotification {

  private String productType;
  private String saleValue;
  private int noOfOccurrences = 1;
  private String adjustmentOperation;
  private String adjustmentValue;

  public SaleNotification() {
  }

  /**
   * Initialises {@link SaleNotification} With sale details
   *
   * @param productType
   * @param saleValue
   * @param noOfOccurrences
   */
  public SaleNotification(String productType, String saleValue, int noOfOccurrences) {
    this.productType = productType;
    this.saleValue = saleValue;
    this.noOfOccurrences = noOfOccurrences;
  }

  /**
   * Initialises {@link SaleNotification} With sale details and an adjustment operator
   *
   * @param productType
   * @param saleValue
   * @param noOfOccurrences
   * @param adjustmentOperation
   * @param adjustmentValue
   */
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

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("productType", productType)
      .append("saleValue", saleValue)
      .append("noOfOccurrences", noOfOccurrences)
      .append("adjustmentOperation", adjustmentOperation)
      .append("adjustmentValue", adjustmentValue)
      .toString();
  }
}
