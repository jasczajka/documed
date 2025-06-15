package com.documed.backend.notifications.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EmailConfig {
  private final String senderName;
  private final String fromAddress;

  public EmailConfig(
      @Value("${app.email.sender-name:DocuMed}") String senderName,
      @Value("${app.email.from-address}") String fromAddress) {
    this.senderName = senderName;
    this.fromAddress = fromAddress;
  }

  public String buildFromAddress() {
    return String.format("%s <%s>", senderName, fromAddress);
  }
}
