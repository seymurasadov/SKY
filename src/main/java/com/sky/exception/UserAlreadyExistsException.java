package com.sky.exception;

public class UserAlreadyExistsException extends AlreadyExistsException {

  public UserAlreadyExistsException(String key, String value) {
    super("User", key, value);
  }
}
