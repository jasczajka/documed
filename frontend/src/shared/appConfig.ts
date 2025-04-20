export interface AppConfig {
  portalRootId: string;
  dateTimeFormat: string;
  snackBarDuration: number;
}

export const appConfig: AppConfig = {
  portalRootId: 'portal-root',
  dateTimeFormat: "yyyy-MM-dd'T'HH:mm",
  snackBarDuration: 10000,
};
