package com.example.demo.exceptions;

/**
 * Created by tarunbajaj on 24/04/2019.
 */
public class SaleProcessingException extends Exception {

  public SaleProcessingException(String message) {
    super(message);
  }

  public SaleProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
