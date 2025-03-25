export interface TimeSlot {
  id: number;
  doctorId: number;
  startTime: Date;
  endTime: Date;
  date: Date;
  isBusy: boolean;
}
