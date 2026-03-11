import { signalStore, withState, withMethods, withComputed, patchState } from '@ngrx/signals';
import { inject, computed } from '@angular/core';
import { CalendarService } from '../services/calendar.service';
import { Event, EventRequest } from '../../models/event';

interface CalendarState {
  events: Event[];
  loading: boolean;
  error: string | null;
  selectedEvent: Event | null;
}

const initialState: CalendarState = {
  events: [],
  loading: false,
  error: null,
  selectedEvent: null,
};

export const CalendarStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ events }) => ({
    upcomingEvents: computed(() =>
      events()
        .filter((e) => new Date(e.startTime) > new Date())
        .sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime()),
    ),
  })),
  withMethods((store, calendarService = inject(CalendarService)) => ({
    async loadEvents() {
      patchState(store, { loading: true, error: null });
      try {
        const events = await calendarService.getEvents().toPromise();
        patchState(store, { events, loading: false });
      } catch (error: any) {
        patchState(store, { error: 'Failed to load events', loading: false });
      }
    },

    async createEvent(event: EventRequest) {
      const newEvent = await calendarService.createEvent(event).toPromise();
      if (newEvent) {
        patchState(store, { events: [...store.events(), newEvent] });
      }
    },

    async updateEvent(id: number, event: EventRequest) {
      const updated = await calendarService.updateEvent(id, event).toPromise();
      if (updated) {
        patchState(store, {
          events: store.events().map((e) => (e.id === id ? updated : e)),
        });
      }
    },

    async deleteEvent(id: number) {
      await calendarService.deleteEvent(id).toPromise();
      patchState(store, {
        events: store.events().filter((e) => e.id !== id),
      });
    },

    selectEvent(event: Event | null) {
      patchState(store, { selectedEvent: event });
    },
  })),
);
