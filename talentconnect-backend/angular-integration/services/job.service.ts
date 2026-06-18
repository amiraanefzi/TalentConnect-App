// ============================================================
// services/job.service.ts
// À copier dans src/app/core/services/
// ============================================================

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse, JobOfferDto, PageDto } from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';

export interface JobSearchParams {
  q?:          string;
  location?:   string;
  department?: string;
  status?:     string;
  page?:       number;
  size?:       number;
}

@Injectable({ providedIn: 'root' })
export class JobService {
  constructor(private http: HttpClient) {}

  /** GET /api/jobs */
  search(params: JobSearchParams = {}): Observable<PageDto<JobOfferDto>> {
    let p = new HttpParams();
    if (params.q)          p = p.set('q',          params.q);
    if (params.location)   p = p.set('location',   params.location);
    if (params.department) p = p.set('department', params.department);
    if (params.status)     p = p.set('status',     params.status);
    p = p.set('page', params.page ?? 0);
    p = p.set('size', params.size ?? 20);

    return this.http
      .get<ApiResponse<PageDto<JobOfferDto>>>(`${API}/jobs`, { params: p })
      .pipe(map(r => r.data));
  }

  /** GET /api/jobs/:id */
  getById(id: number): Observable<JobOfferDto> {
    return this.http
      .get<ApiResponse<JobOfferDto>>(`${API}/jobs/${id}`)
      .pipe(map(r => r.data));
  }

  /** POST /api/jobs (HR/ADMIN) */
  create(dto: Partial<JobOfferDto>): Observable<JobOfferDto> {
    return this.http
      .post<ApiResponse<JobOfferDto>>(`${API}/jobs`, dto)
      .pipe(map(r => r.data));
  }

  /** PUT /api/jobs/:id (HR/ADMIN) */
  update(id: number, dto: Partial<JobOfferDto>): Observable<JobOfferDto> {
    return this.http
      .put<ApiResponse<JobOfferDto>>(`${API}/jobs/${id}`, dto)
      .pipe(map(r => r.data));
  }

  /** PATCH /api/jobs/:id/status (HR/ADMIN) */
  changeStatus(id: number, status: string): Observable<JobOfferDto> {
    return this.http
      .patch<ApiResponse<JobOfferDto>>(`${API}/jobs/${id}/status`, { status })
      .pipe(map(r => r.data));
  }

  /** DELETE /api/jobs/:id (ADMIN) */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/jobs/${id}`);
  }
}

