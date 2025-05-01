package com.documed.backend.auth.dtos;

import com.documed.backend.users.model.AccountStatus;
import com.documed.backend.users.model.UserRole;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingUserDTO {
  private Integer id;
  private String firstName;
  private String lastName;
  private String email;
  private String pesel;
  private String phoneNumber;
  private String address;
  private Date birthDate;
  private UserRole role;
  private AccountStatus accountStatus;
}
