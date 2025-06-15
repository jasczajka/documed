package com.documed.backend.notifications.mails;

import com.documed.backend.notifications.config.EmailConfig;
import com.documed.backend.notifications.model.BaseEmailTemplate;
import java.time.LocalDate;

public class VisitReminderEmail extends BaseEmailTemplate {
  private final LocalDate visitDate;
  private final String doctorName;

  public VisitReminderEmail(
      EmailConfig emailConfig, String recipient, LocalDate visitDate, String doctorName) {
    super(emailConfig, recipient);
    this.visitDate = visitDate;
    this.doctorName = doctorName;
  }

  @Override
  public String getSubject() {
    return "Przypomnienie o jutrzejszej wizycie";
  }

  @Override
  public String getBody() {
    return String.format(
        """
                Szanowny Pacjencie,

                Przypominamy o zaplanowanej wizycie u %s w dniu %s.

                Z poważaniem,
                Zespół %s
                """,
        doctorName, visitDate.toString(), emailConfig.getSenderName());
  }
}
