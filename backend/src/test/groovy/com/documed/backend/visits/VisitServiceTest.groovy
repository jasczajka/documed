package com.documed.backend.visits

import com.documed.backend.auth.AuthService
import com.documed.backend.auth.exceptions.UnauthorizedException
import com.documed.backend.exceptions.BadRequestException
import com.documed.backend.exceptions.NotFoundException
import com.documed.backend.notifications.NotificationService
import com.documed.backend.prescriptions.PrescriptionService
import com.documed.backend.referrals.ReferralService
import com.documed.backend.schedules.TimeSlotService
import com.documed.backend.schedules.model.TimeRange
import com.documed.backend.schedules.model.TimeSlot
import com.documed.backend.services.ServiceService
import com.documed.backend.users.model.AccountStatus
import com.documed.backend.users.model.User
import com.documed.backend.users.model.UserRole
import com.documed.backend.users.services.SubscriptionService
import com.documed.backend.users.services.UserService
import com.documed.backend.visits.dtos.ScheduleVisitDTO
import com.documed.backend.visits.dtos.UpdateVisitDTO
import com.documed.backend.visits.exceptions.*
import com.documed.backend.visits.model.*
import java.time.LocalDate
import java.time.LocalTime
import spock.lang.Specification
import spock.lang.Subject

class VisitServiceTest extends Specification {

	def visitDAO = Mock(VisitDAO)
	def feedbackDAO = Mock(FeedbackDAO)
	def timeSlotService = Mock(TimeSlotService)
	def authService = Mock(AuthService)
	def serviceService = Mock(ServiceService)
	def userService = Mock(UserService)
	def prescriptionService = Mock(PrescriptionService)
	def referralService = Mock(ReferralService)
	def notificationService = Mock(NotificationService)


	@Subject
	def visitService = new VisitService(visitDAO, feedbackDAO, timeSlotService, authService, serviceService, userService, prescriptionService, referralService, notificationService)

	private VisitWithDetails buildVisitWithDetails(Map overrides = [:]) {
		return VisitWithDetails.builder()
				.id(overrides.id ?: 1)
				.status(overrides.status ?: VisitStatus.PLANNED)
				.facilityId(overrides.facilityId ?: 1)
				.serviceId(overrides.serviceId ?: 1)
				.serviceName(overrides.serviceName ?: "Service")
				.patientId(overrides.patientId ?: 1)
				.patientFullName(overrides.patientFullName ?: "Patient Name")
				.patientPesel(overrides.patientPesel ?: "12345678901")
				.patientBirthDate(overrides.patientBirthDate ?: LocalDate.now().minusYears(30))
				.doctorId(overrides.doctorId ?: 1)
				.doctorFullName(overrides.doctorFullName ?: "Doctor Name")
				.date(overrides.date)
				.startTime(overrides.startTime)
				.endTime(overrides.endTime)
				.build()
	}

	private Visit buildVisit(Map overrides = [:]) {
		return Visit.builder()
				.id(overrides.id ?: 1)
				.status(overrides.status ?: VisitStatus.PLANNED)
				.facilityId(overrides.facilityId ?: 1)
				.serviceId(overrides.serviceId ?: 1)
				.patientId(overrides.patientId ?: 1)
				.doctorId(overrides.doctorId ?: 1)
				.totalCost(overrides.totalCost ?: BigDecimal.valueOf(100))
				.patientInformation(overrides.patientInformation ?: "Info")
				.date(overrides.date)
				.startTime(overrides.startTime)
				.endTime(overrides.endTime)
				.build()
	}


