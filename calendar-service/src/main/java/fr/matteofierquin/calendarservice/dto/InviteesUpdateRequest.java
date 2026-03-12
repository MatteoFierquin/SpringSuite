package fr.matteofierquin.calendarservice.dto;

import java.util.List;

public record InviteesUpdateRequest(List<InviteeItem> invitees) {
    public record InviteeItem(String email, String status) {}
}
