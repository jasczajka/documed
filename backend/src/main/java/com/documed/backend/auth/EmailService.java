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

  public void sendEmail(String to, String subject, String body) {
    if (!isValidEmail(to)) {
      logger.warn("Attempt to send email to invalid address: {}", to);
      return;
    }
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to.trim());
    message.setFrom(buildFromAddress());
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
    logger.info("Email sent to {} with subject '{}'", to, subject);
  }

  public boolean sendOtpEmail(String toEmail, String otpCode, OtpPurpose purpose) {
    if (!isValidEmail(toEmail)) {
      logger.warn("Trying to send OTP to: {}", toEmail);
      return false;
    }

    SimpleMailMessage message = buildOtpMessage(toEmail, otpCode, purpose);
    mailSender.send(message);
    logger.debug("OTP code sent to {}", toEmail);
    return true;
  }

  private SimpleMailMessage buildOtpMessage(String toEmail, String otpCode, OtpPurpose purpose) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail.trim());
    message.setFrom(buildFromAddress());
    message.setSubject(purpose.getSubject());
    message.setText(buildOtpEmailBody(otpCode, purpose));
    return message;
  }

  private String buildFromAddress() {
    return String.format("%s <%s>", senderName, fromAddress);
  }

  private String buildOtpEmailBody(String otpCode, OtpPurpose purpose) {
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