	def "scheduleVisit should create and reserve needed time slots"() {
		given:
		def dto = new ScheduleVisitDTO("patient info", 1, 2, 3, 4, 222)
		def slot = Mock(TimeSlot) {
			getDate() >> LocalDate.now()
		}
		def timeRange = new TimeRange(LocalTime.of(9, 0), LocalTime.of(10, 0))

		def createdVisit = buildVisit(
				facilityId: 222,
				serviceId: dto.serviceId,
				patientId: dto.patientId,
				doctorId: dto.doctorId,
				totalCost: BigDecimal.valueOf(50),
				status: VisitStatus.PLANNED
				)

		def updatedVisit = buildVisit(
				id: createdVisit.id,
				facilityId: createdVisit.facilityId,
				serviceId: createdVisit.serviceId,
				patientId: createdVisit.patientId,
				doctorId: createdVisit.doctorId,
				totalCost: createdVisit.totalCost,
				status: createdVisit.status,
				patientInformation: createdVisit.patientInformation,
				date: slot.date,
				startTime: timeRange.startTime(),
				endTime: timeRange.endTime()
				)

		when:
		def result = visitService.scheduleVisit(dto)

		then:
		1 * timeSlotService.getTimeSlotById(dto.firstTimeSlotId) >> Optional.of(slot)
		1 * serviceService.calculateTotalCost(dto.serviceId, dto.patientId) >> BigDecimal.valueOf(50)
		1 * visitDAO.create(_) >> createdVisit
		1 * timeSlotService.reserveTimeSlotsForVisit(createdVisit, slot) >> timeRange
		1 * visitDAO.updateWithTimeInfo(_) >> { Visit v ->
			assert v.date == slot.date
			assert v.startTime == timeRange.startTime()
			assert v.endTime == timeRange.endTime()
			updatedVisit
		}
		result == updatedVisit
	}

	def "scheduleVisit should throw NotFoundException when time slot not found"() {
		given:
		def dto = new ScheduleVisitDTO("aaaa", 1, 2, 3, 4, 222)
		timeSlotService.getTimeSlotById(dto.firstTimeSlotId) >> Optional.empty()

		when:
		visitService.scheduleVisit(dto)

		then:
		thrown(NotFoundException)
	}

	def "startVisit should update status when planned"() {
		given:
		def id = 5
		def visit = buildVisit([id: id, status: VisitStatus.PLANNED, doctorId: 123])
		visitDAO.getVisitStatus(id) >> VisitStatus.PLANNED
		visitDAO.getById(id) >> Optional.of(visit)
		visitDAO.updateVisitStatus(id, VisitStatus.IN_PROGRESS) >> true
		authService.getCurrentUserId() >> 123

		when:
		def result = visitService.startVisit(id)

		then:
		result
	}

	def "startVisit should throw WrongVisitStatusException when status is not planned"() {
		given:
		def id = 7
		def visit = buildVisit([id: id, status: VisitStatus.CLOSED, doctorId: 123])
		visitDAO.getVisitStatus(id) >> VisitStatus.CLOSED
		visitDAO.getById(id) >> Optional.of(visit)
		authService.getCurrentUserId() >> 123

		when:
		visitService.startVisit(id)

		then:
		thrown(WrongVisitStatusException)
	}

	def "startVisit should throw UnauthorizedException when current user is not the doctor"() {
		given:
		def id = 10
		def visit = buildVisit([id: id, status: VisitStatus.PLANNED, doctorId: 999])
		visitDAO.getVisitStatus(id) >> VisitStatus.PLANNED
		visitDAO.getById(id) >> Optional.of(visit)
		authService.getCurrentUserId() >> 123

		when:
		visitService.startVisit(id)

		then:
		thrown(UnauthorizedException)
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
		referralService.getReferralsForVisit(id) >> []

		prescriptionService.getPrescriptionIdForVisitId(id) >> Optional.empty()
		prescriptionService.getPrescriptionForVisit(id) >> Optional.empty()

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
		def patientId = 100
		def date = LocalDate.now()

		def visit = VisitWithDetails.builder()
				.id(id)
				.patientId(patientId)
				.status(VisitStatus.PLANNED)
				.date(date)
				.build()

		def user = User.builder()
				.id(patientId)
				.firstName("name")
				.lastName("name2")
				.accountStatus(AccountStatus.ACTIVE)
				.role(UserRole.PATIENT)
				.email("test@example.com")
				.build()

		visitDAO.findByIdWithDetails(id) >> Optional.of(visit)
		userService.getById(patientId) >> Optional.of(user)

		when:
		def result = visitService.cancelVisit(id)

		then:
		1 * timeSlotService.releaseTimeSlotsForVisit(id)
		1 * visitDAO.updateVisitStatus(id, VisitStatus.CANCELLED) >> true
		result
	}


