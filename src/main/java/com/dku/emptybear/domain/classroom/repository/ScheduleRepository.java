package com.dku.emptybear.domain.classroom.repository;

import com.dku.emptybear.domain.classroom.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByClassroom_ClassroomIdInAndDayOfWeekOrderByStartTimeAsc(
            Collection<Long> classroomIds,
            String dayOfWeek
    );
}