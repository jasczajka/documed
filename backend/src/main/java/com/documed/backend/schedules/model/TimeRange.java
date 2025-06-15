package com.documed.backend.schedules.model;

import java.time.LocalTime;

public record TimeRange(LocalTime startTime, LocalTime endTime) {}
