package com.documed.backend.referrals.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum ReferralType {
  TO_SPECIALIST("Skierowanie do specjalisty"),
  TO_HOSPITAL("Skierowanie do szpitala"),
  FOR_DIAGNOSTICS("Skierowanie na badania diagnostyczne"),
  FOR_REHABILITATION("Skierowanie na rehabilitację"),
  TO_SANATORIUM("Skierowanie do sanatorium"),
  FOR_LONG_TERM_CARE("Skierowanie do opieki długoterminowej"),
  FOR_PSYCHIATRIC_CARE("Skierowanie psychiatryczne lub uzależnienia");

  private final String description;

  public static ReferralType fromCode(String code) {
    return Stream.of(values())
        .filter(t -> t.name().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown ReferralType: " + code));
  }
}
