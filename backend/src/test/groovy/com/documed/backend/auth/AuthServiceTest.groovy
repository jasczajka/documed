package com.documed.backend.auth

import com.documed.backend.auth.exceptions.*
import com.documed.backend.auth.model.CurrentUser
import com.documed.backend.auth.model.OtpPurpose
import com.documed.backend.exceptions.NotFoundException
import com.documed.backend.schedules.WorkTimeService
import com.documed.backend.users.UserDAO
import com.documed.backend.users.model.AccountStatus
import com.documed.backend.users.model.User
import com.documed.backend.users.model.UserRole
import com.documed.backend.users.services.UserService
import com.documed.backend.visits.FacilityService
import com.documed.backend.visits.model.Facility
import java.sql.Date
import java.time.LocalDate
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

class AuthServiceTest extends Specification {
	def userDAO = Mock(UserDAO)
	def passwordEncoder = Mock(PasswordEncoder)
	def jwtUtil = Mock(JwtUtil)
	def userService = Mock(UserService)
	def otpService = Mock(OtpService)
	def emailService = Mock(EmailService)
	def facilityService = Mock(FacilityService)
	def workTimeService = Mock(WorkTimeService)

	@Subject
	AuthService authService = new AuthService(
	userDAO, passwordEncoder, jwtUtil, userService, otpService, emailService, facilityService, workTimeService
	)

	private User buildPatient(Map overrides = [:]) {
		def builder = User.builder()
		if (overrides.id != null) builder.id(overrides.id)
		builder
				.firstName(overrides.firstName ?: 'First')
				.lastName(overrides.lastName ?: 'Last')
				.email(overrides.email ?: 'user@example.com')
				.pesel(overrides.pesel ?: '12345678901')
				.phoneNumber(overrides.phoneNumber ?: '000000000')
				.address(overrides.address ?: 'Addr')
				.password(overrides.password ?: 'pass')
				.accountStatus(overrides.accountStatus ?: AccountStatus.ACTIVE)
				.role(overrides.role ?: UserRole.PATIENT)
				.birthDate(overrides.birthDate ?: LocalDate.now().minusYears(20))
		return builder.build()
	}

	private User buildDoctor(Map overrides = [:]) {
		def builder = User.builder()
		if (overrides.id != null) builder.id(overrides.id)
		builder
				.firstName(overrides.firstName ?: 'DocFirst')
				.lastName(overrides.lastName ?: 'DocLast')
				.email(overrides.email ?: 'doc@example.com')
				.pwzNumber(overrides.pwzNumber ?: 'PWZ123')
				.phoneNumber(overrides.phoneNumber ?: '111111111')
				.password(overrides.password ?: 'pass')
				.accountStatus(overrides.accountStatus ?: AccountStatus.ACTIVE)
				.role(UserRole.DOCTOR)
		return builder.build()
	}

	private Facility buildFacility(Map overrides = [:]) {
		def builder = Facility.builder()
		if (overrides.id != null) builder.id(overrides.id)
		builder
				.address(overrides.address ?: '123 Mockingbird Lane')
				.city(overrides.city ?: 'Testville')
				.visits(overrides.visits ?: [])
		return builder.build()
	}

	private User buildStaff(Map overrides = [:]) {
		def builder = User.builder()
		if (overrides.id != null) builder.id(overrides.id)
		builder
				.firstName(overrides.firstName ?: 'StaffFirst')
				.lastName(overrides.lastName ?: 'StaffLast')
				.email(overrides.email ?: 'staff@example.com')
				.password(overrides.password ?: 'pass')
				.accountStatus(overrides.accountStatus ?: AccountStatus.ACTIVE)
				.role(UserRole.valueOf(overrides.role ?: 'ADMINISTRATOR'))
		return builder.build()
	}

