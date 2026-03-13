package fr.matteofierquin.calendarservice.repository;

import fr.matteofierquin.calendarservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOwner(String userId);

    // Find events where user is attendee
    List<Event> findByAttendeesContaining(String userId);

    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find events for owner in date range
    List<Event> findByOwnerAndStartTimeBetween(String owner, LocalDateTime start, LocalDateTime end);
}