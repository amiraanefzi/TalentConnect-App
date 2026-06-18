// ============================================================
// services/user.service.ts
// À copier dans src/app/core/services/
// ============================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse, PageDto, UserDto } from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  /** GET /api/users (ADMIN) */
  getAll(page = 0, size = 20): Observable<PageDto<UserDto>> {
    return this.http
      .get<ApiResponse<PageDto<UserDto>>>(`${API}/users?page=${page}&size=${size}`)
      .pipe(map(r => r.data));
  }

  /** GET /api/users/profile */
  getProfile(): Observable<UserDto> {
    return this.http
      .get<ApiResponse<UserDto>>(`${API}/users/profile`)
      .pipe(map(r => r.data));
  }

  /** GET /api/users/:id */
  getById(id: number): Observable<UserDto> {
    return this.http
      .get<ApiResponse<UserDto>>(`${API}/users/${id}`)
      .pipe(map(r => r.data));
  }

  /** PUT /api/users/profile */
  updateProfile(body: Partial<UserDto>): Observable<UserDto> {
    return this.http
      .put<ApiResponse<UserDto>>(`${API}/users/profile`, body)
      .pipe(map(r => r.data));
  }

  /** POST /api/users (ADMIN) */
  create(body: Partial<UserDto> & { password: string }): Observable<UserDto> {
    return this.http
      .post<ApiResponse<UserDto>>(`${API}/users`, body)
      .pipe(map(r => r.data));
  }

  /** DELETE /api/users/:id (ADMIN) */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/users/${id}`);
  }
}

