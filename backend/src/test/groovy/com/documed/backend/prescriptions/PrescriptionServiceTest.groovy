package com.documed.backend.prescriptions


import com.documed.backend.medicines.MedicineDAO
import com.documed.backend.medicines.model.Medicine
import com.documed.backend.medicines.model.MedicineWithAmount
import com.documed.backend.prescriptions.exceptions.AlreadyIssuedException
import com.documed.backend.prescriptions.exceptions.WrongAmountException
import com.documed.backend.prescriptions.model.CreatePrescriptionObject
import com.documed.backend.prescriptions.model.Prescription
import java.time.LocalDate
import spock.lang.Specification
import spock.lang.Subject

class PrescriptionServiceTest extends Specification {

	PrescriptionDAO prescriptionDAO = Mock()
	MedicineDAO medicineDAO = Mock()

	@Subject
	PrescriptionService service = new PrescriptionService(prescriptionDAO, medicineDAO)

	def "getAll returns all prescriptions"() {
		given:
		def prescriptions = [
			Prescription.builder().build(),
			Prescription.builder().build()
		]
		prescriptionDAO.getAll() >> prescriptions

		when:
		def result = service.getAll()

		then:
		result == prescriptions
	}

	def "getById returns prescription when found"() {
		given:
		def prescription = Prescription.builder().id(1).build()
		prescriptionDAO.getById(1) >> Optional.of(prescription)

		when:
		def result = service.getById(1)

		then:
		result.isPresent() && result.get() == prescription
	}

	def "getById returns empty when not found"() {
		given:
		prescriptionDAO.getById(1) >> Optional.empty()

		when:
		def result = service.getById(1)

		then:
		result.isEmpty()
	}

	def "createPrescription calls DAO with CreatePrescriptionObject"() {
		given:
		def visitId = 5
		def expirationDate = LocalDate.of(2025, 6, 1)
		def createObject = new CreatePrescriptionObject(visitId, expirationDate)
		def expected = Prescription.builder().id(visitId).expirationDate(expirationDate).build()

		and:
		prescriptionDAO.create(createObject) >> expected

		when:
		def result = service.createPrescription(visitId, expirationDate)

		then:
		result == expected
	}

	def "getPrescriptionForVisit returns DAO result"() {
		given:
		def visitId = 10
		def expected = Prescription.builder().id(visitId).build()
		prescriptionDAO.getPrescriptionForVisit(visitId) >> Optional.of(expected) // Wrap in Optional

		when:
		def result = service.getPrescriptionForVisit(visitId)

		then:
		result.isPresent() && result.get() == expected
	}

	def "getPrescriptionForVisit returns empty when not found"() {
		given:
		def visitId = 10
		prescriptionDAO.getPrescriptionForVisit(visitId) >> Optional.empty()

		when:
		def result = service.getPrescriptionForVisit(visitId)

		then:
		result.isEmpty()
	}

	def "getPrescriptionsForUser returns list from DAO"() {
		given:
		def userId = 3
		def prescriptions = [
			Prescription.builder().build(),
			Prescription.builder().build()
		]
		prescriptionDAO.getPrescriptionsForUser(userId) >> prescriptions

		when:
		def result = service.getPrescriptionsForUser(userId)

		then:
		result == prescriptions
	}


	def "getMedicinesForPrescription returns medicines from DAO"() {
		given:
		def prescriptionId = 7
		def medicines = [
			MedicineWithAmount.builder().id("MED1").build(),
			MedicineWithAmount.builder().id("MED2").build()
		]
		medicineDAO.getForPrescription(prescriptionId) >> medicines

		when:
		def result = service.getMedicinesForPrescription(prescriptionId)

		then:
		result == medicines
	}
	def "addMedicineToPrescription returns medicine when successful"() {
		given:
		def prescriptionId = 1
		def medicineId = "MED123"
		def amount = 2
		def medicine = Medicine.builder().id(medicineId).build()
		prescriptionDAO.addMedicineToPrescription(prescriptionId, medicineId, amount) >> Optional.of(medicine)

		when:
		def result = service.addMedicineToPrescription(prescriptionId, medicineId, amount)

		then:
		result.isPresent() && result.get() == medicine
	}

