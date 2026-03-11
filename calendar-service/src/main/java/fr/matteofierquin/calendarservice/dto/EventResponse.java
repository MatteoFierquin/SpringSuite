package fr.matteofierquin.calendarservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String owner,
        List<String> attendees,
        LocalDateTime createdAt
) {}