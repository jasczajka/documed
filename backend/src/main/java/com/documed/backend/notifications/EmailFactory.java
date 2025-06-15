package com.documed.backend.notifications;

import com.documed.backend.auth.model.OtpPurpose;
import com.documed.backend.notifications.config.EmailConfig;
import com.documed.backend.notifications.mails.*;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailFactory {
  private final EmailConfig emailConfig;

  public OtpEmail createOtpEmail(String recipient, String otpCode, OtpPurpose purpose) {
    return new OtpEmail(emailConfig, recipient, otpCode, purpose);
  }

  public CancelVisitEmail createCancelVisitEmail(String recipient, LocalDate visitDate) {
    return new CancelVisitEmail(emailConfig, recipient, visitDate);
  }

  public VisitReminderEmail createVisitReminderEmail(
      String recipient, LocalDate visitDate, String doctorName) {
    return new VisitReminderEmail(emailConfig, recipient, visitDate, doctorName);
  }

  public AdditionalServiceUpdateEmail createAdditionalServiceUpdateEmail(String recipient) {
    return new AdditionalServiceUpdateEmail(emailConfig, recipient);
  }

  public SimpleMail createSimpleEmail(String recipient, String subject, String body) {
    return new SimpleMail(emailConfig, recipient, subject, body);
  }
}
