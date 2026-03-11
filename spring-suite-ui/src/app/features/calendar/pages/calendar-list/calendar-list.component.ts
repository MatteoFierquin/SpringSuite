import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { CalendarStore } from '../../../../core/stores/calendar.store';
import { EventCardComponent } from '../../components/event-card/event-card.component';
import { EventDialogComponent } from '../../components/event-dialog/event-dialog.component';

@Component({
  selector: 'app-calendar-list',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    EventCardComponent,
  ],
  template: `
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold text-gray-800">Calendar</h1>
        <p class="text-gray-600 mt-1">
          {{ calendarStore.events().length }} event(s) -
          {{ calendarStore.upcomingEvents().length }} upcoming
        </p>
      </div>
      <button mat-raised-button color="primary" class="flex items-center gap-2" (click)="openCreateDialog()">
        <mat-icon>add</mat-icon>
        New Event
      </button>
    </div>

    @if (calendarStore.loading()) {
      <div class="flex justify-center py-16">
        <mat-spinner diameter="48"></mat-spinner>
      </div>
    } @else if (calendarStore.error()) {
      <div class="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
        {{ calendarStore.error() }}
      </div>
    } @else if (calendarStore.events().length === 0) {
      <div class="text-center py-16">
        <mat-icon class="text-6xl text-gray-300 mb-4">event</mat-icon>
        <h3 class="text-xl font-semibold text-gray-600">No events yet</h3>
        <p class="text-gray-500 mt-2">Create your first event to get started</p>
      </div>
    } @else {
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        @for (event of calendarStore.events(); track event.id) {
          <app-event-card [event]="event" (deleteClick)="deleteEvent($event)" />
        }
      </div>
    }
  `,
})
export class CalendarListComponent {
  protected calendarStore = inject(CalendarStore);
  private dialog = inject(MatDialog);

  ngOnInit() {
    this.calendarStore.loadEvents();
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(EventDialogComponent, {
      width: '500px',
      disableClose: false,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.calendarStore.createEvent(result);
      }
    });
  }

  deleteEvent(id: number) {
    if (confirm('Are you sure you want to delete this event?')) {
      this.calendarStore.deleteEvent(id);
    }
  }
}
