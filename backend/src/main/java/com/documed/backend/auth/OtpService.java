package com.documed.backend.auth;

import com.documed.backend.auth.dtos.OtpGenerationResponse;
import com.documed.backend.auth.exceptions.*;
import com.documed.backend.auth.model.Otp;
import com.documed.backend.auth.model.OtpPurpose;
import com.documed.backend.users.services.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpService {
  private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

  private final OtpDAO otpDAO;
  private final EmailService emailService;
  private final UserService userService;

  @Value("${otp.length:6}")
  private int otpLength;

  @Value("${otp.expiration.minutes:5}")
  private int otpExpirationMinutes;

  @Value("${otp.max.attempts:5}")
  private int maxOtpAttempts;

  @Value("${otp.resend.cooldown.minutes:1}")
  private int resendCooldownMinutes;

  public OtpService(OtpDAO otpDAO, EmailService emailService, UserService userService) {
    this.otpDAO = otpDAO;
    this.emailService = emailService;
    this.userService = userService;
  }

  @Transactional
  public OtpGenerationResponse generateOtp(String email, OtpPurpose purpose) {
    if (purpose == OtpPurpose.PASSWORD_RESET) {
      userService
          .getByEmail(email)
          .orElseThrow(() -> new OtpException("User not found with email: " + email));
    }

    Optional<Otp> recentOtp = otpDAO.findLatestByEmailAndPurpose(email, purpose);
    if (recentOtp.isPresent()) {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime lastGeneratedAt = recentOtp.get().getGeneratedAt();
      long minutesSinceLastOtp =
          TimeUnit.MINUTES.convert(
              java.time.Duration.between(lastGeneratedAt, now).toNanos(), TimeUnit.NANOSECONDS);

      if (minutesSinceLastOtp < resendCooldownMinutes) {
        long remainingTime = resendCooldownMinutes - minutesSinceLastOtp;
        throw new OtpLimitExceededException(
            "Please wait " + remainingTime + " minutes before requesting a new OTP");
      }
    }

    String otpCode = generateRandomOtp();
    LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

    Otp otp =
        Otp.builder()
            .email(email)
            .otp(otpCode)
            .purpose(purpose)
            .generatedAt(LocalDateTime.now())
            .expiresAt(expirationTime)
            .attempts(0)
            .used(false)
            .build();

    otpDAO.create(otp);

    emailService.sendOtpEmail(email, otpCode, purpose);

    logger.info("Generated OTP for {} with purpose {}", email, purpose);

    return OtpGenerationResponse.builder()
        .email(email)
        .purpose(purpose)
        .expiresAt(expirationTime)
        .message("OTP generated successfully")
        .build();
  }

  @Transactional
  public boolean validateOtp(String email, String otp, OtpPurpose purpose) {
    Optional<Otp> otpEntity = otpDAO.findByEmailAndOtpAndPurpose(email, otp, purpose);

    if (otpEntity.isEmpty()) {
      logger.warn("Invalid OTP attempt for {}", email);
      throw new OtpNotFoundException("Invalid OTP");
    }

    Otp otpRecord = otpEntity.get();

    if (LocalDateTime.now().isAfter(otpRecord.getExpiresAt())) {
      logger.warn("Expired OTP attempt for {}", email);
      throw new OtpExpiredException("OTP has expired");
    }

    if (otpRecord.getAttempts() >= maxOtpAttempts) {
      logger.warn("Max OTP attempts reached for {}", email);
      throw new OtpLimitExceededException("Maximum OTP attempts reached");
    }

    otpRecord.setAttempts(otpRecord.getAttempts() + 1);
    otpDAO.update(otpRecord);

    if (otp.equals(otpRecord.getOtp())) {
      otpRecord.setUsed(true);
      otpDAO.update(otpRecord);
      logger.info("Valid OTP for {}", email);
      return true;
    }

    return false;
  }

  private String generateRandomOtp() {
    Random random = new Random();
    StringBuilder otp = new StringBuilder();

    for (int i = 0; i < otpLength; i++) {
      otp.append(random.nextInt(10));
    }

    return otp.toString();
  }
}