	def "getByIdWithDetails should return visit when authorized"() {
		given:
		def visitId = 1
		def userId = 10
		def visitWithDetails = buildVisitWithDetails(id: visitId, patientId: userId)

		when:
		def result = visitService.getByIdWithDetails(visitId)

		then:
		1 * visitDAO.findByIdWithDetails(visitId) >> Optional.of(visitWithDetails)
		1 * authService.getCurrentUserRole() >> UserRole.PATIENT
		1 * authService.getCurrentUserId() >> userId
		result == visitWithDetails
	}

	def "getByIdWithDetails should throw UnauthorizedException when patient accesses other patient's visit"() {
		given:
		def visitId = 1
		def visitWithDetails = buildVisitWithDetails(id: visitId, patientId: 10)

		when:
		visitService.getByIdWithDetails(visitId)

		then:
		1 * visitDAO.findByIdWithDetails(visitId) >> Optional.of(visitWithDetails)
		1 * authService.getCurrentUserRole() >> UserRole.PATIENT
		1 * authService.getCurrentUserId() >> 20
		thrown(UnauthorizedException)
	}

	def "getByIdWithDetails should throw NotFoundException when visit not found"() {
		given:
		def visitId = 1

		when:
		visitService.getByIdWithDetails(visitId)

		then:
		1 * visitDAO.findByIdWithDetails(visitId) >> Optional.empty()
		thrown(NotFoundException)
	}

	def "getAllWithDetailsBetweenDates should return all visits within date range"() {
		given:
		def startDate = LocalDate.now().minusMonths(3)
		def endDate = LocalDate.now()
		def visits = [
			buildVisitWithDetails(id: 1, visitDate: startDate.plusDays(1)),
			buildVisitWithDetails(id: 2, visitDate: endDate.minusDays(1))
		]

		when:
		def result = visitService.getAllWithDetailsBetweenDates(startDate)

		then:
		1 * visitDAO.findAllWithDetailsBetweenDates(startDate) >> visits
		result == visits
	}

	def "getVisitsForCurrentPatientWithDetailsBetweenDates should return patient's visits within date range"() {
		given:
		def patientId = 10
		def facilityId = 1
		def startDate = LocalDate.now().minusMonths(3)
		def endDate = LocalDate.now()
		def visits = [
			buildVisitWithDetails(patientId: patientId, facilityId: facilityId, visitDate: startDate.plusDays(1)),
			buildVisitWithDetails(patientId: patientId, facilityId: facilityId, visitDate: endDate.minusDays(1))
		]

		when:
		def result = visitService.getVisitsForCurrentPatientWithDetailsBetweenDates(startDate)

		then:
		1 * authService.getCurrentUserId() >> patientId
		1 * visitDAO.findByPatientIdWithDetailsBetweenDates(patientId, startDate) >> visits
		result == visits
	}

	def "getVisitsByPatientIdWithDetailsBetweenDates should return visits for specified patient within date range"() {
		given:
		def patientId = 10
		def facilityId = 1
		def startDate = LocalDate.now().minusMonths(3)
		def endDate = LocalDate.now()
		def visits = [
			buildVisitWithDetails(patientId: patientId, facilityId: facilityId, visitDate: startDate.plusDays(1)),
			buildVisitWithDetails(patientId: patientId, facilityId: facilityId, visitDate: endDate.minusDays(1))
		]

		when:
		def result = visitService.getVisitsByPatientIdWithDetailsBetweenDates(patientId, startDate)

		then:
		1 * authService.getCurrentFacilityId() >> facilityId
		1 * visitDAO.findByPatientIdAndFacilityIdWithDetailsBetweenDates(patientId, facilityId, startDate) >> visits
		result == visits
	}

