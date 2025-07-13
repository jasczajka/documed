package com.documed.backend.services

import com.documed.backend.services.model.Service
import com.documed.backend.services.model.ServiceType
import com.documed.backend.users.model.Specialization
import com.documed.backend.users.model.Subscription
import com.documed.backend.users.services.SubscriptionService
import com.documed.backend.users.services.UserService
import spock.lang.Specification

class ServiceServiceTest extends Specification {

	def serviceDAO = Mock(ServiceDAO)
	def subscriptionService = Mock(SubscriptionService)
	def userService = Mock(UserService)
	def serviceService = new ServiceService(serviceDAO, subscriptionService, userService)

	private Service buildService(Map overrides = [:]) {
		return Service.builder()
				.id(overrides.id ?: 1)
				.name(overrides.name ?: "Konsultacja")
				.price(overrides.price ?: BigDecimal.valueOf(120))
				.type(overrides.type ?: ServiceType.REGULAR_SERVICE)
				.estimatedTime(overrides.estimatedTime ?: 45)
				.build()
	}

	def "getAll returns all services"() {
		given:
		def service = buildService()
		serviceDAO.getAll() >> [service]

		when:
		def result = serviceService.getAll()

		then:
		result == [service]
	}

	def "getById returns service when found"() {
		given:
		def service = Service.builder()
				.id(2)
				.name("Wypelnienie")
				.price(BigDecimal.valueOf(200))
				.type(ServiceType.ADDITIONAL_SERVICE)
				.estimatedTime(60)
				.build()
		serviceDAO.getById(2) >> Optional.of(service)

		when:
		def result = serviceService.getById(2)

		then:
		result.isPresent()
		result.get() == service
	}

	def "createService creates service and adds specializations"() {
		given:
		def initialService = Service.builder()
				.name("Leczenie kanałowe")
				.price(BigDecimal.valueOf(500))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(90)
				.build()

		def createdService = Service.builder()
				.id(3)
				.name("Leczenie kanałowe")
				.price(BigDecimal.valueOf(500))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(90)
				.build()

		serviceDAO.create(_) >> createdService
		serviceDAO.addSpecializationsToService(3, [1, 2]) >> createdService

		when:
		def result = serviceService.createService(
				"Leczenie kanałowe",
				BigDecimal.valueOf(500),
				ServiceType.REGULAR_SERVICE,
				90,
				[1, 2]
				)

		then:
		result.id == 3
		result.name == "Leczenie kanałowe"
	}


	def "updatePrice updates price if valid"() {
		given:
		def updated = Service.builder()
				.id(1)
				.name("Rengten")
				.price(BigDecimal.valueOf(150))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(20)
				.build()
		serviceDAO.updatePrice(1, BigDecimal.valueOf(150)) >> updated

		when:
		def result = serviceService.updatePrice(1, BigDecimal.valueOf(150))

		then:
		result.price == BigDecimal.valueOf(150)
	}
	def "updatePrice throws when price invalid"() {
		given:
		def updated = Service.builder()
				.id(1)
				.name("Rengten")
				.price(BigDecimal.valueOf(150))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(20)
				.build()

		when:
		def result = serviceService.updatePrice(1, BigDecimal.valueOf(0))

		then:
		thrown(IllegalArgumentException)
	}

	def "updateEstimatedTime updates time if valid"() {
		given:
		def updated = Service.builder()
				.id(4)
				.name("Ultradźwięki")
				.price(BigDecimal.valueOf(300))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(40)
				.build()
		serviceDAO.updateEstimatedTime(4, 40) >> updated

		when:
		def result = serviceService.updateEstimatedTime(4, 40)

		then:
		result.estimatedTime == 40
	}

	def "updateEstimatedTime throws when time invalid"() {
		given:
		def updated = Service.builder()
				.id(4)
				.name("Ultradźwięki")
				.price(BigDecimal.valueOf(300))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(40)
				.build()

		when:
		def result = serviceService.updateEstimatedTime(4, 0)

		then:
		thrown(IllegalArgumentException)
	}

