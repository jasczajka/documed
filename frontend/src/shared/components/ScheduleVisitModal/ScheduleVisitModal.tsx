import { yupResolver } from '@hookform/resolvers/yup';
import {
  Autocomplete,
  Button,
  Card,
  Dialog,
  DialogActions,
  DialogContent,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { FC, useMemo } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { DoctorDetailsDTO, Service } from 'shared/api/generated/generated.schemas';
import { useGetAvailableFirstTimeSlotsByDoctor } from 'shared/api/generated/time-slot-controller/time-slot-controller';
import { appConfig } from 'shared/appConfig';
import * as Yup from 'yup';
import { PatientInfoPanel } from '../PatientInfoPanel';
import { VisitDatepicker } from '../VisitDatepicker/VisitDatepicker';
import { calculateNeededTimeSlots } from './utils';

interface FormData {
  serviceId: number;
  doctorId: number;
  visitDate: Date;
  firstTimeSlotId: number;
  additionalInfo?: string;
}

interface ScheduleVisitModalProps {
  title?: string;
  patientId: number;
  patientFullName: string;
  patientAge: number | null;
  allDoctors: DoctorDetailsDTO[];
  allServices: Service[];
  confirmText?: string;
  cancelText?: string;
  onConfirm: (data: {
    serviceId: number;
    doctorId: number;
    firstTimeSlotId: number;
    additionalInfo?: string;
  }) => Promise<void>;
  onCancel: () => void;
  loading?: boolean;
}

const validationSchema = Yup.object().shape({
  serviceId: Yup.number().required('Wybierz rodzaj usługi'),
  doctorId: Yup.number().required('Wybierz specjalistę'),
  visitDate: Yup.date().required('Wybierz termin wizyty'),
  additionalInfo: Yup.string().max(
    appConfig.maxAdditionalInfoVisitLength,
    `Maksymalna długość to ${appConfig.maxAdditionalInfoVisitLength} znaków`,
  ),
});

export const ScheduleVisitModal: FC<ScheduleVisitModalProps> = ({
  title = 'Umów wizytę',
  patientId,
  patientFullName,
  patientAge,
  allDoctors,
  allServices,
  confirmText = 'Potwierdź',
  cancelText = 'Anuluj',
  onConfirm,
  onCancel,
  loading,
}) => {
  const {
    control,
    handleSubmit,
    watch,
    formState: { errors },
    resetField,
  } = useForm<Partial<FormData>>({
    resolver: yupResolver(validationSchema) as any,
    defaultValues: {
      serviceId: undefined,
      doctorId: undefined,
      visitDate: undefined,
      additionalInfo: undefined,
    },
  });

  const selectedServiceId = watch('serviceId');
  const selectedDoctorId = watch('doctorId');

  const neededTimeSlots = useMemo(() => {
    return calculateNeededTimeSlots(
      selectedServiceId,
      allServices,
      appConfig.timeSlotLengthInMinutes,
    );
  }, [selectedServiceId]);

  const {
    data: availableTimeSlots = [],
    isLoading: isPossibleStartTimesLoading,
    refetch: refetchTimeSlots,
  } = useGetAvailableFirstTimeSlotsByDoctor(
    selectedDoctorId ?? -1,
    {
      neededTimeSlots,
    },
    { query: { enabled: !!selectedDoctorId && !!selectedServiceId && !!neededTimeSlots } },
  );

  const possibleStartTimes = useMemo(() => {
    if (!availableTimeSlots) return [];

    return availableTimeSlots.map((slot) => new Date(slot.startTime));
  }, [availableTimeSlots]);

  const availableDoctorsForChosenService = useMemo(() => {
    if (!selectedServiceId) {
      return [];
    }

    const service = allServices.find((s) => s.id === selectedServiceId);
    if (!service) {
      return [];
    }

    return allDoctors.filter((doctor) => {
      const doctorSpecializationIds = doctor.specializations.map((spec) => spec.id);
      return service.specializationIds.some((requiredId) =>
        doctorSpecializationIds.includes(requiredId),
      );
    });
  }, [allDoctors, allServices, selectedServiceId]);

  const onSubmit = (data: Partial<FormData>) => {
    if (data.serviceId && data.doctorId && data.visitDate) {
      const selectedTimeSlot = availableTimeSlots.find(
        (slot) => new Date(slot.startTime).getTime() === data.visitDate!.getTime(),
      );

      if (!selectedTimeSlot) {
        console.error('No matching time slot found');
        return;
      }
      console.log(data);
      onConfirm({
        serviceId: data.serviceId,
        doctorId: data.doctorId,
        firstTimeSlotId: selectedTimeSlot.id,
        additionalInfo: data.additionalInfo,
      });
    }
  };

  return (
    <Dialog open onClose={onCancel}>
      <Card
        sx={{
          px: 8,
          py: 6,
          display: 'flex',
          flexDirection: 'column',
          gap: 6,
          minWidth: 600,
        }}
        component="form"
        onSubmit={handleSubmit(onSubmit)}
      >
        <Typography variant="h6" fontWeight="bold">
          {title}
        </Typography>
        <PatientInfoPanel
          patientId={patientId}
          patientFullName={patientFullName}
          patientAge={patientAge}
        />
        <Stack direction="row" spacing={8} width="100%">
          <Controller
            name="serviceId"
            control={control}
            render={({ field }) => (
              <Autocomplete
                disabled={loading}
                options={allServices}
                getOptionLabel={(option) => option.name}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id);
                  resetField('doctorId');
                  resetField('visitDate');
                }}
                value={allServices.find((service) => service.id === field.value) ?? null}
                noOptionsText="Brak opcji spełniających wyszukiwanie"
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Rodzaj usługi"
                    error={!!errors.serviceId}
                    helperText={errors.serviceId?.message}
                  />
                )}
                fullWidth
              />
            )}
          />
          <Controller
            name="doctorId"
            control={control}
            render={({ field }) => (
              <Autocomplete
                disabled={!selectedServiceId || loading}
                options={availableDoctorsForChosenService}
                getOptionLabel={(option) => `d ${option.firstName} ${option.lastName}`}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id);
                  console.log('new value id: ', newValue?.id);
                  resetField('visitDate');
                  if (newValue?.id) {
                    refetchTimeSlots();
                  }
                }}
                value={allDoctors.find((doctor) => doctor.id === field.value) ?? null}
                noOptionsText="Brak opcji spełniających wyszukiwanie"
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Specjalista"
                    error={!!errors.doctorId}
                    helperText={errors.doctorId?.message}
                  />
                )}
                fullWidth
              />
            )}
          />
        </Stack>
        <DialogContent sx={{ p: 0 }}>
          <Controller
            name="visitDate"
            control={control}
            render={({ field }) => (
              <VisitDatepicker
                timeSlotCount={neededTimeSlots}
                timeSlotLengthInMinutes={appConfig.timeSlotLengthInMinutes}
                possibleStartTimes={possibleStartTimes}
                onConfirmSelectedStartTime={(selectedStartDate) => {
                  field.onChange(selectedStartDate);
                }}
                disabled={!selectedDoctorId || !selectedServiceId || loading}
                error={errors.visitDate?.message}
              />
            )}
          />
        </DialogContent>
        <DialogContent sx={{ p: 0, paddingTop: 2 }}>
          <Controller
            name="additionalInfo"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                multiline
                slotProps={{ htmlInput: { maxLength: appConfig.maxAdditionalInfoVisitLength } }}
                label="Dodatkowe informacje (opcjonalnie)"
                fullWidth
                error={!!errors.additionalInfo}
                helperText={errors.additionalInfo?.message}
              />
            )}
          />
        </DialogContent>
        <DialogActions sx={{ p: 0 }}>
          <Stack direction="row" spacing={8} width="100%">
            <Button fullWidth onClick={onCancel} color="error" variant="contained">
              {cancelText}
            </Button>
            <Button
              fullWidth
              type="submit"
              color="success"
              variant="contained"
              disabled={loading || isPossibleStartTimesLoading}
              loading={loading || isPossibleStartTimesLoading}
            >
              {confirmText}
            </Button>
          </Stack>
        </DialogActions>
      </Card>
    </Dialog>
  );
};
