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
import { appConfig } from 'shared/appConfig';
import * as Yup from 'yup';
import { PatientInfoPanel } from './PatientInfoPanel';
import { VisitDatepicker } from './VisitDatepicker/VisitDatepicker';
import { generatePossibleStartTimes } from './VisitDatepicker/utils';

interface FormData {
  serviceId: number;
  doctorId: number;
  visitDate: Date;
  additionalInfo?: string;
}

interface ScheduleVisitModalProps {
  title?: string;
  patientId: number;
  patientFullName: string;
  patientAge: number;
  allDoctors: DoctorDetailsDTO[];
  allServices: Service[];
  confirmText?: string;
  cancelText?: string;
  onConfirm: (
    serviceId: number,
    doctorId: number,
    visitDate: Date,
    additionalInfo?: string,
  ) => void;
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
  const possibleStartTimes = useMemo(() => generatePossibleStartTimes(), []);

  const {
    control,
    handleSubmit,
    watch,
    formState: { errors },
    resetField,
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      serviceId: undefined,
      doctorId: undefined,
      visitDate: undefined,
      additionalInfo: undefined,
    },
  });

  const selectedServiceId = watch('serviceId');
  const selectedDoctorId = watch('doctorId');

  const availableDoctorsForChosenService = useMemo(() => {
    if (!selectedServiceId) return [];

    const service = allServices.find((s) => s.id === selectedServiceId);
    if (!service) return [];

    return allDoctors.filter((doctor) => {
      const doctorSpecializationIds = doctor.specializations.map((spec) => spec.id);
      return service.specializationIds.some((requiredId) =>
        doctorSpecializationIds.includes(requiredId),
      );
    });
  }, [allDoctors, allServices, selectedServiceId]);

  const onSubmit = (data: FormData) => {
    onConfirm(data.serviceId, data.doctorId, data.visitDate, data.additionalInfo);
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
                getOptionLabel={(option) => `dr. ${option.firstName} ${option.lastName}`}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id);
                  // @TODO here we will await new dates, disable stuff, trigger some loading state maybe on the buttons?
                  resetField('visitDate');
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
                timeSlotCount={3}
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
              disabled={loading}
              loading={loading}
            >
              {confirmText}
            </Button>
          </Stack>
        </DialogActions>
      </Card>
    </Dialog>
  );
};
