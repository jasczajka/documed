import { format } from 'date-fns';
import { AvailableTimeSlotDTO } from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';

export const groupPossibleStartTimeSlotsByDoctorIdAndDate = (
  slots: AvailableTimeSlotDTO[],
): Record<number, Record<string, AvailableTimeSlotDTO[]>> => {
  return slots.reduce(
    (acc, slot) => {
      const doctorId = slot.doctorId;
      const dayKey = format(new Date(slot.startTime), appConfig.localDateFormat);

      if (!acc[doctorId]) {
        acc[doctorId] = {};
      }
      if (!acc[doctorId][dayKey]) {
        acc[doctorId][dayKey] = [];
      }
      acc[doctorId][dayKey].push(slot);
      return acc;
    },
    {} as Record<number, Record<string, AvailableTimeSlotDTO[]>>,
  );
};
