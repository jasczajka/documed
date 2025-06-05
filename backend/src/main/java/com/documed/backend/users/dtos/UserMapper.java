package com.documed.backend.users.dtos;

import com.documed.backend.users.model.User;

public class UserMapper {

  public static PatientDetailsDTO toPatientDetailsDTO(User user) {
    return PatientDetailsDTO.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .birthdate(user.getBirthDate())
        .pesel(user.getPesel())
        .subscriptionId(user.getSubscriptionId())
        .build();
  }
}
