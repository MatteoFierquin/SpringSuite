package fr.matteofierquin.calendarservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventInvitee {

    @Column(name = "invitee_email")
    private String email;

    @Column(name = "invitee_status")
    @Builder.Default
    private String status = "PENDING";
}
