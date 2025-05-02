package com.documed.backend.schedules

import com.documed.backend.schedules.model.WorkTime
import spock.lang.Specification

import java.time.DayOfWeek
import java.time.LocalTime

class WorkTimeServiceTest extends Specification{

    def "should create WorkTime"() {
        given:
        def dao = Mock(WorkTimeDAO)
        def service = new WorkTimeService(dao)
        def workTime = WorkTime.builder()
            .userId(1)
            .dayOfWeek(DayOfWeek.FRIDAY)
            .startTime(LocalTime.of(10, 00))
            .endTime(LocalTime.of(15, 00))
            .build()

        when:
        def result = service.createWorkTime(workTime)

        then:
        1 * dao.create(workTime) >> workTime
        result == workTime
    }
}
