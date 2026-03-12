package fr.matteofierquin.calendarservice.mapper;

import fr.matteofierquin.calendarservice.dto.EventRequest;
import fr.matteofierquin.calendarservice.dto.EventResponse;
import fr.matteofierquin.calendarservice.model.Event;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public Event toEntity(EventRequest request, String owner) {
        return Event.builder()
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .owner(owner)
                .attendees(request.attendees() != null ? request.attendees() : new java.util.ArrayList<>())
                .build();
    }

    public EventResponse toResponse(Event event) {
        List<EventResponse.InviteeResponse> invitees = event.getInvitees().stream()
                .map(i -> new EventResponse.InviteeResponse(i.getEmail(), i.getStatus()))
                .collect(Collectors.toList());
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getOwner(),
                event.getAttendees(),
                invitees,
                event.getCreatedAt()
        );
    }
}