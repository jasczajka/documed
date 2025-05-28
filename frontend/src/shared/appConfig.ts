export interface AppConfig {
  portalRootId: string;
  dateTimeFormat: string;
  snackBarDuration: number;
  timeSlotLengthInMinutes: number;
  maxAdditionalInfoVisitLength: number;
  maxTextFieldLength: number;
}

export const appConfig: AppConfig = {
  portalRootId: 'portal-root',
  dateTimeFormat: "yyyy-MM-dd'T'HH:mm",
  snackBarDuration: 10000,
  timeSlotLengthInMinutes: 15,
  maxAdditionalInfoVisitLength: 255,
  maxTextFieldLength: 5000,
};
