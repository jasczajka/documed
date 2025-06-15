package com.documed.backend.notifications;

import com.documed.backend.notifications.model.EmailMessageTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {
  private final JavaMailSender mailSender;

  public void send(EmailMessageTemplate template) {
    if (!isValidEmail(template.getRecipient())) {
      log.warn("Attempt to send email to invalid address: {}", template.getRecipient());
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(template.getRecipient().trim());
    message.setFrom(template.getFrom());
    message.setSubject(template.getSubject());
    message.setText(template.getBody());

    mailSender.send(message);
  }

  private boolean isValidEmail(String email) {
    return StringUtils.hasText(email) && email.contains("@");
  }
}
