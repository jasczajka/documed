package com.documed.backend.notifications.mails;

import com.documed.backend.notifications.config.EmailConfig;
import com.documed.backend.notifications.model.BaseEmailTemplate;

public class SimpleMail extends BaseEmailTemplate {
  private final String subject;
  private final String body;

  public SimpleMail(EmailConfig emailConfig, String recipient, String subject, String body) {
    super(emailConfig, recipient);
    this.subject = subject;
    this.body = body;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public String getBody() {
    return body;
  }
}
