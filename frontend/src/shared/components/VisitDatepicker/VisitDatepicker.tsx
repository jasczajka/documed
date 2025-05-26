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
import { groupPossibleStartTimesByDay } from './utils';

interface VisitDatepickerProps {
  timeSlotCount: number;
  timeSlotLengthInMinutes: number;
  possibleStartTimes: Date[];
  onConfirmSelectedStartTime: (selectedStartTime: Date) => void;
  disabled?: boolean;
  error?: string;
}

export const VisitDatepicker: FC<VisitDatepickerProps> = ({
  timeSlotCount,
  timeSlotLengthInMinutes,
  possibleStartTimes,
  onConfirmSelectedStartTime,
  disabled,
  error,
}) => {
  const [isDayListOpen, setIsDayListOpen] = useState(false);
  const [selectedStartTime, setSelectedStartTime] = useState<Date | null>(null);
  const startTimesGroupedByDay = useMemo(
    () => groupPossibleStartTimesByDay(possibleStartTimes),
    [possibleStartTimes],
  );

  const selectedEndTime =
    selectedStartTime && addMinutes(selectedStartTime, timeSlotCount * timeSlotLengthInMinutes);

  const isStartTimesListEmpty = Object.keys(startTimesGroupedByDay).length === 0;

  return (
    <Paper elevation={0} className="p-2">
      <Box className="flex flex-wrap items-center justify-between gap-4">
        <Typography variant="subtitle1">
          <Box component="span">Wybrany termin:</Box>{' '}
          <Typography
            fontWeight={selectedStartTime ? 'bold' : ''}
            color={error ? 'error' : 'textPrimary'}
          >
            {selectedStartTime ? (
              <>
                {format(selectedStartTime, 'd MMMM yyyy, HH:mm', { locale: pl })} –{' '}
                {selectedEndTime ? format(selectedEndTime, 'HH:mm', { locale: pl }) : ''}
              </>
            ) : (
              'Brak'
            )}
          </Typography>
        </Typography>
        <Button
          variant="outlined"
          startIcon={<CalendarMonth />}
          onClick={() => setIsDayListOpen((prev) => !prev)}
          disabled={disabled}
        >
          {isDayListOpen ? 'Schowaj dni' : 'Pokaż dni'}
        </Button>
      </Box>
      <Collapse in={isDayListOpen && !disabled}>
        <Box className="mt-2 space-y-2">
          {isStartTimesListEmpty ? (
            <Typography variant="subtitle1">Brak terminów dla tego lekarza</Typography>
          ) : (
            Object.entries(startTimesGroupedByDay)
              .sort(([dayA], [dayB]) => new Date(dayA).getTime() - new Date(dayB).getTime())
              .map(([day, times]) => (
                <Accordion key={day}>
                  <AccordionSummary expandIcon={<ExpandMore />}>
                    <Typography>{format(new Date(day), 'd MMMM yyyy', { locale: pl })}</Typography>
                  </AccordionSummary>
                  <AccordionDetails className="flex flex-wrap gap-2">
                    {times
                      .sort((a, b) => a.getTime() - b.getTime())
                      .map((time) => (
                        <Button
                          key={time.toISOString()}
                          variant={
                            selectedStartTime?.getTime() === time.getTime()
                              ? 'contained'
                              : 'outlined'
                          }
                          onClick={() => {
                            setSelectedStartTime(time);
                            onConfirmSelectedStartTime(time);
                          }}
                        >
                          {format(time, 'HH:mm')}
                        </Button>
                      ))}
                  </AccordionDetails>
                </Accordion>
              ))
          )}
        </Box>
      </Collapse>
    </Paper>
  );
};
