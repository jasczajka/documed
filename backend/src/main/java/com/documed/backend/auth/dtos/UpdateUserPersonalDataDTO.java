package com.documed.backend.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO for updating user personal data")
public class UpdateUserPersonalDataDTO {

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "First name is required") @Size(max = 255, message = "First name must be at most 255 characters") private String firstName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Last name is required") @Size(max = 255, message = "Last name must be at most 255 characters") private String lastName;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email is required") @Email(message = "Email should be valid") @Size(max = 255, message = "Email must be at most 255 characters") private String email;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(max = 255, message = "Address can be at most 255 characters") private String address;

  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(max = 255, message = "Phone number must be at most 255 characters") private String phoneNumber;
}
