package com.documed.backend.auth

import com.documed.backend.auth.model.OtpPurpose
import com.documed.backend.others.EmailService
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification

class EmailServiceTest extends Specification {
	def mailSender = Mock(JavaMailSender)
	def senderName = 'TestSender'
	def fromAddress = 'test@documed.com'
	def emailService = new EmailService(mailSender, senderName, fromAddress)

	def "sendEmail should not send when email is invalid"() {
		given:
		def invalidEmail = 'invalidEmail'

		when:
		emailService.sendEmail(invalidEmail, 'Subject', 'Body')

		then:
		0 * mailSender.send(_)
	}

	def "sendEmail should send a SimpleMailMessage with correct fields when email is valid"() {
		given:
		def to = ' user@example.com '
		def subject = 'Test Subject'
		def body = 'This is a test.'

		when:
		emailService.sendEmail(to, subject, body)

		then:
		1 * mailSender.send({ SimpleMailMessage msg ->
			assert msg.to == ['user@example.com']
			assert msg.from == "${senderName} <${fromAddress}>"
			assert msg.subject == subject
			assert msg.text == body
			true
		})
	}

	def "sendOtpEmail should return false and not send when OTP email is invalid"() {
		when:
		def result = emailService.sendOtpEmail('', '123456', OtpPurpose.REGISTRATION)

		then:
		!result
		0 * mailSender.send(_)
	}

	def "sendOtpEmail should return true and send OTP email with correct content when valid"() {
		given:
		def toEmail = 'user@example.com'
		def otpCode = '654321'
		def purpose = OtpPurpose.REGISTRATION

		when:
		def result = emailService.sendOtpEmail(toEmail, otpCode, purpose)

		then:
		result
		1 * mailSender.send({ SimpleMailMessage msg ->
			assert msg.to == [toEmail]
			assert msg.from == "${senderName} <${fromAddress}>"
			assert msg.subject == purpose.getSubject()
			assert msg.text.contains(otpCode)
			assert msg.text.contains(purpose.getActionDescription())
			assert msg.text.contains('Kod będzie ważny przez') || msg.text.contains('kod będzie ważny przez')
			true
		})
	}
}
