import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, Pagination, PaginationItem, TextField, Typography } from '@mui/material';
import { FC, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { WorkTimeDayOfWeek } from 'shared/api/generated/generated.schemas';
import { WorkTimeWithoutIdAndUser } from 'src/pages/SingleSpecialistPage';
import * as Yup from 'yup';
import { isValid15MinuteTime, mapFromWorkTimes, mapToWorkTimes } from '../utils';

interface EditWorkTimeTabProps {
  currentWorkTimes: WorkTimeWithoutIdAndUser[];
  onSave: (updatedWorkTimes: WorkTimeWithoutIdAndUser[]) => void;
  loading?: boolean;
}

const polishDayAbbreviations: Record<WorkTimeDayOfWeek, string> = {
  [WorkTimeDayOfWeek.MONDAY]: 'Pon.',
  [WorkTimeDayOfWeek.TUESDAY]: 'Wt.',
  [WorkTimeDayOfWeek.WEDNESDAY]: 'Śr.',
  [WorkTimeDayOfWeek.THURSDAY]: 'Czw.',
  [WorkTimeDayOfWeek.FRIDAY]: 'Pt.',
  [WorkTimeDayOfWeek.SATURDAY]: 'Sob.',
  [WorkTimeDayOfWeek.SUNDAY]: 'Niedz.',
};

export type WorkTimeFormValues = {
  workTimes: Record<WorkTimeDayOfWeek, TimePair>;
};

export type TimePair = {
  startTime: string;
  endTime: string;
};

const timePairSchema = Yup.object()
  .shape({
    startTime: Yup.string()
      .default('')
      .test('15-min-step', 'Minuty muszą być podzielne przez 15', isValid15MinuteTime),
    endTime: Yup.string()
      .default('')
      .test('15-min-step', 'Minuty muszą być podzielne przez 15', isValid15MinuteTime),
  })
  .test(
    'time-validation',
    'Proszę wypełnić, lub zostawić puste obydwie wartości',
    function (value) {
      const { startTime, endTime } = value;
      if (!startTime && !endTime) {
        return true;
      }
      if (startTime && endTime) {
        return true;
      }
      return false;
    },
  );

const workTimesShape = Object.values(WorkTimeDayOfWeek).reduce(
  (acc, day) => {
    acc[day] = timePairSchema;
    return acc;
  },
  {} as Record<WorkTimeDayOfWeek, Yup.ObjectSchema<TimePair>>,
);

const validationSchema: Yup.ObjectSchema<WorkTimeFormValues> = Yup.object().shape({
  workTimes: Yup.object().shape(workTimesShape),
});

const days = Object.values(WorkTimeDayOfWeek);

export const EditWorkTimeTab: FC<EditWorkTimeTabProps> = ({
  currentWorkTimes,
  onSave,
  loading,
}) => {
  const defaultValues = mapFromWorkTimes(currentWorkTimes);
  console.log('defaultValues: ', defaultValues);
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<WorkTimeFormValues>({
    defaultValues,
    resolver: yupResolver(validationSchema),
  });

  const [selectedDayIndex, setSelectedDayIndex] = useState(0);
  const selectedDay = days[selectedDayIndex];

  const onSubmit = (data: WorkTimeFormValues) => {
    console.log('data in onsubmit: ', data);
    console.log('currentWorkTimes in onsubmit: ', currentWorkTimes);
    const updatedWorkTimes = mapToWorkTimes(data);
    onSave(updatedWorkTimes);
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)} p={2}>
      <Pagination
        count={days.length}
        page={selectedDayIndex + 1}
        onChange={(_, page) => setSelectedDayIndex(page - 1)}
        sx={{ mb: 6 }}
        shape="rounded"
        hidePrevButton
        hideNextButton
        renderItem={(item) => {
          if (item.page === null) {
            return <PaginationItem {...item} />;
          }

          const day = days[item.page - 1];
          const hasError = !!errors.workTimes?.[day];
          return (
            <PaginationItem
              {...item}
              page={polishDayAbbreviations[days[item.page - 1]]}
              sx={{ color: hasError ? 'error.main' : '' }}
            />
          );
        }}
      />
      <Box sx={{ display: 'flex', gap: 10, width: '60%' }}>
        <Controller
          key={`start-${selectedDay}`}
          control={control}
          name={`workTimes.${selectedDay}.startTime`}
          render={({ field }) => (
            <TextField
              {...field}
              sx={{ width: '100%' }}
              label="Godzina rozpoczęcia pracy"
              type="time"
              slotProps={{ inputLabel: { shrink: true }, htmlInput: { step: 900 } }}
              error={
                !!errors.workTimes?.[selectedDay]?.startTime || !!errors.workTimes?.[selectedDay]
              }
              helperText={
                errors.workTimes?.[selectedDay]?.startTime?.message ??
                errors.workTimes?.[selectedDay]?.message
              }
            />
          )}
        />

        <Controller
          key={`end-${selectedDay}`}
          control={control}
          name={`workTimes.${selectedDay}.endTime`}
          render={({ field }) => (
            <TextField
              {...field}
              sx={{ width: '100%' }}
              label="Godzina zakończenia pracy"
              type="time"
              slotProps={{ inputLabel: { shrink: true }, htmlInput: { step: 900 } }}
              error={
                !!errors.workTimes?.[selectedDay]?.endTime || !!errors.workTimes?.[selectedDay]
              }
              helperText={
                errors.workTimes?.[selectedDay]?.endTime?.message ??
                errors.workTimes?.[selectedDay]?.message
              }
            />
          )}
        />
      </Box>
      {Object.keys(errors).length > 0 && (
        <Typography
          color="error"
          variant="body2"
          sx={{
            pt: 6,
            alignSelf: 'start',
          }}
        >
          Wypełnij odpowiednio dane dla każdego dnia
        </Typography>
      )}
      <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 4 }}>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          loading={loading}
          disabled={loading}
        >
          Zapisz
        </Button>
      </Box>
    </Box>
  );
};
