import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthStore } from '../../../../core/stores/auth.store';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCardModule,
    MatProgressSpinnerModule,
    RouterLink,
  ],
  template: `
    <div
      class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-100 via-purple-50 to-pink-100"
    >
      <div class="w-full max-w-md p-8 bg-white rounded-xl shadow-2xl">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-gray-800">Create Account</h1>
          <p class="text-gray-600 mt-2">Join SpringSuite today</p>
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="space-y-4">
          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Username</mat-label>
            <input matInput formControlName="username" placeholder="Choose a username" />
            @if (f['username'].invalid && f['username'].touched) {
              <mat-error>Username is required</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Email</mat-label>
            <input matInput formControlName="email" type="email" placeholder="you@example.com" />
            @if (f['email'].invalid && f['email'].touched) {
              <mat-error>Valid email is required</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Password</mat-label>
            <input
              matInput
              type="password"
              formControlName="password"
              placeholder="Create a password"
            />
            @if (f['password'].invalid && f['password'].touched) {
              <mat-error>Password must be at least 6 characters</mat-error>
            }
          </mat-form-field>

          @if (authStore.error()) {
            <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {{ authStore.error() }}
            </div>
          }

          <button
            mat-raised-button
            color="primary"
            type="submit"
            [disabled]="registerForm.invalid || authStore.loading()"
            class="w-full py-3"
          >
            @if (authStore.loading()) {
              <span class="flex items-center justify-center gap-2">
                <mat-progress-spinner diameter="20"></mat-progress-spinner>
                Creating account...
              </span>
            } @else {
              Create Account
            }
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-gray-600">
            Already have an account?
            <a routerLink="/login" class="text-indigo-600 hover:text-indigo-800 font-semibold">
              Sign in
            </a>
          </p>
        </div>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  protected authStore = inject(AuthStore);
  private router = inject(Router);

  registerForm = this.fb.nonNullable.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.authStore.register(
        this.f['username'].value,
        this.f['email'].value,
        this.f['password'].value,
      );
    }
  }
}
