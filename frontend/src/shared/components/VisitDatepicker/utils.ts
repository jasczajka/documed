import { format } from 'date-fns';
import { appConfig } from 'shared/appConfig';

export const groupPossibleStartTimesByDay = (
  possibleStartTimes: Date[],
): Record<string, Date[]> => {
  const groupedByDay: Record<string, Date[]> = possibleStartTimes.reduce(
    (acc, date) => {
      const key = format(date, appConfig.localDateFormat);
      if (!acc[key]) acc[key] = [];
      acc[key].push(date);
      return acc;
    },
    {} as Record<string, Date[]>,
  );
  return groupedByDay;
};
