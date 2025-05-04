package com.documed.backend.schedules

import com.documed.backend.schedules.model.TimeSlot
import spock.lang.Specification
import com.documed.backend.schedules.model.WorkTime
import java.time.DayOfWeek
import java.time.LocalTime

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
}
