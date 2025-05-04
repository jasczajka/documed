package com.documed.backend.users

import com.documed.backend.auth.exceptions.UserNotFoundException
import com.documed.backend.users.exceptions.SpecializationToNonDoctorException
import com.documed.backend.users.model.AccountStatus
import com.documed.backend.users.model.User
import com.documed.backend.users.model.UserRole
import spock.lang.Specification
import spock.lang.Subject

class UserServiceTest extends Specification {

	def userDAO = Mock(UserDAO)

	@Subject
	UserService userService = new UserService(userDAO)

	private User buildUser(Map overrides = [:]) {
		return User.builder()
				.id(overrides.id ?: 1)
				.firstName(overrides.firstName ?: "John")
				.lastName(overrides.lastName ?: "Doe")

				.email(overrides.email ?: "john.doe@example.com")
				.accountStatus(overrides.accountStatus ?: AccountStatus.ACTIVE)
				.role(overrides.role ?: UserRole.PATIENT)
				.emailNotifications(overrides.emailNotifications != null ? overrides.emailNotifications : true)
				.pesel(overrides.pesel ?: null)
				.password(overrides.password ?: "defaultPass")
				.build()
	}

	def "getById returns user when exists"() {
		given:
		def user = buildUser(id: 1)
		userDAO.getById(1) >> Optional.of(user)

		when:
		def result = userService.getById(1)

		then:
		result.isPresent()
		result.get() == user
	}

	def "getById returns empty when not exists"() {
		given:
		userDAO.getById(1) >> Optional.empty()

		when:
		def result = userService.getById(1)

		then:
		result.isEmpty()
	}

	def "getByEmail returns user when exists"() {
		given:
		def user = buildUser(email: "test@example.com")
		userDAO.getByEmail("test@example.com") >> Optional.of(user)

		when:
		def result = userService.getByEmail("test@example.com")

		then:
		result.isPresent()
		result.get() == user
	}

	def "getByEmail returns empty when not exists"() {
		given:
		userDAO.getByEmail("test@example.com") >> Optional.empty()

		when:
		def result = userService.getByEmail("test@example.com")

		then:
		result.isEmpty()
	}

	def "getByPesel returns user when exists"() {
		given:
		def user = buildUser().tap { it.pesel = "12345678901" }
		userDAO.getByPesel("12345678901") >> Optional.of(user)

		when:
		def result = userService.getByPesel("12345678901")

		then:
		result.isPresent()
		result.get() == user
	}

	def "getByPesel returns empty when not exists"() {
		given:
		userDAO.getByPesel("12345678901") >> Optional.empty()

		when:
		def result = userService.getByPesel("12345678901")

		then:
		result.isEmpty()
	}
	def "createPendingUser sets status to PENDING_CONFIRMATION and saves user"() {
		given:
		def user = User.builder()
				.id(1)
				.firstName("John")
				.lastName("Doe")
				.email("john.doe@example.com")
				.password("defaultPass")
				.accountStatus(AccountStatus.ACTIVE)
				.role(UserRole.PATIENT)
				.emailNotifications(true)
				.build()

		when:
		def result = userService.createPendingUser(user)

		then:
		1 * userDAO.createAndReturn({ User u ->
			assert u.is(user)
			assert u.accountStatus == AccountStatus.PENDING_CONFIRMATION
			true
		}) >> { User u -> u }

		result.accountStatus == AccountStatus.PENDING_CONFIRMATION
	}

	def "activateUser activates user successfully"() {
		given:
		def user = User.builder()
				.id(2)
				.firstName("Jane")
				.lastName("Roe")
				.email("jane.roe@example.com")
				.password("secret")
				.accountStatus(AccountStatus.PENDING_CONFIRMATION)
				.role(UserRole.PATIENT)
				.emailNotifications(true)
				.build()

		userDAO.getByEmail("jane.roe@example.com") >> Optional.of(user)

		when:
		def activatedUser = userService.activateUser("jane.roe@example.com")

		then:
		1 * userDAO.update({ User u ->
			assert u.is(user)
			assert u.accountStatus == AccountStatus.ACTIVE
			true
		}) >> { User u -> u }

		activatedUser.accountStatus == AccountStatus.ACTIVE
	}

	def "activateUser throws UserNotFoundException when user not found"() {
		given:
		userDAO.getByEmail("notfound@test.com") >> Optional.empty()

		when:
		userService.activateUser("notfound@test.com")

		then:
		thrown(UserNotFoundException)
	}

	def "deactivateUser returns true when user is deleted"() {
		given:
		userDAO.delete(1) >> 1

		when:
		def result = userService.deactivateUser(1)

		then:
		result
	}

	def "deactivateUser returns false when no user deleted"() {
		given:
		userDAO.delete(1) >> 0

		when:
		def result = userService.deactivateUser(1)

		then:
		!result
	}

	def "addSpecializationsToUser throws UserNotFoundException when user not found"() {
		given:
		userDAO.getById(1) >> Optional.empty()

		when:
		userService.addSpecializationsToUser(1, [1, 2])

		then:
		thrown(UserNotFoundException)
	}

	def "addSpecializationsToUser throws SpecializationToNonDoctorException for non-doctor role"() {
		given:
		def user = buildUser(id: 1, role: UserRole.PATIENT)
		userDAO.getById(1) >> Optional.of(user)

		when:
		userService.addSpecializationsToUser(1, [1, 2])

		then:
		thrown(SpecializationToNonDoctorException)
	}

	def "addSpecializationsToUser adds specializations for doctor"() {
		given:
		def user = buildUser(id: 1, role: UserRole.DOCTOR)
		userDAO.getById(1) >> Optional.of(user)
		userDAO.addSpecializationsToUser(1, [1, 2]) >> user

		when:
		def result = userService.addSpecializationsToUser(1, [1, 2])

		then:
		result == user
	}

	def "toggleEmailNotificationsById invokes DAO method"() {
		when:
		userService.toggleEmailNotificationsById(1)

		then:
		1 * userDAO.toggleEmailNotificationsById(1)
	}

	def "areNotificationsOn returns true when notifications enabled"() {
		given:
		def user = buildUser(id: 1, emailNotifications: true)
		userDAO.getById(1) >> Optional.of(user)

		when:
		def result = userService.areNotificationsOn(1)

		then:
		result
	}

	def "areNotificationsOn returns false when notifications disabled"() {
		given:
		def user = buildUser(id: 1, emailNotifications: false)
		userDAO.getById(1) >> Optional.of(user)

		when:
		def result = userService.areNotificationsOn(1)

		then:
		!result
	}

	def "areNotificationsOn throws UserNotFoundException when user not found"() {
		given:
		userDAO.getById(1) >> Optional.empty()

		when:
		userService.areNotificationsOn(1)

		then:
		thrown(UserNotFoundException)
	}
}
