import { UserLite } from './User';

export interface FreeDay extends FreeDayLite {
  user: UserLite | null;
}

interface FreeDayLite {
  id: number;
  date: Date | null;
}