	def "getVisitsByDoctorIdWithDetailsBetweenDates should return visits for specified doctor within date range"() {
		given:
		def doctorId = 5
		def facilityId = 1
		def startDate = LocalDate.now().minusMonths(3)
		def endDate = LocalDate.now()
		def visits = [
			buildVisitWithDetails(doctorId: doctorId, facilityId: facilityId, visitDate: startDate.plusDays(1)),
			buildVisitWithDetails(doctorId: doctorId, facilityId: facilityId, visitDate: endDate.minusDays(1))
		]

		when:
		def result = visitService.getVisitsByDoctorIdWithDetailsBetweenDates(doctorId, startDate)

		then:
		1 * authService.getCurrentFacilityId() >> facilityId
		1 * visitDAO.findByDoctorIdAndFacilityIdWithDetailsBetweenDates(doctorId, facilityId, startDate) >> visits
		result == visits
	}

	def "getVisitsForCurrentDoctorWithDetailsBetweenDates should return doctor's visits within date range"() {
		given:
		def doctorId = 5
		def facilityId = 1
		def startDate = LocalDate.now().minusMonths(3)
		def endDate = LocalDate.now()
		def visits = [
			buildVisitWithDetails(doctorId: doctorId, facilityId: facilityId, visitDate: startDate.plusDays(1)),
			buildVisitWithDetails(doctorId: doctorId, facilityId: facilityId, visitDate: endDate.minusDays(1))
		]

		when:
		def result = visitService.getVisitsForCurrentDoctorWithDetailsBetweenDates(startDate)

		then:
		1 * authService.getCurrentUserId() >> doctorId
		1 * authService.getCurrentFacilityId() >> facilityId
		1 * visitDAO.findByDoctorIdAndFacilityIdWithDetailsBetweenDates(doctorId, facilityId, startDate) >> visits
		result == visits
	}

	def "getById should return visit when authorized"() {
		given:
		def visitId = 1
		def userId = 10
		def visit = buildVisit(id: visitId, patientId: userId)

		when:
		def result = visitService.getById(visitId)

		then:
		1 * visitDAO.getById(visitId) >> Optional.of(visit)
		1 * authService.getCurrentUserRole() >> UserRole.PATIENT
		1 * authService.getCurrentUserId() >> userId
		result == visit
	}

	def "getById should throw UnauthorizedException when patient accesses other patient's visit"() {
		given:
		def visitId = 1
		def visit = buildVisit(id: visitId, patientId: 10)

		when:
		visitService.getById(visitId)

		then:
		1 * visitDAO.getById(visitId) >> Optional.of(visit)
		1 * authService.getCurrentUserRole() >> UserRole.PATIENT
		1 * authService.getCurrentUserId() >> 20
		thrown(UnauthorizedException)
	}

	def "getById should throw NotFoundException when visit not found"() {
		given:
		def visitId = 1

		when:
		visitService.getById(visitId)

		then:
		1 * visitDAO.getById(visitId) >> Optional.empty()
		thrown(NotFoundException)
	}




	def "closeVisit should remove empty prescription"() {
		given:
		def visitId = 8
		def prescriptionId = 15
		def updateDto = new UpdateVisitDTO()
		def visit = buildVisit(id: visitId, status: VisitStatus.IN_PROGRESS)

		visitDAO.getVisitStatus(visitId) >> VisitStatus.IN_PROGRESS
		visitDAO.getById(visitId) >> Optional.of(visit)
		visitDAO.update(_ as Visit) >> { Visit v -> v }
		visitDAO.updateVisitStatus(visitId, VisitStatus.CLOSED) >> true

		prescriptionService.getPrescriptionIdForVisitId(visitId) >> Optional.of(prescriptionId)
		prescriptionService.getNumberOfMedicinesOnPrescriptionByVisitId(visitId) >> 0
		prescriptionService.getPrescriptionForVisit(visitId) >> Optional.empty()

		referralService.getReferralsForVisit(visitId) >> []

		when:
		def result = visitService.closeVisit(visitId, updateDto)

		then:
		1 * prescriptionService.removePrescription(prescriptionId)
		result
	}

