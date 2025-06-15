package com.documed.backend.notifications.mails;

import com.documed.backend.notifications.config.EmailConfig;
import com.documed.backend.notifications.model.BaseEmailTemplate;
import java.time.LocalDate;

public class CancelVisitEmail extends BaseEmailTemplate {
  private final LocalDate visitDate;

  public CancelVisitEmail(EmailConfig emailConfig, String recipient, LocalDate visitDate) {
    super(emailConfig, recipient);
    this.visitDate = visitDate;
  }

  @Override
  public String getSubject() {
    return "Twoja wizyta została anulowana.";
  }

  @Override
  public String getBody() {
    return String.format(
        """
                Szanowny Użytkowniku,

                Informujemy, że wizyta na dzień %s została anulowana.

                W razie wątpliwości prosimy o kontakt z placówką.

                Z poważaniem,
                Zespół %s
                """,
        visitDate.toString(), emailConfig.getSenderName());
  }
}
