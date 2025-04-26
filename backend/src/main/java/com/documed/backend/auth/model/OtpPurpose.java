package com.documed.backend.auth.model;

public enum OtpPurpose {
  REGISTRATION("weryfikacja konta", "Twój kod weryfikacyjny DocuMed"),
  PASSWORD_RESET("reset hasła", "DocuMed - kod do resetu hasła");

  private final String actionDescription;
  private final String subject;

  OtpPurpose(String actionDescription, String subject) {
    this.actionDescription = actionDescription;
    this.subject = subject;
  }

  public String getActionDescription() {
    return actionDescription;
  }

  public String getSubject() {
    return subject;
  }
}
