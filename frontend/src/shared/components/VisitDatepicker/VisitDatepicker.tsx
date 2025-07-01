import { CalendarMonth, ExpandMore } from '@mui/icons-material';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Button,
  Collapse,
  Paper,
  Typography,
} from '@mui/material';
import { addMinutes, format } from 'date-fns';
import { pl } from 'date-fns/locale';
import { FC, useMemo, useState } from 'react';
import { AvailableTimeSlotDTO } from 'shared/api/generated/generated.schemas';
import { useDoctorsStore } from 'shared/hooks/stores/useDoctorsStore';
import { groupPossibleStartTimeSlotsByDoctorIdAndDate } from './utils';

interface VisitDatepickerProps {
  timeSlotCount: number;
  timeSlotLengthInMinutes: number;
  possibleStartTimes: AvailableTimeSlotDTO[];
  selectedTimeSlot: AvailableTimeSlotDTO | null;
  onSelectTimeSlot: (timeSlot: AvailableTimeSlotDTO) => void;
  selectedDoctorId?: string;
  disabled?: boolean;
  error?: string;
}

export const VisitDatepicker: FC<VisitDatepickerProps> = ({
  timeSlotCount,
  timeSlotLengthInMinutes,
  possibleStartTimes,
  selectedTimeSlot,
  onSelectTimeSlot,
  disabled,
  error,
}) => {
  const doctors = useDoctorsStore((state) => state.doctors);
  const doctorMap = useMemo(() => {
    return doctors.reduce<Record<string, { firstName: string; lastName: string }>>(
      (acc, doctor) => {
        acc[doctor.id.toString()] = { firstName: doctor.firstName, lastName: doctor.lastName };
        return acc;
      },
      {},
    );
  }, []);

  const [isDayListOpen, setIsDayListOpen] = useState(false);

  const startTimesGroupedByDoctorAndDay = useMemo(
    () => groupPossibleStartTimeSlotsByDoctorIdAndDate(possibleStartTimes),
    [possibleStartTimes],
  );

  const selectedEndTime =
    selectedTimeSlot &&
    addMinutes(selectedTimeSlot.startTime, timeSlotCount * timeSlotLengthInMinutes);

  return (
    <Paper elevation={0} className="p-2">
      <Box className="flex flex-wrap items-center justify-between gap-4">
        <Typography variant="subtitle1">
          <Box component="span">Wybrany termin:</Box>{' '}
          <Typography
            fontWeight={selectedTimeSlot ? 'bold' : ''}
            color={error ? 'error' : 'textPrimary'}
          >
            {selectedTimeSlot ? (
              <>
                {format(new Date(selectedTimeSlot.startTime), 'd MMMM yyyy, HH:mm', { locale: pl })}{' '}
                - {selectedEndTime ? format(selectedEndTime, 'HH:mm', { locale: pl }) : ''}
              </>
            ) : (
              'Brak'
            )}
          </Typography>
        </Typography>
        {possibleStartTimes.length > 0 ? (
          <Button
            variant="outlined"
            startIcon={<CalendarMonth />}
            onClick={() => setIsDayListOpen((prev) => !prev)}
            disabled={disabled}
          >
            {isDayListOpen ? 'Schowaj terminy' : 'Pokaż terminy'}
          </Button>
        ) : (
          <Button variant="outlined" startIcon={<CalendarMonth />} disabled>
            Brak terminów
          </Button>
        )}
      </Box>
      <Collapse in={isDayListOpen && !disabled}>
        {Object.entries(startTimesGroupedByDoctorAndDay)
          .sort(([idA], [idB]) => Number(idA) - Number(idB))
          .map(([doctorId, days]) => (
            <Accordion key={doctorId} sx={{ marginTop: 6 }}>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography>
                  {doctorMap[doctorId]?.lastName} {doctorMap[doctorId]?.firstName}
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                {Object.entries(days)
                  .sort(([dayA], [dayB]) => new Date(dayA).getTime() - new Date(dayB).getTime())
                  .map(([day, slots]) => (
                    <Accordion key={day}>
                      <AccordionSummary expandIcon={<ExpandMore />}>
                        <Typography>
                          {format(new Date(day), 'd MMMM yyyy', { locale: pl })}
                        </Typography>
                      </AccordionSummary>
                      <AccordionDetails className="flex flex-wrap gap-2">
                        {slots
                          .sort(
                            (a, b) =>
                              new Date(a.startTime).getTime() - new Date(b.startTime).getTime(),
                          )
                          .map((slot) => {
                            const slotDate = new Date(slot.startTime);
                            return (
                              <Button
                                key={slot.id}
                                variant={
                                  selectedTimeSlot?.id === slot.id ? 'contained' : 'outlined'
                                }
                                onClick={() => onSelectTimeSlot(slot)}
                              >
                                {format(slotDate, 'HH:mm')}
                              </Button>
                            );
                          })}
                      </AccordionDetails>
                    </Accordion>
                  ))}
              </AccordionDetails>
            </Accordion>
          ))}
      </Collapse>
    </Paper>
  );
};
