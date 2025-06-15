package com.documed.backend.notifications;

import com.documed.backend.auth.model.OtpPurpose;
import com.documed.backend.notifications.mails.*;
import com.documed.backend.notifications.model.BaseEmailTemplate;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final EmailSenderService emailSenderService;
  private final EmailFactory emailFactory;
  private final NotificationDAO notificationDAO;

  public void sendCancelVisitEmail(String recipient, LocalDate visitDate, Integer visitId) {
    CancelVisitEmail email = emailFactory.createCancelVisitEmail(recipient, visitDate);
    sendWithNotification(email, visitId, null, NotificationType.VISIT_CANCELLATION);
  }

  public void sendVisitReminderEmail(
      String recipient, LocalDate visitDate, String doctorName, Integer visitId) {
    VisitReminderEmail email =
        emailFactory.createVisitReminderEmail(recipient, visitDate, doctorName);
    sendWithNotification(email, visitId, null, NotificationType.VISIT_REMINDER);
  }

  public void sendAdditionalServiceUpdateEmail(String recipient, Integer additionalServiceId) {
    AdditionalServiceUpdateEmail email = emailFactory.createAdditionalServiceUpdateEmail(recipient);
    sendWithNotification(
        email, null, additionalServiceId, NotificationType.ADDITIONAL_SERVICE_UPDATE);
  }

  public void sendOtpEmail(String recipient, String otpCode, OtpPurpose purpose) {
    OtpEmail email = emailFactory.createOtpEmail(recipient, otpCode, purpose);
    sendWithoutNotification(email);
  }

  public void sendSimpleEmail(String recipient, String subject, String body) {
    SimpleMail email = emailFactory.createSimpleEmail(recipient, subject, body);
    sendWithoutNotification(email);
  }

  private void sendWithNotification(
      BaseEmailTemplate email,
      Integer visitId,
      Integer additionalServiceId,
      NotificationType type) {

    int notificationId =
        notificationDAO.createNotification(
            visitId, additionalServiceId, NotificationStatus.PENDING, type);

    try {
      emailSenderService.send(email);

      notificationDAO.updateNotificationStatus(notificationId, NotificationStatus.SENT);
      log.info("Email sent to {} with subject '{}'", email.getRecipient(), email.getSubject());

    } catch (MailException e) {
      notificationDAO.updateNotificationStatus(notificationId, NotificationStatus.ERROR);
      log.error("Failed to send email to {}", email.getRecipient(), e);
      throw e;
    }
  }

  private void sendWithoutNotification(BaseEmailTemplate email) {

    try {
      emailSenderService.send(email);
      log.info("Email sent to {} with subject '{}'", email.getRecipient(), email.getSubject());

    } catch (MailException e) {
      log.error("Failed to send email to {}", email.getRecipient(), e);
      throw e;
    }
  }
}
