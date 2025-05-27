package com.documed.backend.auth


import com.documed.backend.auth.exceptions.*
import com.documed.backend.auth.model.Otp
import com.documed.backend.auth.model.OtpPurpose
import com.documed.backend.users.services.UserService
import java.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Subject

class OtpServiceTest extends Specification {
	def otpDAO = Mock(OtpDAO)
	def emailService = Mock(EmailService)
	def userService = Mock(UserService)

	@Subject
	OtpService otpService = new OtpService(otpDAO, emailService, userService)

	def setup() {
		// default config values
		otpService.otpLength = 6
		otpService.otpExpirationMinutes = 5
		otpService.maxOtpAttempts = 5
		otpService.resendCooldownMinutes = 1
	}

	def "generateOtp should throw when user not found for password reset"() {
		given:
		def email = 'unknown@example.com'
		otpDAO.findLatestByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET) >> Optional.empty()
		userService.getByEmail(email) >> Optional.empty()

		when:
		otpService.generateOtp(email, OtpPurpose.PASSWORD_RESET)

		then:
		def e = thrown(OtpException)
		e.message.contains("User not found with email: $email")
	}

	def "generateOtp should enforce resend cooldown"() {
		given:
		def email = 'user@example.com'
		def past = LocalDateTime.now().minusSeconds(30)
		otpDAO.findLatestByEmailAndPurpose(email, OtpPurpose.REGISTRATION) >> Optional.of(
				Otp.builder()
				.email(email)
				.purpose(OtpPurpose.REGISTRATION)
				.otp('123456')
				.generatedAt(past)
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.attempts(0)
				.used(false)
				.build()
				)

		when:
		otpService.generateOtp(email, OtpPurpose.REGISTRATION)

		then:
		def e = thrown(OtpLimitExceededException)
		e.message.contains('Please wait')
	}

	def "generateOtp should create and send OTP successfully"() {
		given:
		def email = 'user@example.com'
		otpDAO.findLatestByEmailAndPurpose(email, OtpPurpose.REGISTRATION) >> Optional.empty()

		when:
		def response = otpService.generateOtp(email, OtpPurpose.REGISTRATION)

		then:
		1 * otpDAO.create({ Otp otp ->
			assert otp.email == email
			assert otp.purpose == OtpPurpose.REGISTRATION
			assert otp.otp.length() == otpService.otpLength
			assert otp.attempts == 0
			assert !otp.used
			assert otp.expiresAt.isAfter(otp.generatedAt)
			true
		})
		1 * emailService.sendOtpEmail(email, _ as String, OtpPurpose.REGISTRATION)
		response.email == email
		response.purpose == OtpPurpose.REGISTRATION
		response.message == 'OTP generated successfully'
	}

	def "validateOtp should throw when OTP not found"() {
		given:
		def email = 'user@example.com'
		otpDAO.findByEmailAndOtpAndPurpose(email, '000000', OtpPurpose.REGISTRATION) >> Optional.empty()

		when:
		otpService.validateOtp(email, '000000', OtpPurpose.REGISTRATION)

		then:
		thrown(OtpNotFoundException)
	}

	def "validateOtp should throw when OTP expired"() {
		given:
		def email = 'user@example.com'
		def expired = Otp.builder()
				.email(email)
				.purpose(OtpPurpose.REGISTRATION)
				.otp('123456')
				.generatedAt(LocalDateTime.now().minusMinutes(10))
				.expiresAt(LocalDateTime.now().minusMinutes(5))
				.attempts(0)
				.used(false)
				.build()
		otpDAO.findByEmailAndOtpAndPurpose(email, '123456', OtpPurpose.REGISTRATION) >> Optional.of(expired)

		when:
		otpService.validateOtp(email, '123456', OtpPurpose.REGISTRATION)

		then:
		thrown(OtpExpiredException)
	}

	def "validateOtp should throw when max attempts reached"() {
		given:
		def email = 'user@example.com'
		def record = Otp.builder()
				.email(email)
				.purpose(OtpPurpose.REGISTRATION)
				.otp('123456')
				.generatedAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.attempts(otpService.maxOtpAttempts)
				.used(false)
				.build()
		otpDAO.findByEmailAndOtpAndPurpose(email, '123456', OtpPurpose.REGISTRATION) >> Optional.of(record)

		when:
		otpService.validateOtp(email, '123456', OtpPurpose.REGISTRATION)

		then:
		thrown(OtpLimitExceededException)
	}

	def "validateOtp should return true and mark used when correct OTP"() {
		given:
		def email = 'user@example.com'
		def otpCode = '123456'
		def purpose = OtpPurpose.REGISTRATION
		def record = Otp.builder()
				.email(email)
				.otp(otpCode)
				.purpose(purpose)
				.generatedAt(LocalDateTime.now().minusMinutes(1))
				.expiresAt(LocalDateTime.now().plusMinutes(4))
				.attempts(0)
				.used(false)
				.build()
		otpDAO.findByEmailAndOtpAndPurpose(email, otpCode, purpose) >> Optional.of(record)

		when:
		def result = otpService.validateOtp(email, otpCode, purpose)

		then:
		result
		1 * otpDAO.update({ it.attempts == 1 })
		1 * otpDAO.update({ it.used })
	}

	def "validateOtp should return false for incorrect OTP and increment attempts"() {
		given:
		def email = 'user@example.com'
		def otpCode = '123456'
		def wrongCode = '000000'
		def purpose = OtpPurpose.REGISTRATION
		def record = Otp.builder()
				.email(email)
				.otp(otpCode)
				.purpose(purpose)
				.generatedAt(LocalDateTime.now().minusMinutes(1))
				.expiresAt(LocalDateTime.now().plusMinutes(4))
				.attempts(1)
				.used(false)
				.build()
		otpDAO.findByEmailAndOtpAndPurpose(email, wrongCode, purpose) >> Optional.of(record)

		when:
		def result = otpService.validateOtp(email, wrongCode, purpose)

		then:
		!result
		1 * otpDAO.update({ it.attempts == 2 })
	}
}
