package com.documed.backend.auth.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
    "hasAnyRole('DOCTOR', 'NURSE', 'WARD_CLERK', 'ADMINISTRATOR') or "
        + "(hasRole('PATIENT') and #userId == @securityService.getCurrentUserId())")
public @interface StaffOnlyOrSelf {
  String userId() default "#userId";
}
