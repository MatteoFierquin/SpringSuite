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
  selector: 'app-login',
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
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-100 via-purple-50 to-pink-100">
      <div class="w-full max-w-md p-8 bg-white rounded-xl shadow-2xl">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-gray-800">SpringSuite</h1>
          <p class="text-gray-600 mt-2">Sign in to your account</p>
        </div>

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="space-y-6">
          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Username</mat-label>
            <input 
              matInput 
              formControlName="username" 
              placeholder="Enter your username"
              autocomplete="username"
            />
            @if (f['username'].invalid && f['username'].touched) {
              <mat-error>Username is required</mat-error>
            }
          </mat-form-field>

          <mat-form-field appearance="outline" class="w-full">
            <mat-label>Password</mat-label>
            <input 
              matInput 
              type="password" 
              formControlName="password"
              placeholder="Enter your password"
              autocomplete="current-password"
            />
            @if (f['password'].invalid && f['password'].touched) {
              <mat-error>Password is required</mat-error>
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
            [disabled]="loginForm.invalid || authStore.loading()"
            class="w-full py-3 text-lg"
          >
            @if (authStore.loading()) {
              <span class="flex items-center justify-center gap-2">
                <mat-progress-spinner diameter="20"></mat-progress-spinner>
                Signing in...
              </span>
            } @else {
              Sign In
            }
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-gray-600">
            Don't have an account?
            <a routerLink="/register" class="text-indigo-600 hover:text-indigo-800 font-semibold">
              Sign up
            </a>
          </p>
        </div>

        <div class="mt-4 p-4 bg-gray-50 rounded-lg">
          <p class="text-sm text-gray-600 font-semibold mb-2">Demo credentials:</p>
          <p class="text-sm text-gray-500">Username: <code class="bg-gray-200 px-2 py-1 rounded">admin</code></p>
          <p class="text-sm text-gray-500">Password: <code class="bg-gray-200 px-2 py-1 rounded">admin123</code></p>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  protected authStore = inject(AuthStore);
  private router = inject(Router);

  loginForm = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.authStore.login(
        this.f['username'].value,
        this.f['password'].value
      );
    }
  }
}
