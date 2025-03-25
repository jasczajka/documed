export interface WorkTime {
  id: number;
  userId: number;
  dayOfWeek: number;
  startTime: Date | null;
  endTime: Date | null;
}
