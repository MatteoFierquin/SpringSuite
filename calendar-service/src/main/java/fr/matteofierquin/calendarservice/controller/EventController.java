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
            @RequestHeader("X-User-Name") String username) {
        List<EventResponse> events = eventService.getUserEvents(username);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable Long id,
            @RequestHeader("X-User-Name") String username) {
        EventResponse event = eventService.getEventById(id, username);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request,
            @RequestHeader("X-User-Name") String username) {
        EventResponse created = eventService.createEvent(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            @RequestHeader("X-User-Name") String username) {
        EventResponse updated = eventService.updateEvent(id, request, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @RequestHeader("X-User-Name") String username) {
        eventService.deleteEvent(id, username);
        return ResponseEntity.noContent().build();
    }

    // GET /api/calendar/events/range?start=2026-03-01T00:00:00&end=2026-03-31T23:59:59
    @GetMapping("/range")
    public ResponseEntity<List<EventResponse>> getEventsByDateRange(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestHeader("X-User-Name") String username) {
        List<EventResponse> events = eventService.getEventsByDateRange(username, start, end);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<EventResponse> inviteUserToEvent(
            @PathVariable Long id,
            @RequestParam String inviteeUsername,
            @RequestHeader("X-User-Name") String username) {
        EventResponse updatedEvent = eventService.inviteUserToEvent(id, username, inviteeUsername);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{id}/invitees")
    public ResponseEntity<EventResponse> addInvitee(
            @PathVariable Long id,
            @RequestBody InviteeRequest request,
            @RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(eventService.addInvitee(id, username, request.email()));
    }

    @PutMapping("/{id}/invitees")
    public ResponseEntity<EventResponse> updateInvitees(
            @PathVariable Long id,
            @RequestBody InviteesUpdateRequest request,
            @RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(eventService.updateInvitees(id, username, request.invitees()));
    }

    @PatchMapping("/{id}/invitees/{email}")
    public ResponseEntity<EventResponse> updateInviteeStatus(
            @PathVariable Long id,
            @PathVariable String email,
            @RequestBody InviteeStatusRequest request,
            @RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(eventService.updateInviteeStatus(id, username, email, request.status()));
    }

    @DeleteMapping("/{id}/invitees/{email}")
    public ResponseEntity<EventResponse> removeInvitee(
            @PathVariable Long id,
            @PathVariable String email,
            @RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(eventService.removeInvitee(id, username, email));
    }
}