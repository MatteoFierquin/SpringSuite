package fr.matteofierquin.calendarservice;

import fr.matteofierquin.calendarservice.dto.EventRequest;
import fr.matteofierquin.calendarservice.dto.EventResponse;
import fr.matteofierquin.calendarservice.mapper.EventMapper;
import fr.matteofierquin.calendarservice.model.Event;
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
        request = new EventRequest(
                "Team Meeting",
                "Weekly sync",
                "Conference Room A",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                new ArrayList<>()
        );

        event = Event.builder()
                .id(1L)
                .title("Team Meeting")
                .owner("testuser")
                .startTime(request.startTime())
                .endTime(request.endTime())
                .attendees(new ArrayList<>())
                .build();

        response = new EventResponse(
                1L,
                "Team Meeting",
                "Weekly sync",
                "Conference Room A",
                request.startTime(),
                request.endTime(),
                "testuser",
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
        assertEquals("Team Meeting", result.getTitle());
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    void getEventById_WhenExists_ShouldReturnEvent() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(any())).thenReturn(response);

        EventResponse result = eventService.getEventById(1L, "testuser");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getEventById_WhenNotExists_ShouldThrowException() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () ->
                eventService.getEventById(99L, "testuser"));
    }
}