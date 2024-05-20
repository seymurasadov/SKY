package com.sky.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("""
    hasAnyRole(T(com.sky.enums.UserAuthority).USER.name(),
    T(com.sky.enums.UserAuthority).ADMIN.name())
  """)
public @interface UserOrAdminPermission {
}
