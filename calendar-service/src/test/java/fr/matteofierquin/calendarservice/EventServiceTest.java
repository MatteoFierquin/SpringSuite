package fr.matteofierquin.calendarservice;

import fr.matteofierquin.calendarservice.dto.EventRequest;
import fr.matteofierquin.calendarservice.dto.EventResponse;
import fr.matteofierquin.calendarservice.dto.InviteesUpdateRequest;
import fr.matteofierquin.calendarservice.exception.ResourceNotFoundException;
import fr.matteofierquin.calendarservice.mapper.EventMapper;
import fr.matteofierquin.calendarservice.model.Event;
import fr.matteofierquin.calendarservice.model.EventInvitee;
import fr.matteofierquin.calendarservice.repository.EventRepository;
import fr.matteofierquin.calendarservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventRequest request;
    private EventResponse response;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        request = new EventRequest(
                "Team Meeting",
                start,
                end,
                "Weekly sync",
                "Conference Room A",
                new ArrayList<>()
        );

        event = Event.builder()
                .id(1L)
                .title("Team Meeting")
                .owner("testuser")
                .startTime(start)
                .endTime(end)
                .attendees(new ArrayList<>())
                .invitees(new ArrayList<>())
                .build();

        response = new EventResponse(
                1L,
                "Team Meeting",
                "Weekly sync",
                "Conference Room A",
                start,
                end,
                "testuser",
                new ArrayList<>(),
                new ArrayList<>(),
                LocalDateTime.now()
        );
    }

    @Test
    void createEvent_ShouldReturnEventResponse() {
        when(eventMapper.toEntity(any(), any())).thenReturn(event);
        when(eventRepository.save(any())).thenReturn(event);
        when(eventMapper.toResponse(any())).thenReturn(response);

        EventResponse result = eventService.createEvent(request, "testuser");

        assertNotNull(result);
        assertEquals("Team Meeting", result.title());
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    void getEventById_WhenExists_ShouldReturnEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(any())).thenReturn(response);

        EventResponse result = eventService.getEventById(1L, "testuser");

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getEventById_WhenNotExists_ShouldThrowException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                eventService.getEventById(99L, "testuser"));
    }

    @Test
    void inviteUserToEvent_ShouldAddAttendee() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(eventMapper.toResponse(any())).thenReturn(response);

        EventResponse result = eventService.inviteUserToEvent(1L, "testuser", "invitee");

        assertNotNull(result);
        assertTrue(event.getAttendees().contains("invitee"));
        verify(eventRepository, times(1)).save(any());
    }

    // --- Invitee tests ---

    @Test
    void addInvitee_ShouldAddToInviteesList() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(eventMapper.toResponse(any())).thenReturn(response);

        eventService.addInvitee(1L, "testuser", "guest@example.com");

        assertEquals(1, event.getInvitees().size());
        assertEquals("guest@example.com", event.getInvitees().get(0).getEmail());
        assertEquals("PENDING", event.getInvitees().get(0).getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    void addInvitee_WhenAlreadyInvited_ShouldThrow() {
        event.getInvitees().add(EventInvitee.builder().email("guest@example.com").status("PENDING").build());
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.addInvitee(1L, "testuser", "guest@example.com"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void addInvitee_WhenNotOwner_ShouldThrow() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.addInvitee(1L, "otheruser", "guest@example.com"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateInvitees_ShouldReplaceList() {
        event.getInvitees().add(EventInvitee.builder().email("old@example.com").status("PENDING").build());
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(eventMapper.toResponse(any())).thenReturn(response);

        List<InviteesUpdateRequest.InviteeItem> newInvitees = List.of(
                new InviteesUpdateRequest.InviteeItem("new1@example.com", "PENDING"),
                new InviteesUpdateRequest.InviteeItem("new2@example.com", "ACCEPTED")
        );

        eventService.updateInvitees(1L, "testuser", newInvitees);

        assertEquals(2, event.getInvitees().size());
        assertFalse(event.getInvitees().stream().anyMatch(i -> i.getEmail().equals("old@example.com")));
        verify(eventRepository).save(event);
    }

    @Test
    void updateInvitees_WhenNotOwner_ShouldThrow() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.updateInvitees(1L, "otheruser", List.of()));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateInviteeStatus_ShouldChangeStatus() {
        EventInvitee invitee = EventInvitee.builder().email("guest@example.com").status("PENDING").build();
        event.getInvitees().add(invitee);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(eventMapper.toResponse(any())).thenReturn(response);

        eventService.updateInviteeStatus(1L, "testuser", "guest@example.com", "ACCEPTED");

        assertEquals("ACCEPTED", invitee.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    void updateInviteeStatus_WhenInviteeNotFound_ShouldThrow() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ResourceNotFoundException.class, () ->
                eventService.updateInviteeStatus(1L, "testuser", "nobody@example.com", "ACCEPTED"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void removeInvitee_ShouldRemoveFromList() {
        event.getInvitees().add(EventInvitee.builder().email("guest@example.com").status("PENDING").build());
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(eventMapper.toResponse(any())).thenReturn(response);

        eventService.removeInvitee(1L, "testuser", "guest@example.com");

        assertTrue(event.getInvitees().isEmpty());
        verify(eventRepository).save(event);
    }

    @Test
    void removeInvitee_WhenNotOwner_ShouldThrow() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.removeInvitee(1L, "otheruser", "guest@example.com"));
        verify(eventRepository, never()).save(any());
    }
}