	def "addSpecializationToService calls DAO and returns specialization"() {
		given:
		def specialization = Specialization.builder()
				.id(1)
				.name("Radiologia")
				.build()
		serviceDAO.addSpecializationToService(5, 1) >> specialization

		when:
		def result = serviceService.addSpecializationToService(5, 1)

		then:
		result == specialization
	}

	def "addSpecializationsToService calls DAO and returns updated service"() {
		given:
		def service = Service.builder()
				.id(6)
				.name("MRI")
				.price(BigDecimal.valueOf(800))
				.type(ServiceType.REGULAR_SERVICE)
				.estimatedTime(60)
				.build()
		serviceDAO.addSpecializationsToService(6, [3, 4]) >> service

		when:
		def result = serviceService.addSpecializationsToService(6, [3, 4])

		then:
		result == service
	}

	def "removeSpecializationFromService removes specialization from service"() {
		given:
		def serviceId = 3
		def specializationId = 2

		serviceDAO.removeSpecializationFromService(serviceId, specializationId) >> 1

		when:
		def result = serviceService.removeSpecializationFromService(serviceId, specializationId)

		then:
		result == 1
	}

	def "delete deletes service"() {
		given:
		def serviceId = 3

		serviceDAO.delete(serviceId) >> 1

		when:
		def result = serviceService.delete(serviceId)

		then:
		result == 1
	}

	def "calculateTotalCost should return basic price when no subscription"() {
		given:
		def serviceId = 1
		def patientId = 10
		def basicPrice = BigDecimal.valueOf(100)
		def service = buildService(price: basicPrice)

		when:
		def result = serviceService.calculateTotalCost(serviceId, patientId)

		then:
		1 * serviceDAO.getById(serviceId) >> Optional.of(service)
		1 * userService.getSubscriptionIdForPatient(patientId) >> 0
		result == basicPrice
	}

	def "calculateTotalCost should apply discount when subscription exists"() {
		given:
		def serviceId = 1
		def patientId = 10
		def subscriptionId = 5
		def basicPrice = BigDecimal.valueOf(100)
		def discount = 20
		def service = buildService(price: basicPrice)

		when:
		def result = serviceService.calculateTotalCost(serviceId, patientId)

		then:
		1 * serviceDAO.getById(serviceId) >> Optional.of(service)
		1 * userService.getSubscriptionIdForPatient(patientId) >> subscriptionId
		1 * subscriptionService.getDiscountForService(serviceId, subscriptionId) >> discount
		result == BigDecimal.valueOf(80)
	}

	def "calculateTotalCost should return basic price when discount is 0"() {
		given:
		def serviceId = 1
		def patientId = 10
		def subscriptionId = 3
		def basicPrice = BigDecimal.valueOf(100)
		def discount = 0
		def service = buildService(price: basicPrice)

		when:
		def result = serviceService.calculateTotalCost(serviceId, patientId)

		then:
		1 * serviceDAO.getById(serviceId) >> Optional.of(service)
		1 * userService.getSubscriptionIdForPatient(patientId) >> subscriptionId
		1 * subscriptionService.getDiscountForService(serviceId, subscriptionId) >> discount
		result == basicPrice
	}

	def "calculateTotalCost should return basic price when discount results in zero or negative multiplier"() {
		given:
		def serviceId = 1
		def patientId = 10
		def subscriptionId = 5
		def basicPrice = BigDecimal.valueOf(100)
		def discount = 105
		def service = buildService(price: basicPrice)

		when:
		def result = serviceService.calculateTotalCost(serviceId, patientId)

		then:
		1 * serviceDAO.getById(serviceId) >> Optional.of(service)
		1 * userService.getSubscriptionIdForPatient(patientId) >> subscriptionId
		1 * subscriptionService.getDiscountForService(serviceId, subscriptionId) >> discount
		result == basicPrice
	}
}
