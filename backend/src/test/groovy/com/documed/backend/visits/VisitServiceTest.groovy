package com.documed.backend.visits

import com.documed.backend.auth.AuthService
import com.documed.backend.schedules.TimeSlotService
import com.documed.backend.services.ServiceService
import com.documed.backend.visits.model.ScheduleVisitDTO
import spock.lang.Specification
import spock.lang.Subject

class VisitServiceTest extends Specification {

    def visitDAO = Mock(VisitDAO)
    def serviceService = Mock(ServiceService)
    def timeSlotService = Mock(TimeSlotService)
    def authService = Mock(AuthService)

    @Subject
    def visitService = new VisitService(visitDAO, timeSlotService, authService, serviceService)

    def "scheduleVisit should create and reserve needed time slots"() {
        given:
        def dto = new ScheduleVisitDTO("patient info", 1,2 , 3)
        def slot = Mock(TimeSlot)
        def createdVisit = Visit.builder()
                .id(333)
                .facilityId(222)
                .serviceId(dto.serviceId)
                .patientId(dto.patientId)
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


}
