import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthStore } from '../../../core/stores/auth.store';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatToolbarModule,
    MatMenuModule,
    MatIconModule,
    RouterLink,
    RouterLinkActive,
  ],
  template: `
    <mat-toolbar color="primary" class="shadow-lg">
      <div class="container mx-auto px-4 flex justify-between items-center">
        <div class="flex items-center gap-8">
          <a routerLink="/calendar" class="text-xl font-bold hover:opacity-80 transition">
            SpringSuite
          </a>
          <nav class="hidden md:flex gap-4">
            <a
              routerLink="/calendar"
              routerLinkActive="bg-white/20"
              class="px-3 py-2 rounded hover:bg-white/10 transition"
            >
              Calendar
            </a>
            <a
              routerLink="/messaging"
              routerLinkActive="bg-white/20"
              class="px-3 py-2 rounded hover:bg-white/10 transition opacity-50 cursor-not-allowed"
              title="Coming soon"
            >
              Messaging
            </a>
            <a
              routerLink="/docs"
              routerLinkActive="bg-white/20"
              class="px-3 py-2 rounded hover:bg-white/10 transition opacity-50 cursor-not-allowed"
              title="Coming soon"
            >
              Docs
            </a>
          </nav>
        </div>

        <div class="flex items-center gap-4">
          <span class="hidden md:block text-sm opacity-80">
            {{ authStore.user()?.username }}
          </span>
          <button mat-icon-button [matMenuTriggerFor]="menu">
            <mat-icon>account_circle</mat-icon>
          </button>
          <mat-menu #menu="matMenu">
            <button mat-menu-item (click)="logout()">
              <mat-icon>logout</mat-icon>
              <span>Logout</span>
            </button>
          </mat-menu>
        </div>
      </div>
    </mat-toolbar>
  `,
  styles: [
    `
      :host {
        display: block;
        position: sticky;
        top: 0;
        z-index: 100;
      }
    `,
  ],
})
export class HeaderComponent {
  protected authStore = inject(AuthStore);
  private router = inject(Router);

  logout() {
    this.authStore.logout();
  }
}
