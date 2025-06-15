package com.documed.backend.notifications.mails;

import com.documed.backend.notifications.config.EmailConfig;
import com.documed.backend.notifications.model.BaseEmailTemplate;

public class AdditionalServiceUpdateEmail extends BaseEmailTemplate {

  public AdditionalServiceUpdateEmail(EmailConfig emailConfig, String recipient) {
    super(emailConfig, recipient);
  }

  @Override
  public String getSubject() {
    return "Nowe wyniki badań dostępne";
  }

  @Override
  public String getBody() {
    return String.format(
        """
                Szanowny Pacjencie,

                Informujemy, że nowe informacje dotyczące wykonywanej u nas usługi są już dostępne w systemie.

                Z poważaniem,
                Zespół %s
                """,
        emailConfig.getSenderName());
  }
}