	def "resetPassword should validate OTP and update password and send email"() {
		given:
		def email = 'user@example.com'
		def otp = '123456'
		otpService.validateOtp(email, otp, OtpPurpose.PASSWORD_RESET) >> true
		userService.getByEmail(email) >> Optional.of(buildPatient())
		passwordEncoder.encode(_ as String) >> 'encodedNew'

		when:
		authService.resetPassword(email, otp)

		then:
		1 * userDAO.updatePasswordByEmail(email, 'encodedNew')
		1 * emailService.sendEmail(email, 'Twoje nowe hasło', _ as String)
	}

	def "resetPassword should throw when OTP invalid"() {
		given: otpService.validateOtp(_, _, _) >> { throw new OtpNotFoundException('Invalid') }
		when: authService.resetPassword('a', 'b')
		then: thrown(OtpNotFoundException)
	}

	def "registerPatient returns existing pending and sends OTP"() {
		given:
		def existing = buildPatient(accountStatus: AccountStatus.PENDING_CONFIRMATION)
		userService.getByEmail('u') >> Optional.of(existing)
		otpService.generateOtp('u', OtpPurpose.REGISTRATION) >> null
		when:
		def result = authService.registerPatient('f','l','u','pes','pwd','PATIENT','phone','addr', LocalDate.now())
		then:
		result.is(existing)
		1 * otpService.generateOtp('u', OtpPurpose.REGISTRATION)
	}

	def "registerPatient throws when user exists active"() {
		given: userService.getByEmail('u') >> Optional.of(buildPatient())
		when: authService.registerPatient('f','l','u','pes','pwd','PATIENT','phone','addr', LocalDate.now())
		then: thrown(UserAlreadyExistsException)
	}

	def "registerPatient creates new and sends OTP"() {
		given:
		userService.getByEmail('u') >> Optional.empty()
		passwordEncoder.encode('pwd') >> 'enc'
		def pending = buildPatient(id: 10, firstName: 'fn', lastName: 'ln', email: 'u', pesel: 'pes', password: 'enc', accountStatus: AccountStatus.PENDING_CONFIRMATION, role: UserRole.PATIENT, birthDate: LocalDate.of(2000,1,1))
		userService.createPendingUser(_ as User) >> pending
		otpService.generateOtp('u', OtpPurpose.REGISTRATION) >> null
		when:
		def result = authService.registerPatient('fn','ln','u','pes','pwd','PATIENT','ph','ad', LocalDate.of(2000,1,1))
		then:
		result == pending
		1 * otpService.generateOtp('u', OtpPurpose.REGISTRATION)
	}

	def "confirmRegistration validates OTP and returns DTO"() {
		given:
		otpService.validateOtp('e','o', OtpPurpose.REGISTRATION) >> true
		def activated = buildPatient(id: 42, email: 'e', accountStatus: AccountStatus.ACTIVE)
		userService.activateUser('e') >> activated
		when:
		def dto = authService.confirmRegistration('e','o')
		then:
		dto.userId == 42
		dto.role == UserRole.PATIENT
	}

	def "registerDoctor throws when email exists"() {
		given: userDAO.getByEmail('e') >> Optional.of(buildDoctor(email: 'e'))
		when: authService.registerDoctor('fn','ln','e','pwz','pw','ph',[1, 2])
		then: thrown(UserAlreadyExistsException)
	}

	def "registerDoctor creates and returns DTO"() {
		given:
		def authServiceSpy = Spy(authService)
		authServiceSpy.getCurrentFacilityId() >> 1

		userDAO.getByEmail('e') >> Optional.empty()
		passwordEncoder.encode('pw') >> 'enc'
		def doc = buildDoctor(id:1, email:'e', password:'enc', pwzNumber:'pwz')
		userDAO.createAndReturn(_ as User) >> doc
		userService.addSpecializationsToUser(1, [1, 2]) >> null
		workTimeService.createWorkTimeForNewUser(1, UserRole.DOCTOR, 1) >> []

		when:
		def dto = authServiceSpy.registerDoctor('fn', 'ln', 'e', 'pwz', 'pw', 'ph', [1, 2])

		then:
		dto.userId == 1
		dto.role == UserRole.DOCTOR
	}

