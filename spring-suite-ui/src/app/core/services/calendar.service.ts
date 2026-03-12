import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Event, EventRequest, EventInvitee } from '../../models/event';

@Injectable({ providedIn: 'root' })
export class CalendarService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/calendar/events`;

  getEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }

  getEvent(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`);
  }

  createEvent(event: EventRequest): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, event);
  }

  updateEvent(id: number, event: EventRequest): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, event);
  }

  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getEventsByDateRange(start: string, end: string): Observable<Event[]> {
    return this.http.get<Event[]>(`${this.apiUrl}/range`, {
      params: { start, end },
    });
  }

  // Invitation endpoints
  addInvitee(eventId: number, email: string): Observable<Event> {
    return this.http.post<Event>(`${this.apiUrl}/${eventId}/invitees`, { email });
  }

  updateInviteeStatus(
    eventId: number,
    email: string,
    status: 'PENDING' | 'ACCEPTED' | 'DECLINED',
  ): Observable<Event> {
    return this.http.patch<Event>(
      `${this.apiUrl}/${eventId}/invitees/${encodeURIComponent(email)}`,
      { status },
    );
  }

  removeInvitee(eventId: number, email: string): Observable<Event> {
    return this.http.delete<Event>(
      `${this.apiUrl}/${eventId}/invitees/${encodeURIComponent(email)}`,
    );
  }

  updateInvitees(eventId: number, invitees: EventInvitee[]): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${eventId}/invitees`, { invitees });
  }
}
