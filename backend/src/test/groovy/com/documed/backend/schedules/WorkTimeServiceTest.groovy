package com.documed.backend.schedules

import com.documed.backend.schedules.model.WorkTime
import java.time.DayOfWeek
import java.time.LocalTime
import spock.lang.Specification
import spock.lang.Subject

class WorkTimeServiceTest extends Specification{

	def workTimeDAO = Mock(WorkTimeDAO)

	@Subject
	def workTimeService = new WorkTimeService(workTimeDAO)

	def setup() {
		workTimeService.slotDurationInMinutes = 15
	}

	def "should create work time"() {
		given:
		def workTime = WorkTime.builder()
				.userId(997)
				.dayOfWeek(DayOfWeek.FRIDAY)
				.startTime(LocalTime.of(10, 00))
				.endTime(LocalTime.of(15, 00))
				.build()

		when:
		def result = workTimeService.createWorkTime(workTime)

		then:
		1 * workTimeDAO.create(workTime) >> workTime
		result == workTime
	}

	def "should update work times"() {
		given:
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(9, 0),
				endTime: LocalTime.of(10, 0)
				)

		when:
		def result = workTimeService.updateWorkTimes([dto], 997)

		then:
		1 * workTimeDAO.updateWorkTime(_ as WorkTime)
		result.size() == 1
		result[0].dayOfWeek == DayOfWeek.MONDAY
		result[0].userId == 997
	}

	def "should throw exception when duration is too short"() {
		given:
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(9, 0),
				endTime: LocalTime.of(9, 5)
				)

		when:
		workTimeService.updateWorkTimes([dto], 997)

		then:
		thrown(WrongTimesGivenException)
	}

	def "should throw exception when startTime is not before endTime"() {
		given:
		def dto = new WorkTimeDTO(
				dayOfWeek: DayOfWeek.MONDAY,
				startTime: LocalTime.of(10, 0),
				endTime: LocalTime.of(9, 0)
				)

		when:
		workTimeService.updateWorkTimes([dto], 997)

		then:
		thrown(WrongTimesGivenException)
	}
}
