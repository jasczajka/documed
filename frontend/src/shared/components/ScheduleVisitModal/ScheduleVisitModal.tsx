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
import {
  DoctorDetailsDTO,
  ScheduleVisitDTO,
  Service,
} from 'shared/api/generated/generated.schemas';
import { useGetAvailableFirstTimeSlotsByDoctor } from 'shared/api/generated/time-slot-controller/time-slot-controller';
import {
  useCalculateVisitCost,
  useScheduleVisit,
} from 'shared/api/generated/visit-controller/visit-controller';
import { appConfig } from 'shared/appConfig';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import { useNotification } from 'shared/hooks/useNotification';
import * as Yup from 'yup';
import { PatientInfoPanel } from '../PatientInfoPanel';
import { VisitDatepicker } from '../VisitDatepicker/VisitDatepicker';
import { calculateNeededTimeSlots } from './utils';

export interface FormData {
  serviceId: number;
  doctorId: number;
  facilityId: number;
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
  onConfirm: () => Promise<void>;
  onCancel: () => void;
  loading?: boolean;
}

const validationSchema = Yup.object().shape({
  serviceId: Yup.number().required('Wybierz rodzaj usługi'),
  doctorId: Yup.number().required('Wybierz specjalistę'),
  facilityId: Yup.number().required('Wybierz placówkę'),
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
  const currentFacilityId = useAuthStore((state) => state.user?.facilityId);
  const allFacilities = useFacilityStore((state) => state.facilities);
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
      facilityId: currentFacilityId ? currentFacilityId : undefined,
    },
  });

  const selectedServiceId = watch('serviceId');
  const selectedDoctorId = watch('doctorId');

  const { showNotification, NotificationComponent } = useNotification();

  const neededTimeSlots = useMemo(() => {
    return calculateNeededTimeSlots(
      selectedServiceId,
      allServices,
      appConfig.timeSlotLengthInMinutes,
    );
  }, [selectedServiceId]);

  const { mutateAsync: scheduleVisit, isPending: isScheduleVisitLoading } = useScheduleVisit();

  const handleScheduleVisit = async (data: ScheduleVisitDTO) => {
    try {
      await scheduleVisit({ data });
    } catch (err) {
      console.warn(err);
      showNotification('Nie udało się umówić wizyty', 'error');
    }
  };

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

  const { data: visitCost, refetch: refetchVisitCost } = useCalculateVisitCost(
    { patientId: patientId, serviceId: selectedServiceId ?? -1 },
    { query: { enabled: !!selectedServiceId } },
  );

  const possibleStartTimes = useMemo(() => {
    if (!availableTimeSlots) {
      return [];
    }

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

  const onSubmit = async (data: Partial<FormData>) => {
    if (data.serviceId && data.doctorId && data.visitDate && data.facilityId) {
      const selectedTimeSlot = availableTimeSlots.find(
        (slot) => new Date(slot.startTime).getTime() === data.visitDate!.getTime(),
      );

      if (!selectedTimeSlot) {
        console.error('No matching time slot found');
        return;
      }

      await handleScheduleVisit({
        patientInformation: data.additionalInfo,
        patientId: patientId,
        doctorId: data.doctorId,
        firstTimeSlotId: selectedTimeSlot.id,
        serviceId: data.serviceId,
        facilityId: data.facilityId,
      });
      await onConfirm();
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
          overflow: 'visible',
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
                  if (newValue?.id) {
                    refetchVisitCost();
                  }
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
                getOptionLabel={(option) => `${option.firstName} ${option.lastName}`}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id);
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
        <TextField
          label="Cena wizyty"
          value={visitCost ? `${visitCost.toFixed(2)} zł` : 'Wybierz usługę, aby poznać cenę'}
          fullWidth
          slotProps={{ input: { readOnly: true } }}
          sx={{
            pointerEvents: 'none',
          }}
        />
        <Controller
          name="facilityId"
          control={control}
          render={({ field }) => (
            <Autocomplete
              options={allFacilities}
              getOptionLabel={(option) => `${option.city} ${option.address}`}
              onChange={(_, newValue) => {
                field.onChange(newValue?.id ?? null);
              }}
              value={allFacilities.find((facility) => facility.id === field.value) ?? null}
              noOptionsText="Brak dostępnych placówek"
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Placówka"
                  error={!!errors.facilityId}
                  helperText={errors.facilityId?.message}
                />
              )}
              fullWidth
            />
          )}
        />
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
              disabled={loading || isPossibleStartTimesLoading || isScheduleVisitLoading}
              loading={loading || isPossibleStartTimesLoading || isScheduleVisitLoading}
            >
              {confirmText}
            </Button>
          </Stack>
        </DialogActions>
      </Card>
      <NotificationComponent />
    </Dialog>
  );
};
