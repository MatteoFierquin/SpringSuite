import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-event-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
  ],
  template: `
    <div class="p-6 min-w-[400px]">
      <h2 class="text-2xl font-bold mb-6 text-gray-800">Create New Event</h2>

      <form [formGroup]="eventForm" (ngSubmit)="onSubmit()" class="space-y-4">
        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Title</mat-label>
          <input matInput formControlName="title" placeholder="Event title" />
          @if (f['title'].invalid && f['title'].touched) {
            <mat-error>Title is required</mat-error>
          }
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="3" placeholder="Event description"></textarea>
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Location</mat-label>
          <input matInput formControlName="location" placeholder="Event location" />
        </mat-form-field>

        <div class="grid grid-cols-2 gap-4">
          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Start Date</mat-label>
            <input matInput [matDatepicker]="startPicker" formControlName="startDate" />
            <mat-datepicker-toggle matSuffix [for]="startPicker"></mat-datepicker-toggle>
            <mat-datepicker #startPicker></mat-datepicker>
            @if (f['startDate'].invalid && f['startDate'].touched) {
              <mat-error>Start date is required</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Start Time</mat-label>
            <input matInput type="time" formControlName="startTime" />
            @if (f['startTime'].invalid && f['startTime'].touched) {
              <mat-error>Start time is required</mat-error>
            }
          </mat-form-field>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <mat-form-field appearance="outline" class="w-full">
            <mat-label>End Date</mat-label>
            <input matInput [matDatepicker]="endPicker" formControlName="endDate" />
            <mat-datepicker-toggle matSuffix [for]="endPicker"></mat-datepicker-toggle>
            <mat-datepicker #endPicker></mat-datepicker>
            @if (f['endDate'].invalid && f['endDate'].touched) {
              <mat-error>End date is required</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="w-full">
            <mat-label>End Time</mat-label>
            <input matInput type="time" formControlName="endTime" />
            @if (f['endTime'].invalid && f['endTime'].touched) {
              <mat-error>End time is required</mat-error>
            }
          </mat-form-field>
        </div>

        @if (errorMessage) {
          <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {{ errorMessage }}
          </div>
        }

        <div class="flex justify-end gap-3 pt-4">
          <button mat-button type="button" (click)="dialogRef.close()">Cancel</button>
          <button mat-raised-button color="primary" type="submit" [disabled]="eventForm.invalid || isSubmitting">
            @if (isSubmitting) {
              <span class="flex items-center gap-2">
                <mat-progress-spinner diameter="16"></mat-progress-spinner>
                Creating...
              </span>
            } @else {
              Create Event
            }
          </button>
        </div>
      </form>
    </div>
  `,
})
export class EventDialogComponent {
  private fb = inject(FormBuilder);
  protected dialogRef = inject(MatDialogRef<EventDialogComponent>);

  isSubmitting = false;
  errorMessage = '';

  eventForm = this.fb.nonNullable.group({
    title: ['', Validators.required],
    description: [''],
    location: [''],
    startDate: [new Date(), Validators.required],
    startTime: ['09:00', Validators.required],
    endDate: [new Date(), Validators.required],
    endTime: ['10:00', Validators.required],
  });

  get f() {
    return this.eventForm.controls;
  }

  onSubmit() {
    if (this.eventForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const formValue = this.eventForm.getRawValue();
    
    // Combine date and time
    const startTime = this.combineDateTime(formValue.startDate, formValue.startTime);
    const endTime = this.combineDateTime(formValue.endDate, formValue.endTime);

    this.dialogRef.close({
      title: formValue.title,
      description: formValue.description || '',
      location: formValue.location || '',
      startTime: startTime.toISOString(),
      endTime: endTime.toISOString(),
    });
  }

  private combineDateTime(date: Date, time: string): Date {
    const [hours, minutes] = time.split(':').map(Number);
    const result = new Date(date);
    result.setHours(hours, minutes, 0, 0);
    return result;
  }
}
