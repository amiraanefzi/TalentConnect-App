// ============================================================
// services/notification.service.ts
// À copier dans src/app/core/services/
// ============================================================

import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse, NotificationDto } from '../models/talentconnect.models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly _notifications = signal<NotificationDto[]>([]);

  readonly notifications  = this._notifications.asReadonly();
  readonly unreadCount    = computed(() =>
    this._notifications().filter(n => !n.read).length
  );

  constructor(private http: HttpClient) {}

  /** GET /api/notifications — charge et met à jour le signal */
  load(): Observable<NotificationDto[]> {
    return this.http
      .get<ApiResponse<NotificationDto[]>>(`${API}/notifications`)
      .pipe(
        map(r => r.data),
        tap(list => this._notifications.set(list))
      );
  }

  /** PATCH /api/notifications/:id/read */
  markRead(id: number): Observable<NotificationDto> {
    return this.http
      .patch<ApiResponse<NotificationDto>>(`${API}/notifications/${id}/read`, {})
      .pipe(
        map(r => r.data),
        tap(updated => this._notifications.update(list =>
          list.map(n => n.id === id ? updated : n)
        ))
      );
  }

  /** PATCH /api/notifications/read-all */
  markAllRead(): Observable<void> {
    return this.http
      .patch<ApiResponse<void>>(`${API}/notifications/read-all`, {})
      .pipe(
        map(() => void 0),
        tap(() => this._notifications.update(list =>
          list.map(n => ({ ...n, read: true }))
        ))
      );
  }

  /** DELETE /api/notifications/:id */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/notifications/${id}`).pipe(
      tap(() => this._notifications.update(list => list.filter(n => n.id !== id)))
    );
  }
}

