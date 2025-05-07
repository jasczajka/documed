import { WorkTimeDayOfWeek } from 'shared/api/generated/generated.schemas';
import { WorkTimeWithoutIdAndUser } from 'src/pages/SingleSpecialistPage';
import { TimePair, WorkTimeFormValues } from './tabs/EditWorkTimeTab';

export function mapToWorkTimes(formValues: WorkTimeFormValues): WorkTimeWithoutIdAndUser[] {
  return Object.entries(formValues.workTimes)
    .filter(([, value]) => value.startTime && value.endTime)
    .map(([dayOfWeekStr, value]) => {
      const dayOfWeek = WorkTimeDayOfWeek[dayOfWeekStr as keyof typeof WorkTimeDayOfWeek];
      return {
        dayOfWeek,
        startTime: value.startTime,
        endTime: value.endTime,
      };
    });
}

export function mapFromWorkTimes(workTimes: WorkTimeWithoutIdAndUser[]): WorkTimeFormValues {
  return {
    workTimes: Object.values(WorkTimeDayOfWeek).reduce(
      (acc, day) => {
        const found = workTimes.find((wt) => wt.dayOfWeek === day);
        acc[day] = {
          startTime: found?.startTime || '',
          endTime: found?.endTime || '',
        };
        return acc;
      },
      {} as Record<WorkTimeDayOfWeek, TimePair>,
    ),
  };
}

export function isValid15MinuteTime(time?: string) {
  if (!time) return true;
  const minutes = parseInt(time.split(':')[1], 10);
  return minutes % 15 === 0;
}

export const dayOfWeekToInt: Record<WorkTimeDayOfWeek, number> = {
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
  SATURDAY: 6,
  SUNDAY: 7,
};