	def "registerStaff creates and returns DTO"() {
		given:
		userDAO.getByEmail('e') >> Optional.empty()
		passwordEncoder.encode('pw') >> 'enc'
		def staff = buildStaff(id:2, email:'e', password:'enc', role:'ADMINISTRATOR')
		userDAO.createAndReturn(_ as User) >> staff
		when:
		def dto = authService.registerStaff('fn','ln','e','pw','ADMINISTRATOR')
		then:
		dto.userId == 2
		dto.role == UserRole.ADMINISTRATOR
	}

	def "loginUser authenticates and returns DTO"() {
		given:
		def user = buildPatient(id:3, email:'l', password:'enc', accountStatus:AccountStatus.ACTIVE)
		userDAO.getByEmail('l') >> Optional.of(user)
		userDAO.getByPesel('l') >> Optional.empty()
		facilityService.getById(1) >> Optional.of(buildFacility())
		passwordEncoder.matches('pw','enc') >> true
		jwtUtil.generateToken(3,'PATIENT', 1) >> 'tok'
		when:
		def dto = authService.loginUser('l','pw', 1)
		then: dto.token == 'tok'
	}

	def "loginUser throws UserNotFoundException when no user"() {
		given:
		facilityService.getById(1) >> Optional.of(buildFacility())
		userDAO.getByEmail('l') >> Optional.empty()
		userDAO.getByPesel('l') >> Optional.empty()

		when:
		authService.loginUser('l','pw', 1)

		then:
		thrown(UserNotFoundException)
	}

	def "loginUser throws InvalidCredentialsException for wrong password"() {
		given:
		def u = buildPatient(id:4, email:'l', password:'enc', accountStatus:AccountStatus.ACTIVE)
		facilityService.getById(1) >> Optional.of(buildFacility())
		userDAO.getByEmail('l') >> Optional.of(u)
		passwordEncoder.matches('pw','enc') >> false

		when:
		authService.loginUser('l','pw', 1)

		then:
		thrown(InvalidCredentialsException)
	}

	def "loginUser throws NotFoundException when facility not found"() {
		given:
		def user = buildPatient(id: 3, email: 'l', password: 'enc', accountStatus: AccountStatus.ACTIVE)
		userDAO.getByEmail('l') >> Optional.of(user)
		userDAO.getByPesel('l') >> Optional.empty()
		passwordEncoder.matches('pw', 'enc') >> true

		facilityService.getById(1) >> Optional.empty()

		when:
		authService.loginUser('l', 'pw', 1)

		then:
		thrown(NotFoundException)
	}

	def "loginUser throws AccountNotActiveException when account not active"() {
		given:
		def u = buildPatient(id:5, email:'l', password:'enc', accountStatus:AccountStatus.PENDING_CONFIRMATION)
		userDAO.getByEmail('l') >> Optional.of(u)
		facilityService.getById(1) >> Optional.of(buildFacility())
		passwordEncoder.matches('pw','enc') >> true

		when:
		authService.loginUser('l','pw',1)

		then:
		thrown(AccountNotActiveException)
	}


	def "changePassword success and failure"() {
		given:
		def user = buildPatient(id:5, email:'u', password:'old')
		userService.getById(5) >> Optional.of(user)
		passwordEncoder.matches('old','old') >> true
		passwordEncoder.encode('new') >> 'encNew'
		when:
		authService.changePassword(5,'old','new')
		then:
		1 * userDAO.updatePasswordById(5, 'encNew')
		when:
		passwordEncoder.matches('wrong','old') >> false
		authService.changePassword(5,'wrong','new')
		then: thrown(InvalidCredentialsException)
	}

	def "getCurrentUserId and role from SecurityContext"() {
		given:
		def cu = Mock(CurrentUser) { getUserId() >> 10; getRole() >> UserRole.DOCTOR }
		def auth = Mock(Authentication) { isAuthenticated() >> true; principal >> cu }
		SecurityContextHolder.context.authentication = auth
		expect:
		authService.getCurrentUserId() == 10
		authService.getCurrentUserRole() == UserRole.DOCTOR
	}
}
