import { format } from 'date-fns';

export const groupPossibleStartTimesByDay = (
  possibleStartTimes: Date[],
): Record<string, Date[]> => {
  const groupedByDay: Record<string, Date[]> = possibleStartTimes.reduce(
    (acc, date) => {
      const key = format(date, 'yyyy-MM-dd');
      if (!acc[key]) acc[key] = [];
      acc[key].push(date);
      return acc;
    },
    {} as Record<string, Date[]>,
  );
  return groupedByDay;
};

export const generatePossibleStartTimes = (): Date[] => {
  const result: Date[] = [];
  const daysToGenerate = 6;
  const slotsPerDay = 20;
  const now = new Date();
  const oneMonthLater = new Date(now);
  oneMonthLater.setMonth(now.getMonth() + 1);

  function getRandomDay(): Date {
    const start = now.getTime();
    const end = oneMonthLater.getTime();
    const date = new Date(start + Math.random() * (end - start));
    date.setHours(0, 0, 0, 0); // Reset time
    return date;
  }

  function shuffle<T>(array: T[]): T[] {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
  }

  const uniqueDays = new Set<number>();
  while (uniqueDays.size < daysToGenerate) {
    const day = getRandomDay().getTime();
    uniqueDays.add(day);
  }

  for (const dayTimestamp of uniqueDays) {
    const baseDate = new Date(dayTimestamp);

    const availableSlots: Date[] = [];
    for (let hour = 8; hour < 20; hour++) {
      for (const minute of [0, 30]) {
        const slot = new Date(baseDate);
        slot.setHours(hour, minute, 0, 0);
        availableSlots.push(slot);
      }
    }

    const uniqueSlots = shuffle(availableSlots).slice(0, slotsPerDay);
    result.push(...uniqueSlots);
  }

  return result;
};
