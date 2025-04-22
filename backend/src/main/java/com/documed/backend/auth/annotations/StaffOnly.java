package com.documed.backend.auth.annotations;

import java.lang.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'WARD_CLERK', 'ADMINISTRATOR')")
public @interface StaffOnly {}