	def "addMedicineToPrescription throws when amount smaller than 1"() {
		given:
		def prescriptionId = 1
		def medicineId = "MED123"
		def amount = 0
		def medicine = Medicine.builder().id(medicineId).build()
		prescriptionDAO.addMedicineToPrescription(prescriptionId, medicineId, amount) >> Optional.of(medicine)

		when:
		service.addMedicineToPrescription(prescriptionId, medicineId, amount)

		then:
		thrown(WrongAmountException)
	}

	def "addMedicineToPrescription returns empty when not added"() {
		given:
		prescriptionDAO.addMedicineToPrescription(1, "MED123", 2) >> Optional.empty()

		when:
		def result = service.addMedicineToPrescription(1, "MED123", 2)

		then:
		result.isEmpty()
	}

	def "removeMedicineFromPrescription returns count from DAO"() {
		given:
		prescriptionDAO.removeMedicineFromPrescription(1, "MED123") >> 1

		when:
		def result = service.removeMedicineFromPrescription(1, "MED123")

		then:
		result == 1
	}

	def "removePrescription calls DAO delete"() {
		given:
		prescriptionDAO.delete(1) >> 1

		when:
		def result = service.removePrescription(1)

		then:
		result == 1
	}

	def "issuePrescription should throw exception if prescription is already issued"() {
		given:
		def existingPrescription = Prescription.builder().id(1).status(PrescriptionStatus.ISSUED).build()
		prescriptionDAO.getById(1) >> Optional.of(existingPrescription)

		when:
		service.issuePrescription(1, LocalDate.now().plusDays(30))
		then:
		thrown(AlreadyIssuedException)
	}

	def "successfully issues a NEW prescription"() {
		given: "A prescription that hasn't been issued yet"
		def newPrescription = Prescription.builder()
				.id(1)
				.status(PrescriptionStatus.NEW)
				.build()

		def issuedPrescription = Prescription.builder()
				.id(1)
				.status(PrescriptionStatus.ISSUED)
				.build()

		and: "The DAO will be called in sequence"
		1 * prescriptionDAO.getById(1) >> Optional.of(newPrescription)
		1 * prescriptionDAO.updatePrescriptionStatus(1, PrescriptionStatus.ISSUED, LocalDate.now().plusDays(30)) >> 1
		1 * prescriptionDAO.getById(1) >> Optional.of(issuedPrescription)

		when: "We issue the prescription"
		def result = service.issuePrescription(1, LocalDate.now().plusDays(30))

		then: "It should return the issued prescription"
		result.status == PrescriptionStatus.ISSUED
	}

	def "issuePrescription should throw when update fails"() {
		given:
		def initialPrescription = Prescription.builder()
				.id(1)
				.status(PrescriptionStatus.NEW)
				.build()

		prescriptionDAO.getById(1) >> Optional.of(initialPrescription)
		prescriptionDAO.updatePrescriptionStatus(1, PrescriptionStatus.ISSUED, LocalDate.now().plusDays(30)) >> 0

		when:
		service.issuePrescription(1, LocalDate.now().plusDays(30))

		then:
		thrown(IllegalStateException)
	}
	def "getUserIdForPrescriptionById returns user ID from DAO"() {
		given:
		prescriptionDAO.getUserIdForPrescriptionById(1) >> 5

		when:
		def result = service.getUserIdForPrescriptionById(1)

		then:
		result == 5
	}

	def "getUserIdForPrescriptionById returns null when not found"() {
		given:
		prescriptionDAO.getUserIdForPrescriptionById(1) >> null

		when:
		def result = service.getUserIdForPrescriptionById(1)

		then:
		result == null
	}
}
