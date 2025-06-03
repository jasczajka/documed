package com.documed.backend.schedules

import com.documed.backend.auth.AuthService
import com.documed.backend.schedules.dtos.WorkTimeDTO
import com.documed.backend.schedules.exceptions.WrongTimesGivenException
import com.documed.backend.schedules.model.WorkTime
import com.documed.backend.users.model.UserRole
import com.documed.backend.users.services.UserService
import java.time.DayOfWeek
import java.time.LocalTime
import spock.lang.Specification
import spock.lang.Subject

class WorkTimeServiceTest extends Specification{

	def workTimeDAO = Mock(WorkTimeDAO)
	def authService = Mock(AuthService)
	def userService = Mock(UserService)

	@Subject
	def workTimeService = new WorkTimeService(workTimeDAO, authService, userService)

	def setup() {
		workTimeService.slotDurationInMinutes = 15
	}

	def "should create work time"() {
		given:

		def userId = 997
		def facilityId = 10
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(9, 0),
				endTime: LocalTime.of(10, 0)
				)



		def workTime = WorkTime.builder()
				.userId(userId)
				.dayOfWeek(dto.dayOfWeek)
				.startTime(dto.startTime)
				.endTime(dto.endTime)
				.facilityId(facilityId)
				.build()

		when:
		authService.getCurrentFacilityId() >> facilityId
		userService.isUserAssignedToRole(userId, UserRole.DOCTOR) >> true
		workTimeDAO.create(_ as WorkTime) >> workTime

		def result = workTimeService.createWorkTime(userId, dto)

		then:
		result == workTime
	}

	def "should update work times"() {
		given:

		def userId = 997
		def facilityId = 10
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(9, 0),
				endTime: LocalTime.of(10, 0)
				)

		def workTime = WorkTime.builder()
				.userId(userId)
				.dayOfWeek(dto.dayOfWeek)
				.startTime(dto.startTime)
				.endTime(dto.endTime)
				.facilityId(facilityId)
				.build()

		authService.getCurrentFacilityId() >> facilityId
		userService.isUserAssignedToRole(userId, UserRole.DOCTOR) >> true
		workTimeDAO.updateWorkTime(_ as WorkTime) >> workTime

		when:
		def result = workTimeService.updateWorkTimes([dto], 997)

		then:
		1 * workTimeDAO.updateWorkTime(_ as WorkTime)
		result.size() == 1
		result[0].dayOfWeek == DayOfWeek.MONDAY
		result[0].userId == 997
		result[0].facilityId == facilityId
		result[0].startTime == LocalTime.of(9, 0)
		result[0].endTime == LocalTime.of(10, 0)
	}

	def "should throw exception when duration is too short"() {
		given:
		def userId = 997
		def facilityId = 10
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(9, 0),
				endTime: LocalTime.of(9, 5)
				)

		authService.getCurrentFacilityId() >> facilityId
		userService.isUserAssignedToRole(userId, UserRole.DOCTOR) >> true

		when:
		workTimeService.updateWorkTimes([dto], 997)

		then:
		thrown(WrongTimesGivenException)
	}

	def "should throw exception when startTime is not before endTime"() {
		given:
		def userId = 997
		def facilityId = 10
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(10, 0),
				endTime: LocalTime.of(9, 0)
				)

		authService.getCurrentFacilityId() >> facilityId
		userService.isUserAssignedToRole(userId, UserRole.DOCTOR) >> true

		when:
		workTimeService.updateWorkTimes([dto], 997)

		then:
		thrown(WrongTimesGivenException)
	}
}
