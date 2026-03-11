package fr.matteofierquin.calendarservice.service;

import fr.matteofierquin.calendarservice.dto.EventRequest;
import fr.matteofierquin.calendarservice.dto.EventResponse;
import fr.matteofierquin.calendarservice.mapper.EventMapper;
import fr.matteofierquin.calendarservice.model.Event;
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
}