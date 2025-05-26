package com.documed.backend.schedules

import com.documed.backend.schedules.exceptions.NotEnoughTimeInTimeSlotException
import com.documed.backend.schedules.model.TimeSlot
import com.documed.backend.schedules.model.WorkTime
import com.documed.backend.services.ServiceService
import com.documed.backend.services.model.Service
import com.documed.backend.visits.model.Visit
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import spock.lang.Specification

class TimeSlotServiceTest extends Specification {

	TimeSlotDAO timeSlotDAO
	ServiceService serviceService
	TimeSlotService timeSlotService

	def setup() {
		timeSlotDAO = Mock(TimeSlotDAO)
		serviceService = Mock(ServiceService)
		timeSlotService = new TimeSlotService(timeSlotDAO, serviceService)
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
			getServiceId() >> 55
			getId() >> 10
		}

		serviceService.getById(55) >> Optional.of(service)

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
		def service = Mock(com.documed.backend.services.model.Service) {
			getEstimatedTime() >> 25
		}
		def visit = Mock(Visit) {
			getServiceId() >> 55
			getId() >> 112
		}

		serviceService.getById(55) >> Optional.of(service)

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
	def "should return empty list when no available slots exist"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 2

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> []

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == []
	}

	def "should return first available slot when continuous slots exist"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 2
		def date = LocalDate.now()

		def slot1 = TimeSlot.builder()
				.id(1)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def slot2 = TimeSlot.builder()
				.id(2)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 15))
				.endTime(LocalTime.of(9, 30))
				.build()

		def slot3 = TimeSlot.builder()
				.id(3)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 15))
				.build()

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> [slot1, slot2, slot3]

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == [slot1]
	}

	def "should handle multiple dates correctly"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 3
		def today = LocalDate.now()
		def tomorrow = today.plusDays(1)

		def todaySlot1 = TimeSlot.builder()
				.id(1)
				.doctorId(doctorId)
				.date(today)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def todaySlot2 = TimeSlot.builder()
				.id(2)
				.doctorId(doctorId)
				.date(today)
				.startTime(LocalTime.of(9, 15))
				.endTime(LocalTime.of(9, 30))
				.build()

		def tomorrowSlot1 = TimeSlot.builder()
				.id(3)
				.doctorId(doctorId)
				.date(tomorrow)
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 15))
				.build()

		def tomorrowSlot2 = TimeSlot.builder()
				.id(4)
				.doctorId(doctorId)
				.date(tomorrow)
				.startTime(LocalTime.of(10, 15))
				.endTime(LocalTime.of(10, 30))
				.build()

		def tomorrowSlot3 = TimeSlot.builder()
				.id(5)
				.doctorId(doctorId)
				.date(tomorrow)
				.startTime(LocalTime.of(10, 30))
				.endTime(LocalTime.of(10, 45))
				.build()

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> [
			todaySlot1,
			todaySlot2,
			tomorrowSlot1,
			tomorrowSlot2,
			tomorrowSlot3
		]

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == [tomorrowSlot1]
	}

	def "should skip non-continuous slots"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 2
		def date = LocalDate.now()

		def slot1 = TimeSlot.builder()
				.id(1)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def slot2 = TimeSlot.builder()
				.id(2)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 30)) // Gap between slots
				.endTime(LocalTime.of(9, 45))
				.build()

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> [slot1, slot2]

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == []
	}

	def "should return first slot of multiple blocks when multiple continuous blocks exist"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 2
		def date = LocalDate.now()

		def slot1 = TimeSlot.builder()
				.id(1)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def slot2 = TimeSlot.builder()
				.id(2)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 15))
				.endTime(LocalTime.of(9, 30))
				.build()

		def slot3 = TimeSlot.builder()
				.id(3)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 15))
				.build()

		def slot4 = TimeSlot.builder()
				.id(4)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(10, 15))
				.endTime(LocalTime.of(10, 30))
				.build()

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> [slot1, slot2, slot3, slot4]

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == [slot1, slot3]
	}

	def "should handle minimum neededTimeSlots value (1)"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 1
		def date = LocalDate.now()

		def slot1 = TimeSlot.builder()
				.id(1)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def slot2 = TimeSlot.builder()
				.id(2)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(10, 0))
				.endTime(LocalTime.of(10, 15))
				.build()

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> [slot1, slot2]

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == [slot1, slot2]
	}

	def "should handle overlapping continuous blocks correctly"() {
		given:
		def doctorId = 1
		def neededTimeSlots = 2
		def date = LocalDate.now()

		def slot1 = TimeSlot.builder()
				.id(1)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 0))
				.endTime(LocalTime.of(9, 15))
				.build()

		def slot2 = TimeSlot.builder()
				.id(2)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 15))
				.endTime(LocalTime.of(9, 30))
				.build()

		def slot3 = TimeSlot.builder()
				.id(3)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 30))
				.endTime(LocalTime.of(9, 45))
				.build()

		def slot4 = TimeSlot.builder()
				.id(4)
				.doctorId(doctorId)
				.date(date)
				.startTime(LocalTime.of(9, 45))
				.endTime(LocalTime.of(10, 0))
				.build()

		when:
		timeSlotDAO.getAvailableFutureTimeSlotsByDoctor(doctorId) >> [slot1, slot2, slot3, slot4]

		then:
		timeSlotService.getAvailableFirstTimeSlotsByDoctor(doctorId, neededTimeSlots) == [slot1, slot2, slot3]
	}
}
