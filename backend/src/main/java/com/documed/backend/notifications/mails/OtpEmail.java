package com.documed.backend.notifications.mails;

import com.documed.backend.auth.model.OtpPurpose;
import com.documed.backend.notifications.config.EmailConfig;
import com.documed.backend.notifications.model.BaseEmailTemplate;

public class OtpEmail extends BaseEmailTemplate {
  private static final int OTP_EXPIRY_MINUTES = 5;
  private final String otpCode;
  private final OtpPurpose purpose;

  public OtpEmail(EmailConfig emailConfig, String recipient, String otpCode, OtpPurpose purpose) {
    super(emailConfig, recipient);
    this.otpCode = otpCode;
    this.purpose = purpose;
  }

  @Override
  public String getSubject() {
    return purpose.getSubject();
  }

  @Override
  public String getBody() {
    return String.format(
        """
                Szanowny Użytkowniku,

                Twój kod %s to: %s
                Kod będzie ważny przez %d minut.

                Jeżeli nie żądałeś/łaś tego kodu, zignoruj tę wiadomość.

                Z poważaniem,
                Zespół %s
                """,
        purpose.getActionDescription(), otpCode, OTP_EXPIRY_MINUTES, emailConfig.getSenderName());
  }
}
