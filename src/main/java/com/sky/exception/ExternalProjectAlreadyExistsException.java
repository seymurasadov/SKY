package com.sky.exception;

public class ExternalProjectAlreadyExistsException extends AlreadyExistsException {

  public ExternalProjectAlreadyExistsException(String key, String value) {
    super("External Project", key, value);
  }
}
