export enum VisitStatus {
  PLANNED = 'PLANNED',
  IN_PROGRESS = 'IN_PROGRESS',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED',
}

export enum UserRole {
  PATIENT = 'PATIENT',
  DOCTOR = 'DOCTOR',
  NURSE = 'NURSE',
  WARD_CLERK = 'WARD_CLERK',
  ADMINISTRATOR = 'ADMINISTRATOR',
}

export enum ServiceType {
  REGULAR_SERVICE = 'REGULAR_SERVICE',
  ADDITIONAL_SERVICE = 'ADDITIONAL_SERVICE',
}

export enum ReferralType {
  RTG = 'RTG',
}

export enum NotificationStatus {
  QUEUED = 'QUEUED',
  SENT = 'SENT',
  ERROR = 'ERROR',
}