	def "closeVisit should not remove prescription with medicines"() {
		given:
		def visitId = 8
		def prescriptionId = 15
		def updateDto = new UpdateVisitDTO()
		def visit = buildVisit(id: visitId, status: VisitStatus.IN_PROGRESS)

		visitDAO.getVisitStatus(visitId) >> VisitStatus.IN_PROGRESS
		visitDAO.getById(visitId) >> Optional.of(visit)
		visitDAO.update(_ as Visit) >> { Visit v -> v }
		visitDAO.updateVisitStatus(visitId, VisitStatus.CLOSED) >> true
		referralService.getReferralsForVisit(visitId) >> []

		prescriptionService.getPrescriptionIdForVisitId(visitId) >> Optional.of(prescriptionId)
		prescriptionService.getNumberOfMedicinesOnPrescriptionByVisitId(visitId) >> 2
		prescriptionService.getPrescriptionForVisit(visitId) >> Optional.empty()

		referralService.getReferralsForVisit(visitId) >> []

		when:
		def result = visitService.closeVisit(visitId, updateDto)

		then:
		0 * prescriptionService.removePrescription(_)
		result
	}

	def "cancelVisit should throw UnauthorizedException when patient cancels other patient's visit"() {
		given:
		def visitId = 12
		def currentUserId = 10
		def visitPatientId = 20

		def visit = VisitWithDetails.builder()
				.id(visitId)
				.patientId(visitPatientId)
				.status(VisitStatus.PLANNED)
				.date(LocalDate.now())
				.build()

		visitDAO.findByIdWithDetails(visitId) >> Optional.of(visit)
		authService.getCurrentUserRole() >> UserRole.PATIENT
		authService.getCurrentUserId() >> currentUserId

		when:
		visitService.cancelVisit(visitId)

		then:
		thrown(UnauthorizedException)
	}

	def "giveFeedback should create feedback for closed visit when valid"() {
		given:
		1 * authService.getCurrentUserId() >> 1
		def visitId = 1
		def feedback = Feedback.builder()
				.rating(3)
				.text("Good service")
				.visitId(visitId)
				.build()
		def visit = buildVisit(id: visitId, status: VisitStatus.CLOSED, patientId: 1)

		when:
		visitService.giveFeedback(feedback)

		then:
		1 * visitDAO.getById(visitId) >> Optional.of(visit)
		1 * feedbackDAO.getByVisitId(visitId) >> Optional.empty()
		1 * feedbackDAO.create(feedback)
	}

	def "giveFeedback should throw UnathorizedException when patient not assigned to visit tries to give feedback"() {
		given:
		1 * authService.getCurrentUserId() >> 3
		def visitId = 1
		def feedback = Feedback.builder()
				.rating(3)
				.text("Good service")
				.visitId(visitId)
				.build()
		def visit = buildVisit(id: visitId, status: VisitStatus.CLOSED, patientId: 1)

		when:
		visitService.giveFeedback(feedback)

		then:
		1 * visitDAO.getById(visitId) >> Optional.of(visit)
		1 * feedbackDAO.getByVisitId(visitId) >> Optional.empty()
		thrown(UnauthorizedException)
	}

	def "giveFeedback should throw NotFoundException when visit not found"() {
		given:
		def visitId = 99
		def feedback = Feedback.builder()
				.rating(5)
				.text("Great service")
				.visitId(visitId)
				.build()

		when:
		visitService.giveFeedback(feedback)

		then:
		1 * visitDAO.getById(visitId) >> Optional.empty()
		thrown(NotFoundException)
	}

	def "giveFeedback should throw BadRequestException when visit is not closed"() {
		given:
		def visitId = 2
		def feedback = Feedback.builder()
				.rating(4)
				.text("Good service")
				.visitId(visitId)
				.build()
		def visit = buildVisit(id: visitId, status: VisitStatus.IN_PROGRESS)

		when:
		visitService.giveFeedback(feedback)

		then:
		1 * visitDAO.getById(visitId) >> Optional.of(visit)
		def e = thrown(BadRequestException)
		e.message == "Feedback can only be given for a closed visit"
	}

