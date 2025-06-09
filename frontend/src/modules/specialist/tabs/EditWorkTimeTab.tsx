import { yupResolver } from '@hookform/resolvers/yup';
import {
  Autocomplete,
  Box,
  Button,
  Pagination,
  PaginationItem,
  TextField,
  Typography,
} from '@mui/material';
import { FC, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { DayOfWeekEnum, UploadWorkTimeDTO } from 'shared/api/generated/generated.schemas';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import * as Yup from 'yup';
import { isValid15MinuteTime, mapFromWorkTimes, mapToWorkTimes } from '../utils';

interface EditWorkTimeTabProps {
  currentWorkTimes: UploadWorkTimeDTO[];
  onSave: (updatedWorkTimes: UploadWorkTimeDTO[]) => void;
  loading?: boolean;
  disabled?: boolean;
}

const polishDayAbbreviations: Record<DayOfWeekEnum, string> = {
  [DayOfWeekEnum.MONDAY]: 'Pon.',
  [DayOfWeekEnum.TUESDAY]: 'Wt.',
  [DayOfWeekEnum.WEDNESDAY]: 'Śr.',
  [DayOfWeekEnum.THURSDAY]: 'Czw.',
  [DayOfWeekEnum.FRIDAY]: 'Pt.',
  [DayOfWeekEnum.SATURDAY]: 'Sob.',
  [DayOfWeekEnum.SUNDAY]: 'Niedz.',
};

export type WorkTimeFormValues = {
  workTimes: Record<DayOfWeekEnum, TimePairWithFacilityId>;
};

export type TimePairWithFacilityId = {
  startTime: string;
  endTime: string;
  facilityId: number;
};

const timePairSchema = Yup.object()
  .shape({
    startTime: Yup.string()
      .default('')
      .test('15-min-step', 'Minuty muszą być podzielne przez 15', isValid15MinuteTime),
    endTime: Yup.string()
      .default('')
      .test('15-min-step', 'Minuty muszą być podzielne przez 15', isValid15MinuteTime),
    facilityId: Yup.number()
      .default(-1)
      .when(['startTime', 'endTime'], {
        is: (startTime: string, endTime: string) => startTime && endTime,
        then: (schema) => schema.required('Placówka jest wymagana, gdy podane są godziny pracy'),
        otherwise: (schema) => schema.nullable().notRequired(),
      }),
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

const workTimesShape = Object.values(DayOfWeekEnum).reduce(
  (acc, day) => {
    acc[day] = timePairSchema;
    return acc;
  },
  {} as Record<DayOfWeekEnum, Yup.ObjectSchema<TimePairWithFacilityId>>,
);

const validationSchema: Yup.ObjectSchema<WorkTimeFormValues> = Yup.object().shape({
  workTimes: Yup.object().shape(workTimesShape),
});

const days = Object.values(DayOfWeekEnum);

export const EditWorkTimeTab: FC<EditWorkTimeTabProps> = ({
  currentWorkTimes,
  onSave,
  loading,
  disabled = false,
}) => {
  const defaultValues = mapFromWorkTimes(currentWorkTimes);
  const allFacilities = useFacilityStore((state) => state.facilities);
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
    const updatedWorkTimes = mapToWorkTimes(data);
    onSave(updatedWorkTimes);
  };

  return (
    <Box component={disabled ? 'div' : 'form'} onSubmit={handleSubmit(onSubmit)} p={2}>
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
      <Box sx={{ display: 'flex', gap: 10, width: '60%', paddingBottom: 4 }}>
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
              disabled={disabled}
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
              disabled={disabled}
            />
          )}
        />
      </Box>
      <Box sx={{ display: 'flex', gap: 10, width: '60%' }}>
        {' '}
        <Controller
          key={`facility-${selectedDay}`}
          control={control}
          name={`workTimes.${selectedDay}.facilityId`}
          render={({ field: { onChange, value }, fieldState: { error } }) => (
            <Autocomplete
              options={allFacilities}
              getOptionLabel={(option) => `${option.city} ${option.address}`}
              onChange={(_, newValue) => {
                onChange(newValue?.id ?? null);
              }}
              value={allFacilities.find((facility) => facility.id === value) ?? null}
              noOptionsText="Brak dostępnych placówek"
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Placówka"
                  error={!!error}
                  helperText={error?.message}
                />
              )}
              sx={{ width: '100%' }}
              disabled={disabled}
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
          disabled={loading || disabled}
        >
          Zapisz
        </Button>
      </Box>
    </Box>
  );
};
