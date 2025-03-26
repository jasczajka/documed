import { UserLite } from './User';

export interface WorkTime extends WorkTimeLite {
  user: UserLite;
}

export interface WorkTimeLite {
  id: number;
  dayOfWeek: number;
  startTime: Date | null;
  endTime: Date | null;
}
