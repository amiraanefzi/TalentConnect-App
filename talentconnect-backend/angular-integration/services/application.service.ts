// ============================================================
// services/application.service.ts
// À copier dans src/app/core/services/
// ============================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse, ApplicationDto, PageDto } from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  constructor(private http: HttpClient) {}

  /** GET /api/applications (HR/ADMIN) */
  getAll(page = 0, size = 20): Observable<PageDto<ApplicationDto>> {
    return this.http
      .get<ApiResponse<PageDto<ApplicationDto>>>(`${API}/applications?page=${page}&size=${size}`)
      .pipe(map(r => r.data));
  }

  /** GET /api/applications/mine (EMPLOYEE) */
  getMine(page = 0, size = 20): Observable<PageDto<ApplicationDto>> {
    return this.http
      .get<ApiResponse<PageDto<ApplicationDto>>>(`${API}/applications/mine?page=${page}&size=${size}`)
      .pipe(map(r => r.data));
  }

  /** GET /api/applications/:id */
  getById(id: number): Observable<ApplicationDto> {
    return this.http
      .get<ApiResponse<ApplicationDto>>(`${API}/applications/${id}`)
      .pipe(map(r => r.data));
  }

  /** POST /api/applications — EMPLOYEE postuler */
  apply(jobId: number, source: 'INTERNAL' | 'REFERRAL' = 'INTERNAL'): Observable<ApplicationDto> {
    return this.http
      .post<ApiResponse<ApplicationDto>>(`${API}/applications`, { jobId, source })
      .pipe(map(r => r.data));
  }

  /** PATCH /api/applications/:id/status — HR/ADMIN */
  changeStatus(id: number, status: string): Observable<ApplicationDto> {
    return this.http
      .patch<ApiResponse<ApplicationDto>>(`${API}/applications/${id}/status`, { status })
      .pipe(map(r => r.data));
  }

  /** DELETE /api/applications/:id — EMPLOYEE retrait */
  withdraw(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/applications/${id}`);
  }
}

