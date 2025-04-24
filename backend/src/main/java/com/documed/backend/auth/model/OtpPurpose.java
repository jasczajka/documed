package com.documed.backend.auth.model;

public enum OtpPurpose {
  REGISTRATION("weryfikacja konta"),
  PASSWORD_RESET("reset hasła");

  private final String actionDescription;

  OtpPurpose(String actionDescription) {
    this.actionDescription = actionDescription;
  }

  public String getActionDescription() {
    return actionDescription;
  }
}
