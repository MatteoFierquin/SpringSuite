import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { Event } from '../../../../models/event';

@Component({
  selector: 'app-event-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatChipsModule],
  template: `
    <mat-card class="hover:shadow-xl transition-shadow h-full">
      <mat-card-header class="p-4 pb-2">
        <mat-card-title class="text-lg font-semibold line-clamp-1">
          {{ event.title }}
        </mat-card-title>
        <mat-card-subtitle class="text-sm">
          <div class="flex items-center gap-1">
            <mat-icon class="text-xs">calendar_today</mat-icon>
            {{ event.startTime | date: 'EEE, MMM d, y' }}
          </div>
          <div class="flex items-center gap-1 mt-1">
            <mat-icon class="text-xs">schedule</mat-icon>
            {{ event.startTime | date: 'h:mm a' }} - {{ event.endTime | date: 'h:mm a' }}
          </div>
        </mat-card-subtitle>
      </mat-card-header>

      @if (event.description) {
        <mat-card-content class="p-4 pt-2">
          <p class="text-gray-600 text-sm line-clamp-2">{{ event.description }}</p>
        </mat-card-content>
      }

      @if (event.location || event.attendees.length || event.invitees.length) {
        <mat-card-content class="p-4 pt-0">
          @if (event.location) {
            <div class="flex items-center gap-1 text-sm text-gray-500 mb-2">
              <mat-icon class="text-xs">location_on</mat-icon>
              {{ event.location }}
            </div>
          }
          @if (event.attendees.length) {
            <div class="flex items-center gap-1 text-sm text-gray-500 mb-2">
              <mat-icon class="text-xs">people</mat-icon>
              {{ event.attendees.length }} attendee(s)
            </div>
          }
          @if (event.invitees.length) {
            <div class="flex flex-wrap gap-1 mt-2">
              @for (invitee of event.invitees.slice(0, 5); track invitee.email) {
                <span
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs"
                  [class.bg-blue-100]="invitee.status === 'PENDING'"
                  [class.bg-green-100]="invitee.status === 'ACCEPTED'"
                  [class.bg-red-100]="invitee.status === 'DECLINED'"
                  [class.text-blue-700]="invitee.status === 'PENDING'"
                  [class.text-green-700]="invitee.status === 'ACCEPTED'"
                  [class.text-red-700]="invitee.status === 'DECLINED'"
                >
                  {{ invitee.email }}
                </span>
              }
              @if (event.invitees.length > 5) {
                <span class="text-xs text-gray-500">+{{ event.invitees.length - 5 }} more</span>
              }
            </div>
          }
        </mat-card-content>
      }

      <mat-card-actions class="p-4 pt-0">
        <button mat-button color="warn" (click)="deleteClick.emit(event.id)">
          <mat-icon>delete</mat-icon>
          Delete
        </button>
      </mat-card-actions>
    </mat-card>
  `,
  host: {
    class: 'block',
  },
})
export class EventCardComponent {
  @Input({ required: true }) event!: Event;
  @Output() deleteClick = new EventEmitter<number>();
}
