// ============================================================
// models/talentconnect.models.ts
// Interfaces TypeScript miroir des DTOs Spring Boot
// À copier dans src/app/core/models/
// ============================================================

// ── Enums ────────────────────────────────────────────────────────────

export type UserRole = 'EMPLOYEE' | 'HR' | 'ADMIN';

export type EmploymentType = 'CDI' | 'CDD' | 'STAGE' | 'FREELANCE';
export type Seniority      = 'JUNIOR' | 'CONFIRME' | 'SENIOR' | 'LEAD';
export type JobStatus      = 'DRAFT' | 'OPEN' | 'CLOSED';

export type AppSource = 'INTERNAL' | 'REFERRAL';
export type AppStatus  = 'SUBMITTED' | 'REVIEW' | 'INTERVIEW' | 'OFFER' | 'HIRED' | 'REJECTED';

export type ReferralStatus = 'SUBMITTED' | 'REVIEW' | 'INTERVIEW' | 'OFFER' | 'HIRED' | 'REJECTED' | 'DRAFT';

export type NotifType  = 'SUCCESS' | 'INFO' | 'WARNING' | 'ERROR';
export type ScanStatus = 'PENDING' | 'SAFE' | 'FAILED';
export type EntityType = 'JOB' | 'APPLICATION' | 'REFERRAL' | 'DOCUMENT';

// ── Enveloppe API universelle ─────────────────────────────────────────

export interface ApiResponse<T> {
  data:      T;
  timestamp: string;   // ISO string
  status:    number;
}

export interface ApiError {
  error:     string;
  status:    number;
  timestamp: string;
}

// ── Pagination ────────────────────────────────────────────────────────

export interface PageDto<T> {
  content:       T[];
  page:          number;
  size:          number;
  totalElements: number;
  totalPages:    number;
}

// ── Auth ──────────────────────────────────────────────────────────────

export interface AuthRequest {
  email:    string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user:  UserDto;
}

// ── User ──────────────────────────────────────────────────────────────

export interface UserDto {
  id:              number;
  employeeId:      string;
  firstName:       string;
  lastName:        string;
  email:           string;
  role:            UserRole;
  department:      string;
  location:        string;
  title:           string;
  skills:          string[];
  experienceYears: number;
  avatarUrl:       string | null;
  languages:       string[];
}

// ── JobOffer ──────────────────────────────────────────────────────────

export interface JobOfferDto {
  id?:               number;
  title:             string;
  department:        string;
  location:          string;
  description:       string;
  employmentType:    EmploymentType;
  seniority:         Seniority;
  status:            JobStatus;
  requirements:      string[];
  tags:              string[];
  publishedAt?:      string;   // LocalDateTime → ISO string
  closingAt?:        string;
  hiringManager?:    string;
  recommendedScore?: number;
  createdAt?:        string;
  updatedAt?:        string;
}

// ── Application ───────────────────────────────────────────────────────

export interface TimelineEntryDto {
  id:          number;
  title:       string;
  description: string;
  author:      string;
  timestamp:   string;
}

export interface DocumentDto {
  id:          number;
  fileName:    string;
  mimeType:    string;
  size:        number;
  previewUrl:  string;
  downloadUrl: string;
  scanStatus:  ScanStatus;
  uploadedAt:  string;
}

export interface ApplicationDto {
  id:            number;
  jobId:         number;
  jobTitle:      string;          // calculé côté backend
  employeeId:    number | null;
  candidateName: string | null;
  source:        AppSource;
  status:        AppStatus;
  score:         number;
  notes:         string | null;
  createdAt:     string;
  updatedAt:     string;
  timeline:      TimelineEntryDto[];
  documents:     DocumentDto[];
}

// ── Referral ──────────────────────────────────────────────────────────

export interface ReferralDto {
  id?:               number;
  referrerEmployeeId?: number;
  referrerName?:     string;
  candidateFullName: string;
  candidateEmail:    string;
  candidatePhone?:   string;
  linkedIn?:         string;
  targetJobId?:      number;
  targetJobTitle?:   string;
  skills:            string[];
  cvDocumentId?:     string;
  status:            ReferralStatus;
  createdAt?:        string;
}

// ── Notification ──────────────────────────────────────────────────────

export interface NotificationDto {
  id:        number;
  userId:    number;
  type:      NotifType;
  title:     string;
  message:   string;
  read:      boolean;
  deepLink:  string | null;
  createdAt: string;
}

// ── Audit ─────────────────────────────────────────────────────────────

export interface AuditEventDto {
  id:         number;
  actor:      string;
  actorRole:  string;
  action:     string;
  entityType: EntityType;
  entityId:   string;
  details:    string;
  timestamp:  string;
}

// ── HR Metrics ────────────────────────────────────────────────────────

export interface HrMetrics {
  totalApplications:  number;
  internalCandidates: number;
  referrals:          number;
  avgTimeToHire:      number;
  conversionRate:     number;
}

