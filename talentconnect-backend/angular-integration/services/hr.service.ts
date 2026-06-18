// ============================================================
// services/hr.service.ts
// Audit + Métriques RH
// À copier dans src/app/core/services/
// ============================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse, AuditEventDto, HrMetrics, PageDto } from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class HrService {
  constructor(private http: HttpClient) {}

  /** GET /api/hr/metrics */
  getMetrics(): Observable<HrMetrics> {
    return this.http
      .get<ApiResponse<HrMetrics>>(`${API}/hr/metrics`)
      .pipe(map(r => r.data));
  }

  /** GET /api/audit?page=&size=&sortBy=&dir= */
  getAuditLog(page = 0, size = 20, sortBy = 'timestamp', dir = 'desc'): Observable<PageDto<AuditEventDto>> {
    const params = `page=${page}&size=${size}&sortBy=${sortBy}&dir=${dir}`;
    return this.http
      .get<ApiResponse<PageDto<AuditEventDto>>>(`${API}/audit?${params}`)
      .pipe(map(r => r.data));
  }
}

