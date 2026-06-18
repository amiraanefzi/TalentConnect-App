// ============================================================
// services/auth.service.ts
// Gère login, logout, état de l'utilisateur connecté
// À copier dans src/app/core/services/
// ============================================================

import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap, map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import {
  ApiResponse,
  AuthRequest,
  AuthResponse,
  UserDto,
  UserRole
} from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';
const TOKEN_KEY = 'tc_token';
const USER_KEY  = 'tc_user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  // ── Signals réactifs ────────────────────────────────────────────────
  private readonly _user  = signal<UserDto | null>(this.loadUser());
  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly currentUser  = this._user.asReadonly();
  readonly isLoggedIn   = computed(() => !!this._token());
  readonly currentRole  = computed(() => this._user()?.role ?? null);
  readonly isAdmin      = computed(() => this._user()?.role === 'ADMIN');
  readonly isHr         = computed(() => this._user()?.role === 'HR' || this.isAdmin());
  readonly isEmployee   = computed(() => this._user()?.role === 'EMPLOYEE');

  constructor(private http: HttpClient, private router: Router) {}

  // ── Login ─────────────────────────────────────────────────────────

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<ApiResponse<AuthResponse>>(`${API}/auth/login`, credentials).pipe(
      map(res => res.data),
      tap(auth => {
        localStorage.setItem(TOKEN_KEY, auth.token);
        localStorage.setItem(USER_KEY,  JSON.stringify(auth.user));
        this._token.set(auth.token);
        this._user.set(auth.user);
      })
    );
  }

  // ── Logout ────────────────────────────────────────────────────────

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._token.set(null);
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  // ── Token ─────────────────────────────────────────────────────────

  getToken(): string | null {
    return this._token();
  }

  // ── Helpers ───────────────────────────────────────────────────────

  hasRole(role: UserRole): boolean {
    return this._user()?.role === role;
  }

  private loadUser(): UserDto | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}

