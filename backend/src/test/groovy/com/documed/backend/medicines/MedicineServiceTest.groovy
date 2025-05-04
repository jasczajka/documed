package com.documed.backend.medicines

import com.documed.backend.medicines.model.Medicine
import spock.lang.Specification
import spock.lang.Subject

class MedicineServiceTest extends Specification {

	MedicineDAO medicineDAO = Mock()

	@Subject
	MedicineService service = new MedicineService(medicineDAO)

	def "getAll returns all medicines"() {
		given:
		def meds = [
			Medicine.builder().id("A").name("Med A").commonName("Common A").dosage("10mg").build(),
			Medicine.builder().id("B").name("Med B").commonName("Common B").dosage("20mg").build()
		]
		medicineDAO.getAll() >> meds

		when:
		def result = service.getAll()

		then:
		result == meds
	}

	def "getById returns medicine when found"() {
		given:
		def med = Medicine.builder().id("X").name("Med X").commonName("Common X").dosage("5mg").build()
		medicineDAO.getById("X") >> Optional.of(med)

		when:
		def result = service.getById("X")

		then:
		result.isPresent() && result.get() == med
	}

	def "getById returns empty when not found"() {
		given:
		medicineDAO.getById("Y") >> Optional.empty()

		when:
		def result = service.getById("Y")

		then:
		result.isEmpty()
	}

	def "search returns matching medicines and respects limit"() {
		given:
		def query = "aspirin"
		def limit = 3
		def hits = [
			Medicine.builder().id("M1").name("Aspirin 100").commonName("Acetylsalicylic acid").dosage("100mg").build(),
			Medicine.builder().id("M2").name("Aspirin 200").commonName("Acetylsalicylic acid").dosage("200mg").build()
		]
		// simulate DAO.search being called with exact args
		1 * medicineDAO.search(query, limit) >> hits

		when:
		def result = service.search(query, limit)

		then:
		result == hits
	}

	def "getLimited returns the first N medicines"() {
		given:
		def limit = 5
		def subset = (1..limit).collect { i ->
			Medicine.builder()
					.id("ID$i")
					.name("Name$i")
					.commonName("Common$i")
					.dosage("${i * 10}mg")
					.build()
		}
		1 * medicineDAO.getLimited(limit) >> subset

		when:
		def result = service.getLimited(limit)

		then:
		result.size() == limit
		result == subset
	}
}
