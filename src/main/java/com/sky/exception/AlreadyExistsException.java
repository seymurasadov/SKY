package com.sky.exception;

public abstract class AlreadyExistsException extends RuntimeException {

  protected AlreadyExistsException(String model, String key, String value) {
    super(String.format("%s already exists with %s: %s", model, key, value));
  }
}
