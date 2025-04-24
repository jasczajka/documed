package com.documed.backend.auth;

import com.documed.backend.auth.model.OtpPurpose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {
  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private static final int OTP_EXPIRY_MINUTES = 5;

  private final JavaMailSender mailSender;
  private final String senderName;
  private final String fromAddress;

  public EmailService(
      JavaMailSender mailSender,
      @Value("${app.email.sender-name:DocuMed}") String senderName,
      @Value("${app.email.from-address}") String fromAddress) {
    this.mailSender = mailSender;
    this.senderName = senderName;
    this.fromAddress = fromAddress;
  }

  public boolean sendOtpEmail(String toEmail, String otpCode, OtpPurpose purpose) {
    if (!isValidEmail(toEmail)) {
      logger.warn("Próba wysłania kodu OTP na nieprawidłowy adres email: {}", toEmail);
      return false;
    }

    SimpleMailMessage message = buildOtpMessage(toEmail, otpCode, purpose);
    mailSender.send(message);
    logger.debug("Email z kodem OTP wysłany na adres {}", toEmail);
    return true;
  }

  private SimpleMailMessage buildOtpMessage(String toEmail, String otpCode, OtpPurpose purpose) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail.trim());
    message.setFrom(buildFromAddress());
    message.setSubject(buildSubject(purpose));
    message.setText(buildEmailBody(otpCode, purpose));
    return message;
  }

  private String buildFromAddress() {
    return String.format("%s <%s>", senderName, fromAddress);
  }

  private String buildSubject(OtpPurpose purpose) {
    return purpose == OtpPurpose.REGISTRATION
        ? "Twój kod weryfikacyjny DocuMed"
        : "DocuMed - kod do resetu hasła";
  }

  private String buildEmailBody(String otpCode, OtpPurpose purpose) {
    return String.format(
        """
            Szanowny Użytkowniku,

            Twój kod %s to: %s
            Kod będzie ważny przez %d minut.

            Jeżeli nie żądałeś/łaś tego kodu, zignoruj tę wiadomość.

            Z poważaniem,
            Zespół %s
            """,
        purpose.getActionDescription(), otpCode, OTP_EXPIRY_MINUTES, senderName);
  }

  private boolean isValidEmail(String email) {
    return StringUtils.hasText(email) && email.contains("@");
  }
}
