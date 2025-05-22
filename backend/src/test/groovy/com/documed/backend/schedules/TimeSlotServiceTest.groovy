package com.documed.backend.schedules

import com.documed.backend.schedules.exceptions.NotEnoughTimeInTimeSlotException
import com.documed.backend.schedules.model.TimeSlot
import com.documed.backend.schedules.model.WorkTime
import com.documed.backend.services.model.Service
import com.documed.backend.visits.model.Visit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import spock.lang.Specification

class TimeSlotServiceTest extends Specification {

	def timeSlotDAO = Mock(TimeSlotDAO)

	def timeSlotService = new TimeSlotService(timeSlotDAO)

	def setup() {
		timeSlotService.slotDurationInMinutes = 15
	}

	def "should create timeslots for a worktime"() {
		given:
		def workTime = WorkTime.builder()
				.userId(997)
				.dayOfWeek(DayOfWeek.WEDNESDAY)
				.startTime(LocalTime.of(11, 0))
				.endTime(LocalTime.of(12, 0))
				.build()

		when:
		timeSlotService.createTimeSlotForWorkTime(workTime)

		then:
		4 * timeSlotDAO.create({ TimeSlot ts ->
			ts.doctorId == 997 &&
					ts.date.dayOfWeek == DayOfWeek.WEDNESDAY &&
					ts.endTime.minusMinutes(15) == ts.startTime
		})
	}

	def "should throw exception when not enough timeslots available for visit"() {
		given:
		def service = Mock(Service) {
			getEstimatedTime() >> 120
		}
		def visit = Mock(Visit) {
			getService() >> service
		}

		def date = LocalDate.now()
		def timeSlot = TimeSlot.builder()
				.doctorId(997)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		and:
		timeSlotDAO.getAvailableTimeSlotsByDoctorAndDate(997, date) >> [timeSlot]

		when:
		timeSlotService.reserveTimeSlotsForVisit(visit, timeSlot)

		then:
		thrown(NotEnoughTimeInTimeSlotException)
	}

	def "should reserve timeSlots for visit"() {
		given:
		def service = Mock(Service) {
			getEstimatedTime() >> 25
		}
		def visit = Mock(Visit) {
			getService() >> service
			getId() >> 112
		}

		def date = LocalDate.now()
		def timeSlot1 = TimeSlot.builder()
				.doctorId(997)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def timeSlot2 = TimeSlot.builder()
				.doctorId(997)
				.date(date)
				.startTime(LocalTime.of(9, 15))
				.endTime(LocalTime.of(9, 30))
				.build()

		and:
		timeSlotDAO.getAvailableTimeSlotsByDoctorAndDate(997, date) >> [timeSlot1, timeSlot2]

		when:
		timeSlotService.reserveTimeSlotsForVisit(visit, timeSlot1)

		then:
		1 * timeSlotDAO.update({ it == timeSlot1 && it.busy && it.visitId == 112 })
		1 * timeSlotDAO.update({ it == timeSlot2 && it.busy && it.visitId == 112 })
	}
}
