package com.documed.backend.notifications.model;

import com.documed.backend.notifications.config.EmailConfig;

public abstract class BaseEmailTemplate implements EmailMessageTemplate {
  protected final EmailConfig emailConfig;
  protected final String recipient;

  protected BaseEmailTemplate(EmailConfig emailConfig, String recipient) {
    this.emailConfig = emailConfig;
    this.recipient = recipient;
  }

  @Override
  public String getRecipient() {
    return recipient;
  }

  @Override
  public String getFrom() {
    return emailConfig.buildFromAddress();
  }
}
