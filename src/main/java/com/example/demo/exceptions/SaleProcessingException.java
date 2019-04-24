package com.example.demo.exceptions;

/**
 * A specialised exception for Sale processing errors
 */
public class SaleProcessingException extends Exception {

  public SaleProcessingException(String message) {
    super(message);
  }

  public SaleProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
