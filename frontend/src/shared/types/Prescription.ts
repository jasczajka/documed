export interface Prescription {
  id: number;
  accessCode: number | null;
  description: string | null;
  date: Date | null;
  expirationDate: Date;
  pesel: number | null;
  passportNumber: string | null;
  visitId: number;
}
