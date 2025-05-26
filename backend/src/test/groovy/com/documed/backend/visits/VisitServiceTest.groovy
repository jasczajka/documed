package com.documed.backend.visits

import com.documed.backend.auth.AuthService
import com.documed.backend.exceptions.NotFoundException
import com.documed.backend.schedules.TimeSlotService
import com.documed.backend.schedules.model.TimeSlot
import com.documed.backend.services.ServiceService
import com.documed.backend.visits.dtos.UpdateVisitDTO
import com.documed.backend.visits.exceptions.*
import com.documed.backend.visits.model.*
import spock.lang.Specification
import spock.lang.Subject

class VisitServiceTest extends Specification {

	def visitDAO = Mock(VisitDAO)
	def timeSlotService = Mock(TimeSlotService)
	def authService = Mock(AuthService)
	def serviceService = Mock(ServiceService)

	@Subject
	def visitService = new VisitService(visitDAO, timeSlotService, authService, serviceService)

	def "scheduleVisit should create and reserve needed time slots"() {
		given:
		def dto = new ScheduleVisitDTO("patient info", 1,2 , 3, 4)
		def slot = Mock(TimeSlot)
		def createdVisit = Visit.builder()
				.id(333)
				.facilityId(222)
				.serviceId(dto.serviceId)
				.patientId(dto.patientId)
				.doctorId(dto.doctorId)
				.totalCost(BigDecimal.valueOf(50))
				.status(VisitStatus.PLANNED)
				.build()

		when:
		def result = visitService.scheduleVisit(dto)

		then:
		1 * timeSlotService.getTimeSlotById(dto.firstTimeSlotId) >> Optional.of(slot)
		1 * authService.getCurrentFacilityId() >> 100
		1 * serviceService.getPriceForService(dto.serviceId) >> BigDecimal.valueOf(50)
		1 * visitDAO.create(_) >> createdVisit
		1 * timeSlotService.reserveTimeSlotsForVisit(createdVisit, slot)
		result == createdVisit
	}

	def "scheduleVisit should throw NotFoundException when time slot not found"() {
		given:
		def dto = new ScheduleVisitDTO("aaaa", 1, 2, 3, 4)
		timeSlotService.getTimeSlotById(dto.firstTimeSlotId) >> Optional.empty()

		when:
		visitService.scheduleVisit(dto)

		then:
		thrown(NotFoundException)
	}

	def "startVisit should update status when planned"() {
		given:
		def id = 5
		visitDAO.getVisitStatus(id) >> VisitStatus.PLANNED
		visitDAO.updateVisitStatus(id, VisitStatus.IN_PROGRESS) >> true

		when:
		def result = visitService.startVisit(id)

		then:
		result
	}

	def "startVisit should throw WrongVisitStatusException when status is not planned"() {
		given:
		visitDAO.getVisitStatus(7) >> VisitStatus.CLOSED

		when:
		visitService.startVisit(7)

		then:
		thrown(WrongVisitStatusException)
	}

	def "closeVisit should update and close when in progress"() {
		given:
		def id = 8
		def updateDto = new UpdateVisitDTO(interview: 'int', diagnosis: 'diag', recommendations: 'rec')
		def visit = Visit.builder()
				.id(id)
				.interview("old")
				.diagnosis("old")
				.recommendations("old")
				.status(VisitStatus.IN_PROGRESS)
				.build()

		visitDAO.getVisitStatus(id) >> VisitStatus.IN_PROGRESS
		visitDAO.getById(id) >> Optional.of(visit)
		visitDAO.update(_ as Visit) >> { Visit v -> v }
		visitDAO.updateVisitStatus(id, VisitStatus.CLOSED) >> true

		when:
		def result = visitService.closeVisit(id, updateDto)

		then:
		result
	}

	def "closeVisit should throw WrongVisitStatusException when status not in progress"() {
		given:
		visitDAO.getVisitStatus(9) >> VisitStatus.PLANNED

		when:
		visitService.closeVisit(9, new UpdateVisitDTO())

		then:
		thrown(WrongVisitStatusException)
	}

	def "updateVisit should update fields and return visit"() {
		given:
		def existing = Visit.builder()
				.id(11)
				.status(VisitStatus.IN_PROGRESS)
				.interview('old')
				.diagnosis('old')
				.recommendations('old')
				.build()
		def updateDto = new UpdateVisitDTO(interview: 'new', diagnosis: 'new', recommendations: 'new')

		when:
		def result = visitService.updateVisit(11, updateDto)

		then:
		1 * visitDAO.getById(11) >> Optional.of(existing)
		1 * visitDAO.update({ v ->
			assert v.interview == 'new'
			assert v.diagnosis == 'new'
			assert v.recommendations == 'new'
			true
		}) >> { Visit v -> v }

		and:
		result.interview == 'new'
		result.diagnosis == 'new'
		result.recommendations == 'new'
	}

	def "cancelVisit should release slot and cancel"() {
		given:
		def id = 12

		when:
		def result = visitService.cancelVisit(id)

		then:
		1 * timeSlotService.releaseTimeSlotsForVisit(id)
		1 * visitDAO.updateVisitStatus(id, VisitStatus.CANCELLED) >> true
		result
	}
}
