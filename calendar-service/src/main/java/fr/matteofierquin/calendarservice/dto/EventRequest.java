package fr.matteofierquin.calendarservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record EventRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        LocalDateTime endTime,

        String description,
        String location,
        List<String> attendees
) {
    // Compact constructor - runs after the default constructor and can be used for validation and normalization
    public EventRequest {
        // Validate end time is after start time
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Normalize strings (trim whitespace)
        title = title.trim();
        if (description != null) description = description.trim();
        if (location != null) location = location.trim();
    }
}