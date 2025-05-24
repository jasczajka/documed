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
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Service } from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import { useFileUpload } from 'shared/hooks/useFileUpload';
import * as Yup from 'yup';
import { FileUpload } from './FileUpload/FileUpload';
import { PatientInfoPanel } from './PatientInfoPanel';

interface FormData {
  serviceId: number;
  attachmentIds: number[];
  description: string;
}

interface AdditionalServiceModalProps {
  title?: string;
  patientId: number;
  fulfillerId: number;
  patientFullName: string;
  patientAge: number | null;
  allAdditionalServices: Service[];
  confirmText?: string;
  cancelText?: string;
  onConfirm: (data: {
    serviceId: number;
    fulfillerId: number;
    attachmentIds: number[];
    description: string;
  }) => void;
  onCancel: () => void;
  loading?: boolean;
  readOnly?: boolean;
}

const validationSchema = Yup.object().shape({
  serviceId: Yup.number().required('Wybierz rodzaj usługi dodatkowej'),
  description: Yup.string()
    .required('Wypełnij wyniki usługi dodatkowej')
    .max(
      appConfig.maxDescriptionAdditionalServiceLength,
      `Maksymalna długość to ${appConfig.maxDescriptionAdditionalServiceLength} znaków`,
    ),
  attachmentIds: Yup.array().of(Yup.number().required()).required(),
});

export const AdditionalServiceModal: FC<AdditionalServiceModalProps> = ({
  title = 'Dodatkowa usługa',
  patientId,
  fulfillerId,
  patientFullName,
  patientAge,
  allAdditionalServices,
  confirmText = 'Potwierdź',
  cancelText = 'Anuluj',
  onConfirm,
  onCancel,
  loading,
  readOnly = false,
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      serviceId: undefined,
      description: undefined,
      attachmentIds: [],
    },
  });

  const onSubmit = (data: FormData) => {
    onConfirm({
      ...data,
      fulfillerId,
    });
  };

  const { uploadFile, deleteFile } = useFileUpload();

  return (
    <Dialog maxWidth={false} open onClose={onCancel}>
      <Card
        sx={{
          px: 8,
          py: 6,
          display: 'flex',
          flexDirection: 'column',
          gap: 6,
          overflow: 'visible',
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
                options={allAdditionalServices}
                getOptionLabel={(option) => option.name}
                onChange={(_, newValue) => {
                  field.onChange(newValue?.id);
                }}
                value={allAdditionalServices.find((service) => service.id === field.value) ?? null}
                noOptionsText="Brak opcji spełniających wyszukiwanie"
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Rodzaj usługi"
                    error={!!errors.serviceId}
                    helperText={errors.serviceId?.message}
                    disabled={readOnly}
                  />
                )}
                fullWidth
              />
            )}
          />
        </Stack>
        <DialogContent sx={{ p: 0, paddingTop: 2 }}>
          <Controller
            name="description"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                multiline
                slotProps={{
                  htmlInput: { maxLength: appConfig.maxDescriptionAdditionalServiceLength },
                }}
                label="Wyniki"
                fullWidth
                error={!!errors.description}
                helperText={errors.description?.message}
                disabled={readOnly}
              />
            )}
          />
        </DialogContent>
        <FileUpload
          onConfirmUpload={(file) => uploadFile(file)}
          onDeleteUploaded={(fileId) => deleteFile({ id: fileId })}
          onAttachmentsChange={(fileIds) => setValue('attachmentIds', fileIds)}
        />
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
