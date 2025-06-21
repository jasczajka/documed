import {
  DayOfWeekEnum,
  UploadWorkTimeDTO,
  WorkTimeReturnDTO,
} from 'shared/api/generated/generated.schemas';
import { TimePairWithFacilityId, WorkTimeFormValues } from './tabs/EditWorkTimeTab';

export const mapToWorkTimes = (formValues: WorkTimeFormValues): UploadWorkTimeDTO[] => {
  return Object.entries(formValues.workTimes)
    .filter(([, value]) => value.startTime && value.endTime && value.facilityId)
    .map(([dayOfWeekStr, value]) => {
      const dayOfWeek = DayOfWeekEnum[dayOfWeekStr as keyof typeof DayOfWeekEnum];
      return {
        dayOfWeek,
        startTime: value.startTime,
        endTime: value.endTime,
        facilityId: value.facilityId,
      };
    });
};

export const mapFromWorkTimes = (workTimes: UploadWorkTimeDTO[]): WorkTimeFormValues => {
  return {
    workTimes: Object.values(DayOfWeekEnum).reduce(
      (acc, day) => {
        const found = workTimes.find((wt) => wt.dayOfWeek === day);
        acc[day] = {
          startTime: found?.startTime || '',
          endTime: found?.endTime || '',
          facilityId: found?.facilityId || -1,
        };
        return acc;
      },
      {} as Record<DayOfWeekEnum, TimePairWithFacilityId>,
    ),
  };
};

export const mapFromReturnWorkTimes = (workTimes: WorkTimeReturnDTO[]): UploadWorkTimeDTO[] => {
  return workTimes.map((wt) => ({
    dayOfWeek: wt.dayOfWeek,
    startTime: wt.startTime,
    endTime: wt.endTime,
    facilityId: wt.facilityId,
  }));
};

export const isValid15MinuteTime = (time?: string) => {
  if (!time) return true;
  const minutes = parseInt(time.split(':')[1], 10);
  return minutes % 15 === 0;
};

export const dayOfWeekToInt: Record<DayOfWeekEnum, number> = {
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
  SATURDAY: 6,
  SUNDAY: 7,
};
