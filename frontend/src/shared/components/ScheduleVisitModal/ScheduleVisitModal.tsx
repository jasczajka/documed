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
import { FC, useMemo, useState } from 'react';
import { Controller, useForm, useWatch } from 'react-hook-form';
import { AvailableTimeSlotDTO, ScheduleVisitDTO } from 'shared/api/generated/generated.schemas';
import { useGetAvailableFirstTimeSlotsByFacility } from 'shared/api/generated/time-slot-controller/time-slot-controller';
import {
  useCalculateVisitCost,
  useScheduleVisit,
} from 'shared/api/generated/visit-controller/visit-controller';
import { appConfig } from 'shared/appConfig';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useDoctorsStore } from 'shared/hooks/stores/useDoctorsStore';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import { useServicesStore } from 'shared/hooks/stores/useServicesStore';
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
  initialDoctorId?: number;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => Promise<void>;
  onCancel: () => void;
  loading?: boolean;
}

const validationSchema = Yup.object().shape({
  serviceId: Yup.number().required('Wybierz rodzaj usługi'),
  doctorId: Yup.number().nullable(),
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
  initialDoctorId,
  confirmText = 'Potwierdź',
  cancelText = 'Anuluj',
  onConfirm,
  onCancel,
  loading,
}) => {
  const currentFacilityId = useAuthStore((state) => state.user?.facilityId);
  const allFacilities = useFacilityStore((state) => state.facilities);
  const allDoctors = useDoctorsStore((state) => state.doctors);
  const regularServices = useServicesStore((state) => state.regularServices);
  const {
    control,
    handleSubmit,
    formState: { errors },
    setValue,
    resetField,
  } = useForm<Partial<FormData>>({
    resolver: yupResolver(validationSchema) as any,
    defaultValues: {
      serviceId: undefined,
      doctorId: initialDoctorId,
      visitDate: undefined,
      additionalInfo: undefined,
      facilityId: currentFacilityId ? currentFacilityId : undefined,
    },
  });

  const selectedServiceId = useWatch({ control, name: 'serviceId' });
  const selectedDoctorId = useWatch({ control, name: 'doctorId' });
  const selectedFacilityId = useWatch({ control, name: 'facilityId' });
  const [selectedSlot, setSelectedSlot] = useState<AvailableTimeSlotDTO | null>(null);

  const { showNotification, NotificationComponent } = useNotification();

  const neededTimeSlots = useMemo(() => {
    return calculateNeededTimeSlots(
      selectedServiceId,
      regularServices,
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
  } = useGetAvailableFirstTimeSlotsByFacility(
    {
      neededTimeSlots,
      facilityId: selectedFacilityId ?? -1,
    },
    {
      query: {
        enabled: !!selectedServiceId && !!selectedFacilityId && !!neededTimeSlots,
      },
    },
  );

  const { data: visitCost, refetch: refetchVisitCost } = useCalculateVisitCost(
    { patientId: patientId, serviceId: selectedServiceId ?? -1 },
    { query: { enabled: !!selectedServiceId } },
  );

  const availableDoctorsForChosenService = useMemo(() => {
    if (!selectedServiceId) {
      return [];
    }

    const service = regularServices.find((s) => s.id === selectedServiceId);
    if (!service) {
      return [];
    }

    return allDoctors.filter((doctor) => {
      const doctorSpecializationIds = doctor.specializations.map((spec) => spec.id);
      return service.specializationIds.some((requiredId) =>
        doctorSpecializationIds.includes(requiredId),
      );
    });
  }, [selectedServiceId]);

  const availableTimeSlotsFilteredByDoctor = useMemo(() => {
    if (!selectedDoctorId) {
      return availableTimeSlots.filter((slot) =>
        availableDoctorsForChosenService.some((doctor) => doctor.id === slot.doctorId),
      );
    }
    return availableTimeSlots.filter((slot) => slot.doctorId === selectedDoctorId);
  }, [selectedDoctorId, availableTimeSlots, availableDoctorsForChosenService]);

  const availableServices = useMemo(() => {
    if (!initialDoctorId) {
      return regularServices;
    }

    const doctor = allDoctors.find((d) => d.id === initialDoctorId);
    if (!doctor) return regularServices;

    const doctorSpecializationIds = doctor.specializations.map((spec) => spec.id);
    return regularServices.filter((service) =>
      service.specializationIds.some((id) => doctorSpecializationIds.includes(id)),
    );
  }, [initialDoctorId]);

  const onSubmit = async (data: Partial<FormData>) => {
    if (data.serviceId && data.doctorId && data.visitDate && data.facilityId && selectedSlot) {
      await handleScheduleVisit({
        patientInformation: data.additionalInfo,
        patientId: patientId,
        doctorId: selectedSlot.doctorId,
        firstTimeSlotId: selectedSlot.id,
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
                options={availableServices}
                getOptionLabel={(option) => option.name}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id);
                  resetField('doctorId');
                  resetField('visitDate');
                  setSelectedSlot(null);
                  if (newValue?.id) {
                    refetchVisitCost();
                  }
                }}
                value={regularServices.find((service) => service.id === field.value) ?? null}
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
                getOptionLabel={(option) => `${option.lastName} ${option.firstName}`}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id ?? null);
                  setValue('visitDate', undefined);
                  setSelectedSlot(null);
                }}
                value={allDoctors.find((doctor) => doctor.id === field.value) ?? null}
                isOptionEqualToValue={(option, value) => option.id === value?.id}
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
                if (newValue?.id) {
                  refetchTimeSlots();
                }
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
            render={() => (
              <VisitDatepicker
                timeSlotCount={neededTimeSlots}
                timeSlotLengthInMinutes={appConfig.timeSlotLengthInMinutes}
                possibleStartTimes={availableTimeSlotsFilteredByDoctor}
                selectedTimeSlot={selectedSlot}
                onSelectTimeSlot={(slot) => {
                  setSelectedSlot(slot);
                  setValue('visitDate', new Date(slot.startTime));
                  setValue('doctorId', slot.doctorId);
                  setValue('firstTimeSlotId', slot.id);
                }}
                disabled={!selectedServiceId || !selectedFacilityId || loading}
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
