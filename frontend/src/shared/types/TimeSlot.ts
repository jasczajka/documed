import { UserLite } from './User';

export interface TimeSlot extends TimeSlotLite {
  doctor: UserLite;
}

export interface TimeSlotLite {
  id: number;
  startTime: string;
  endTime: string;
  date: Date;
  isBusy: boolean;
}
