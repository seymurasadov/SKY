package com.sky.exception;

public abstract class NotFoundException extends RuntimeException {

  protected NotFoundException(String model, String key, String value) {
    super(String.format("%s not found with %s: %s", model, key, value));
  }
}
