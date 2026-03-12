import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { CalendarStore } from '../../../../core/stores/calendar.store';
import { EventDialogComponent } from '../../components/event-dialog/event-dialog.component';
import { Event } from '../../../../models/event';

interface CalendarDay {
  date: Date;
  isCurrentMonth: boolean;
  isToday: boolean;
  events: Event[];
}

interface CalendarWeek {
  days: CalendarDay[];
}

@Component({
  selector: 'app-calendar-grid',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatButtonToggleModule,
  ],
  template: `
    <div class="mb-6 flex justify-between items-center">
      <div class="flex items-center gap-4">
        <div class="flex items-center gap-2">
          <button mat-icon-button (click)="previousMonth()">
            <mat-icon>chevron_left</mat-icon>
          </button>
          <button mat-icon-button (click)="nextMonth()">
            <mat-icon>chevron_right</mat-icon>
          </button>
        </div>
        <h1 class="text-2xl font-bold text-gray-800 min-w-[200px]">
          {{ currentMonthName() }} {{ currentYear() }}
        </h1>
      </div>

      <div class="flex items-center gap-3">
        <button mat-button (click)="goToToday()">Today</button>
        <button mat-raised-button color="primary" class="flex items-center gap-2" (click)="openCreateDialog()">
          <mat-icon>add</mat-icon>
          New Event
        </button>
      </div>
    </div>

    @if (calendarStore.loading()) {
      <div class="flex justify-center py-16">
        <mat-spinner diameter="48"></mat-spinner>
      </div>
    } @else if (calendarStore.error()) {
      <div class="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
        {{ calendarStore.error() }}
      </div>
    } @else {
      <!-- Weekday Headers -->
      <div class="grid grid-cols-7 border-b border-gray-200">
        @for (day of weekdayHeaders; track day) {
          <div class="py-3 text-center text-sm font-semibold text-gray-600">{{ day }}</div>
        }
      </div>

      <!-- Calendar Grid -->
      <div class="grid grid-cols-7 border-b border-gray-200">
        @for (week of calendarGrid(); track $index) {
          @for (day of week.days; track day.date) {
            <div
              class="min-h-[120px] p-2 border-r border-gray-200 bg-white hover:bg-gray-50 transition-colors"
              [class.bg-gray-50]="!day.isCurrentMonth"
              [class.ring-2]="day.isToday"
              [class.ring-blue-500]="day.isToday"
              (click)="onDayClick(day)"
            >
              <div class="flex items-center justify-between mb-1">
                <span
                  class="text-sm font-medium w-7 h-7 flex items-center justify-center rounded-full"
                  [class.bg-blue-600]="day.isToday"
                  [class.text-white]="day.isToday"
                  [class.text-gray-900]="!day.isToday"
                >
                  {{ day.date.getDate() }}
                </span>
                @if (day.events.length > 3) {
                  <span class="text-xs text-gray-500">+{{ day.events.length - 3 }} more</span>
                }
              </div>

              <div class="space-y-1">
                @for (event of day.events.slice(0, 3); track event.id) {
                  <div
                    class="text-xs px-2 py-1 rounded truncate cursor-pointer bg-blue-100 text-blue-800 hover:bg-blue-200 transition-colors"
                    (click)="onEventClick($event, event)"
                    [title]="event.title"
                  >
                    {{ event.title }}
                  </div>
                }
              </div>
            </div>
          }
        }
      </div>
    }
  `,
})
export class CalendarGridComponent {
  protected calendarStore = inject(CalendarStore);
  private dialog = inject(MatDialog);

  readonly weekdayHeaders = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  currentDate = signal(new Date());

  calendarGrid = computed<CalendarWeek[]>(() => {
    const year = this.currentDate().getFullYear();
    const month = this.currentDate().getMonth();
    const events = this.calendarStore.events();

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);

    const startDay = new Date(firstDayOfMonth);
    startDay.setDate(startDay.getDate() - startDay.getDay());

    const weeks: CalendarWeek[] = [];
    let currentWeek: CalendarDay[] = [];

    let current = new Date(startDay);
    while (current <= lastDayOfMonth || current.getDay() !== 0) {
      const date = new Date(current);
      const isCurrentMonth = date.getMonth() === month;
      const isToday = this.isToday(date);

      const dayEvents = events.filter((event) => {
        const eventStart = new Date(event.startTime);
        return (
          eventStart.getFullYear() === date.getFullYear() &&
          eventStart.getMonth() === date.getMonth() &&
          eventStart.getDate() === date.getDate()
        );
      });

      currentWeek.push({
        date,
        isCurrentMonth,
        isToday,
        events: dayEvents,
      });

      if (currentWeek.length === 7) {
        weeks.push({ days: currentWeek });
        currentWeek = [];
      }

      current.setDate(current.getDate() + 1);
    }

    if (currentWeek.length > 0) {
      weeks.push({ days: currentWeek });
    }

    return weeks;
  });

  currentMonthName = computed(() => {
    return this.currentDate().toLocaleString('default', { month: 'long' });
  });

  currentYear = computed(() => {
    return this.currentDate().getFullYear();
  });

  private isToday(date: Date): boolean {
    const today = new Date();
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  }

  previousMonth() {
    const date = new Date(this.currentDate());
    date.setMonth(date.getMonth() - 1);
    this.currentDate.set(date);
  }

  nextMonth() {
    const date = new Date(this.currentDate());
    date.setMonth(date.getMonth() + 1);
    this.currentDate.set(date);
  }

  goToToday() {
    this.currentDate.set(new Date());
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

  onDayClick(day: CalendarDay) {
    // Could open a quick event creation dialog pre-filled with the selected date
  }

  onEventClick(event: MouseEvent, calEvent: Event) {
    event.stopPropagation();
    const dialogRef = this.dialog.open(EventDialogComponent, {
      width: '500px',
      data: { event: calEvent },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.calendarStore.updateEvent(calEvent.id, result);
      }
    });
  }
}
