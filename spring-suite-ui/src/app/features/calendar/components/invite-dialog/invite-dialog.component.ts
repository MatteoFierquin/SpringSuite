import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { EventInvitee } from '../../../../models/event';

@Component({
  selector: 'app-invite-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
  ],
  template: `
    <div class="p-6 min-w-[450px]">
      <h2 class="text-2xl font-bold mb-2 text-gray-800">Manage Invitations</h2>
      <p class="text-gray-600 mb-6 text-sm">Invite people to this event by adding their email addresses</p>

      <form [formGroup]="inviteForm" (ngSubmit)="onAddInvitee()" class="mb-6">
        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Email address</mat-label>
          <input
            matInput
            formControlName="email"
            type="email"
            placeholder="person@example.com"
            (keydown.enter)="onAddInvitee()"
          />
          <button mat-icon-button matSuffix type="submit" [disabled]="inviteForm.invalid || isSubmitting">
            <mat-icon>add</mat-icon>
          </button>
          @if (inviteForm.get('email')?.hasError('email')) {
            <mat-error>Please enter a valid email address</mat-error>
          }
        </mat-form-field>
      </form>

      @if (invitees.length > 0) {
        <div class="space-y-2">
          <h3 class="text-sm font-semibold text-gray-700">Current Invitations</h3>
          <div class="flex flex-wrap gap-2">
            @for (invitee of invitees; track invitee.email) {
              <mat-chip-set>
                <mat-chip
                  class="flex items-center gap-1"
                  [color]="getStatusColor(invitee.status)"
                  (removed)="onRemoveInvitee(invitee.email)"
                >
                  <span>{{ invitee.email }}</span>
                  <span class="text-xs opacity-75 ml-1">({{ invitee.status }})</span>
                  <button matChipRemove type="button" (click)="onRemoveInvitee(invitee.email)">
                    <mat-icon>cancel</mat-icon>
                  </button>
                </mat-chip>
              </mat-chip-set>
            }
          </div>
        </div>
      } @else {
        <div class="text-center py-8 text-gray-500">
          <mat-icon class="text-4xl text-gray-300 mb-2">mail_outline</mat-icon>
          <p>No invitations yet</p>
        </div>
      }

      @if (errorMessage) {
        <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mt-4">
          {{ errorMessage }}
        </div>
      }

      <div class="flex justify-end gap-3 pt-6 mt-4 border-t">
        <button mat-button type="button" (click)="dialogRef.close()">Cancel</button>
        <button mat-raised-button color="primary" type="button" (click)="onSave()" [disabled]="isSubmitting">
          @if (isSubmitting) {
            <span class="flex items-center gap-2">
              <mat-progress-spinner diameter="16"></mat-progress-spinner>
              Saving...
            </span>
          } @else {
            Save Changes
          }
        </button>
      </div>
    </div>
  `,
})
export class InviteDialogComponent {
  private fb = inject(FormBuilder);
  protected dialogRef = inject(MatDialogRef<InviteDialogComponent>);
  protected data = inject(MAT_DIALOG_DATA);

  invitees: EventInvitee[] = [];
  isSubmitting = false;
  errorMessage = '';
  modifiedInvitees: EventInvitee[] = [];

  inviteForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
  });

  ngOnInit() {
    this.invitees = this.data?.invitees || [];
    this.modifiedInvitees = [...this.invitees];
  }

  get f() {
    return this.inviteForm.controls;
  }

  onAddInvitee() {
    if (this.inviteForm.invalid) {
      return;
    }

    const email = this.f.email.value.toLowerCase().trim();

    if (this.modifiedInvitees.some((i) => i.email === email)) {
      this.errorMessage = 'This email is already invited';
      return;
    }

    this.modifiedInvitees.push({ email, status: 'PENDING' });
    this.inviteForm.reset();
    this.errorMessage = '';
  }

  onRemoveInvitee(email: string) {
    this.modifiedInvitees = this.modifiedInvitees.filter((i) => i.email !== email);
  }

  onSave() {
    this.isSubmitting = true;
    this.dialogRef.close(this.modifiedInvitees);
  }

  getStatusColor(status: string): 'primary' | 'accent' | 'warn' {
    switch (status) {
      case 'ACCEPTED':
        return 'accent';
      case 'DECLINED':
        return 'warn';
      default:
        return 'primary';
    }
  }
}
