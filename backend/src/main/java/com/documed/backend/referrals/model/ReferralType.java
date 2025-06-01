package com.documed.backend.referrals.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReferralType {
  TO_SPECIALIST("TO_SPECIALIST", "Skierowanie do specjalisty"),
  TO_HOSPITAL("TO_HOSPITAL", "Skierowanie do szpitala"),
  FOR_DIAGNOSTICS("FOR_DIAGNOSTICS", "Skierowanie na badania diagnostyczne"),
  FOR_REHABILITATION("FOR_REHABILITATION", "Skierowanie na rehabilitację"),
  TO_SANATORIUM("TO_SANATORIUM", "Skierowanie do sanatorium"),
  FOR_LONG_TERM_CARE("FOR_LONG_TERM_CARE", "Skierowanie do opieki długoterminowej"),
  FOR_PSYCHIATRIC_CARE("FOR_PSYCHIATRIC_CARE", "Skierowanie psychiatryczne lub uzależnienia");

  private final String code;
  private final String description;

  @JsonValue
  public Map<String, String> toJson() {
    return Map.of("code", code, "description", description);
  }

  @JsonCreator
  public static ReferralType fromCode(String code) {
    return Stream.of(ReferralType.values())
        .filter(t -> t.code.equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown ReferralType: " + code));
  }
}