	def "giveFeedback should throw BadRequestException when feedback already exists"() {
		given:
		def visitId = 3
		def feedback = Feedback.builder()
				.rating(5)
				.text("Great service")
				.visitId(visitId)
				.build()
		def visit = buildVisit(id: visitId, status: VisitStatus.CLOSED)
		def existingFeedback = Feedback.builder()
				.rating(4)
				.text("Previous feedback")
				.visitId(visitId)
				.build()

		when:
		visitService.giveFeedback(feedback)

		then:
		1 * visitDAO.getById(visitId) >> Optional.of(visit)
		1 * feedbackDAO.getByVisitId(visitId) >> Optional.of(existingFeedback)
		def e = thrown(BadRequestException)
		e.message == "Feedback has already been given for this visit"
	}

	def "giveFeedback should throw BadRequestException when rating is below 1"() {
		given:
		def invalidFeedback = Feedback.builder()
				.rating(0)
				.text("Too low rating")
				.visitId(4)
				.build()

		when:
		visitService.giveFeedback(invalidFeedback)

		then:
		thrown(BadRequestException)
		0 * visitDAO.getById(_)
		0 * feedbackDAO.getByVisitId(_)
		0 * feedbackDAO.create(_)
	}

	def "giveFeedback should throw BadRequestException when rating is above 5"() {
		given:
		def invalidFeedback = Feedback.builder()
				.rating(6)
				.text("Too high rating")
				.visitId(5)
				.build()

		when:
		visitService.giveFeedback(invalidFeedback)

		then:
		thrown(BadRequestException)
		0 * visitDAO.getById(_)
		0 * feedbackDAO.getByVisitId(_)
		0 * feedbackDAO.create(_)
	}


	def "sendVisitReminders should send notifications only to patients with email notifications enabled"() {
		given:
		def tomorrow = LocalDate.now().plusDays(1)
		def doctorName = "Dr. Smith"
		def visits = [
			buildVisitWithDetails(id: 1, patientId: 10, status: VisitStatus.PLANNED, date: tomorrow, doctorFullName: doctorName),
			buildVisitWithDetails(id: 2, patientId: 20, status: VisitStatus.PLANNED, date: tomorrow, doctorFullName: doctorName)
		]

		def patient1 = User.builder()
				.id(10)
				.firstName("John")
				.lastName("Doe")
				.email("patient1@example.com")
				.accountStatus(AccountStatus.ACTIVE)
				.role(UserRole.PATIENT)
				.emailNotifications(true)  // Notifications enabled
				.build()

		def patient2 = User.builder()
				.id(20)
				.firstName("Jane")
				.lastName("Smith")
				.email("patient2@example.com")
				.accountStatus(AccountStatus.ACTIVE)
				.role(UserRole.PATIENT)
				.emailNotifications(false)  // Notifications disabled
				.build()

		when:
		visitService.sendVisitReminders()

		then:
		1 * visitDAO.findPlannedWithDetailsByDate(tomorrow) >> visits

		and: "First patient should get notification"
		1 * userService.getById(10) >> Optional.of(patient1)
		1 * notificationService.sendVisitReminderEmail("patient1@example.com", tomorrow, doctorName, 1)

		and: "Second patient should not get notification"
		1 * userService.getById(20) >> Optional.of(patient2)
		0 * notificationService.sendVisitReminderEmail("patient2@example.com", _, _, _)
	}



	def "sendVisitReminders should log error when patient not found"() {
		given:
		def tomorrow = LocalDate.now().plusDays(1)
		def visits = [
			buildVisitWithDetails(id: 1, patientId: 99, status: VisitStatus.PLANNED, date: tomorrow)
		]

		when:
		visitService.sendVisitReminders()

		then:
		1 * visitDAO.findPlannedWithDetailsByDate(tomorrow) >> visits
		1 * userService.getById(99) >> Optional.empty()
		thrown(NotFoundException)
	}
}
