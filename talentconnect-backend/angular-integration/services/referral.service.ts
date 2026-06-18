// ============================================================
// services/referral.service.ts
// À copier dans src/app/core/services/
// ============================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse, ReferralDto } from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class ReferralService {
  constructor(private http: HttpClient) {}

  /** GET /api/referrals/mine */
  getMine(): Observable<ReferralDto[]> {
    return this.http
      .get<ApiResponse<ReferralDto[]>>(`${API}/referrals/mine`)
      .pipe(map(r => r.data));
  }

  /** POST /api/referrals */
  create(dto: Omit<ReferralDto, 'id' | 'createdAt' | 'referrerEmployeeId' | 'referrerName'>): Observable<ReferralDto> {
    return this.http
      .post<ApiResponse<ReferralDto>>(`${API}/referrals`, dto)
      .pipe(map(r => r.data));
  }

  /** DELETE /api/referrals/:id */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/referrals/${id}`);
  }
}

