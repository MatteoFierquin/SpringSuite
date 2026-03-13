package fr.matteofierquin.calendarservice.controller;

import fr.matteofierquin.calendarservice.dto.EventRequest;
import fr.matteofierquin.calendarservice.dto.EventResponse;
import fr.matteofierquin.calendarservice.dto.InviteeRequest;
import fr.matteofierquin.calendarservice.dto.InviteeStatusRequest;
import fr.matteofierquin.calendarservice.dto.InviteesUpdateRequest;
import fr.matteofierquin.calendarservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getUserEvents(
            @RequestHeader("X-User-Id") String userId) {
        List<EventResponse> events = eventService.getUserEvents(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        EventResponse event = eventService.getEventById(id, userId);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request,
            @RequestHeader("X-User-Id") String userId) {
        EventResponse created = eventService.createEvent(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            @RequestHeader("X-User-Id") String userId) {
        EventResponse updated = eventService.updateEvent(id, request, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/calendar/events/range?start=2026-03-01T00:00:00&end=2026-03-31T23:59:59
    @GetMapping("/range")
    public ResponseEntity<List<EventResponse>> getEventsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestHeader("X-User-Id") String userId) {
        List<EventResponse> events = eventService.getEventsByDateRange(userId, start, end);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<EventResponse> inviteUserToEvent(
            @PathVariable Long id,
            @RequestParam String inviteeUserId,
            @RequestHeader("X-User-Id") String userId) {
        EventResponse updatedEvent = eventService.inviteUserToEvent(id, userId, inviteeUserId);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{id}/invitees")
    public ResponseEntity<EventResponse> addInvitee(
            @PathVariable Long id,
            @RequestBody InviteeRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(eventService.addInvitee(id, userId, request.email()));
    }

    @PutMapping("/{id}/invitees")
    public ResponseEntity<EventResponse> updateInvitees(
            @PathVariable Long id,
            @RequestBody InviteesUpdateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(eventService.updateInvitees(id, userId, request.invitees()));
    }

    @PatchMapping("/{id}/invitees/{email}")
    public ResponseEntity<EventResponse> updateInviteeStatus(
            @PathVariable Long id,
            @PathVariable String email,
            @RequestBody InviteeStatusRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(eventService.updateInviteeStatus(id, userId, email, request.status()));
    }

    @DeleteMapping("/{id}/invitees/{email}")
    public ResponseEntity<EventResponse> removeInvitee(
            @PathVariable Long id,
            @PathVariable String email,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(eventService.removeInvitee(id, userId, email));
    }
}