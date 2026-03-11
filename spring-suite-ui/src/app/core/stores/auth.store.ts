import { signalStore, withState, withMethods, withComputed, patchState } from '@ngrx/signals';
import { inject, computed } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { User } from '../../models/user';

interface AuthState {
  user: User | null;
  token: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  token: localStorage.getItem('token'),
  loading: false,
  error: null,
};

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ user, token }) => ({
    isAuthenticated: computed(() => !!user() && !!token()),
  })),
  withMethods((store, authService = inject(AuthService), router = inject(Router)) => ({
    async login(username: string, password: string) {
      patchState(store, { loading: true, error: null });
      try {
        const response = await authService.login({ username, password }).toPromise();
        if (response) {
          localStorage.setItem('token', response.token);
          localStorage.setItem(
            'user',
            JSON.stringify({
              username: response.username,
              role: response.role,
            }),
          );
          patchState(store, {
            user: { username: response.username, email: '', role: response.role as 'USER' | 'ADMIN' },
            token: response.token,
            loading: false,
          });
          router.navigate(['/calendar']);
        }
      } catch (error: any) {
        patchState(store, {
          error: error.error?.message || 'Login failed',
          loading: false,
        });
      }
    },

    async register(username: string, email: string, password: string) {
      patchState(store, { loading: true, error: null });
      try {
        const response = await authService.register({ username, email, password }).toPromise();
        if (response) {
          localStorage.setItem('token', response.token);
          localStorage.setItem(
            'user',
            JSON.stringify({
              username: response.username,
              role: response.role,
            }),
          );
          patchState(store, {
            user: { username: response.username, email, role: response.role as 'USER' | 'ADMIN' },
            token: response.token,
            loading: false,
          });
          router.navigate(['/calendar']);
        }
      } catch (error: any) {
        patchState(store, {
          error: error.error?.message || 'Registration failed',
          loading: false,
        });
      }
    },

    logout() {
      authService.logout();
      patchState(store, { user: null, token: null });
      router.navigate(['/login']);
    },

    clearError() {
      patchState(store, { error: null });
    },
  })),
);
