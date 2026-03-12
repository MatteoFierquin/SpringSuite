package fr.matteofierquin.calendarservice.service;

import fr.matteofierquin.calendarservice.dto.EventRequest;
import fr.matteofierquin.calendarservice.dto.EventResponse;
import fr.matteofierquin.calendarservice.dto.InviteesUpdateRequest;
import fr.matteofierquin.calendarservice.mapper.EventMapper;
import fr.matteofierquin.calendarservice.model.Event;
import fr.matteofierquin.calendarservice.model.EventInvitee;
import fr.matteofierquin.calendarservice.repository.EventRepository;
import fr.matteofierquin.calendarservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventResponse createEvent(EventRequest request, String username) {
        Event event = eventMapper.toEntity(request, username);
        Event saved = eventRepository.save(event);
        return eventMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getUserEvents(String username) {
        List<Event> events = eventRepository.findByOwner(username);
        return events.stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id, String username) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        // only owner or attendee can view
        if (!event.getOwner().equals(username) && !event.getAttendees().contains(username)) {
            throw new IllegalArgumentException("You don't have access to this event");
        }

        return eventMapper.toResponse(event);
    }

    public EventResponse updateEvent(Long id, EventRequest request, String username) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        // only owner can update
        if (!event.getOwner().equals(username)) {
            throw new IllegalArgumentException("Only event owner can update");
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        if (request.attendees() != null) {
            event.setAttendees(request.attendees());
        }

        Event updated = eventRepository.save(event);
        return eventMapper.toResponse(updated);
    }

    public void deleteEvent(Long id, String username) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        // only owner can delete
        if (!event.getOwner().equals(username)) {
            throw new IllegalArgumentException("Only event owner can delete");
        }

        eventRepository.delete(event);
    }

    // Get events in date range
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByDateRange(String username, LocalDateTime start, LocalDateTime end) {
        List<Event> events = eventRepository.findByOwnerAndStartTimeBetween(username, start, end);
        return events.stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

    public EventResponse inviteUserToEvent(Long id, String username, String inviteeUsername) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        // only owner can invite
        if (!event.getOwner().equals(username)) {
            throw new IllegalArgumentException("Only event owner can invite");
        }

        if (event.getAttendees().contains(inviteeUsername)) {
            throw new IllegalArgumentException("User is already an attendee");
        }

        event.getAttendees().add(inviteeUsername);
        Event updated = eventRepository.save(event);
        return eventMapper.toResponse(updated);
    }

    public EventResponse addInvitee(Long id, String username, String email) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (!event.getOwner().equals(username)) {
            throw new IllegalArgumentException("Only event owner can manage invitees");
        }

        boolean alreadyInvited = event.getInvitees().stream()
                .anyMatch(i -> i.getEmail().equalsIgnoreCase(email));
        if (alreadyInvited) {
            throw new IllegalArgumentException("User is already invited");
        }

        event.getInvitees().add(EventInvitee.builder().email(email).build());
        return eventMapper.toResponse(eventRepository.save(event));
    }

    public EventResponse updateInvitees(Long id, String username, java.util.List<InviteesUpdateRequest.InviteeItem> invitees) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (!event.getOwner().equals(username)) {
            throw new IllegalArgumentException("Only event owner can manage invitees");
        }

        java.util.List<EventInvitee> updated = invitees.stream()
                .map(item -> EventInvitee.builder()
                        .email(item.email())
                        .status(item.status() != null ? item.status() : "PENDING")
                        .build())
                .collect(java.util.stream.Collectors.toList());

        event.getInvitees().clear();
        event.getInvitees().addAll(updated);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    public EventResponse updateInviteeStatus(Long id, String username, String email, String status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        // owner or the invitee themselves can update status
        EventInvitee invitee = event.getInvitees().stream()
                .filter(i -> i.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Invitee not found: " + email));

        if (!event.getOwner().equals(username) && !invitee.getEmail().equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("Not authorized to update this invitee status");
        }

        invitee.setStatus(status);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    public EventResponse removeInvitee(Long id, String username, String email) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (!event.getOwner().equals(username)) {
            throw new IllegalArgumentException("Only event owner can remove invitees");
        }

        event.getInvitees().removeIf(i -> i.getEmail().equalsIgnoreCase(email));
        return eventMapper.toResponse(eventRepository.save(event));
    }
}