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
import { FC, useState } from 'react';
import { Controller, useForm, useWatch } from 'react-hook-form';
import {
  useCreateAdditionalService,
  useUpdateAdditionalServiceDescription,
} from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { FileInfoDTO, Service } from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import { useFileUpload } from 'shared/hooks/useFileUpload';
import { useNotification } from 'shared/hooks/useNotification';
import * as Yup from 'yup';
import { FileUpload } from './FileUpload/FileUpload';
import { PatientInfoPanel } from './PatientInfoPanel';

interface FormData {
  serviceId: number;
  attachmentIds: number[];
  description: string;
}

interface AdditionalServiceModalProps {
  patientId: number;
  fulfillerId: number;
  patientFullName: string;
  patientAge: number | null;
  allAdditionalServices: Service[];
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => Promise<void>;
  onCancel: () => void;
  refetch?: () => Promise<void>;
  mode: 'create' | 'edit';
  loading?: boolean;
  readOnly?: boolean;
  existingServiceData?: {
    id: number;
    serviceId: number;
    existingAttachments: FileInfoDTO[];
    description?: string;
  };
}

const validationSchema = Yup.object().shape({
  serviceId: Yup.number().required('Wybierz rodzaj usługi dodatkowej'),
  description: Yup.string()
    .required('Wypełnij wyniki usługi dodatkowej')
    .max(
      appConfig.maxTextFieldLength,
      `Maksymalna długość to ${appConfig.maxTextFieldLength} znaków`,
    ),
  attachmentIds: Yup.array().of(Yup.number().required()).required(),
});

export const AdditionalServiceModal: FC<AdditionalServiceModalProps> = ({
  patientId,
  fulfillerId,
  patientFullName,
  patientAge,
  allAdditionalServices,
  confirmText = 'Potwierdź',
  cancelText = 'Anuluj',
  onConfirm,
  onCancel,
  refetch,
  loading,
  readOnly = false,
  mode,
  existingServiceData,
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      serviceId: existingServiceData?.serviceId ?? undefined,
      description: existingServiceData?.description ?? '',
      attachmentIds: existingServiceData?.existingAttachments.map((file) => file.id) ?? [],
    },
  });

  const [onConfirmLoading, setOnConfirmLoading] = useState(false);
  const [fileIdsToDeleteOnConfirm, setFileIdsToDeleteOnConfirm] = useState<number[]>([]);
  const [hasUnuploadedFiles, setHasUnuploadedFiles] = useState(false);

  const watchedDescription = useWatch({ control, name: 'description' });

  const { mutateAsync: createAdditionalService, isPending: isCreateAdditionalServiceLoading } =
    useCreateAdditionalService();
  const {
    mutateAsync: updateAdditionalServiceDescription,
    isPending: isUpdateAdditionalServiceDescriptionLoading,
  } = useUpdateAdditionalServiceDescription();

  const { showNotification, NotificationComponent } = useNotification();
  const { uploadFile, deleteFile } = useFileUpload();

  const onSubmit = async (data: FormData) => {
    if (hasUnuploadedFiles) {
      showNotification(
        'Proszę przesłać lub usunąć wszystkie załadowane pliki przed kontynuowaniem',
        'error',
      );
      return;
    }

    setOnConfirmLoading(true);
    if (mode === 'create') {
      try {
        await createAdditionalService({
          data: { ...data, date: new Date().toISOString(), patientId, fulfillerId },
        });
      } catch (err) {
        console.warn(err);
        showNotification('Nie udało się zapisać danych usługi dodatkowej', 'error');
      }
    }

    if (mode === 'edit' && existingServiceData) {
      try {
        const updates: Promise<any>[] = [];

        if (watchedDescription !== existingServiceData.description) {
          updates.push(
            updateAdditionalServiceDescription({
              id: existingServiceData.id,
              data: { description: watchedDescription },
            }),
          );
        }

        await Promise.all(updates);
      } catch (err) {
        console.warn(err);
        showNotification('Nie udało się zaktualizować danych usługi dodatkowej', 'error');
      }
    }
    const fileDeletes = fileIdsToDeleteOnConfirm.map((fileIdToDelete) => {
      return deleteFile({ id: fileIdToDelete });
    });

    await Promise.all(fileDeletes);
    await onConfirm();
    setOnConfirmLoading(false);
  };

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
          minWidth: 600,
        }}
        component="form"
        onSubmit={handleSubmit(onSubmit)}
      >
        <Typography variant="h6" fontWeight="bold">
          Dodatkowa usługa
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
                disabled={loading || readOnly || mode === 'edit'}
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
                    disabled={loading || readOnly || mode === 'edit'}
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
                  htmlInput: { maxLength: appConfig.maxTextFieldLength },
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
          title={`Załączniki${mode === 'edit' ? ' - uwaga, załączniki są usuwane i dodawane od razu' : ''}`}
          onConfirmUpload={async (file) => {
            setOnConfirmLoading(true);
            const res = await uploadFile(file, undefined, existingServiceData?.id);
            if (refetch) {
              await refetch();
            }
            setOnConfirmLoading(false);
            return res;
          }}
          onDeleteUploaded={(fileId) => {
            setFileIdsToDeleteOnConfirm([...fileIdsToDeleteOnConfirm, fileId]);
          }}
          onHasUnuploadedFiles={setHasUnuploadedFiles}
          initialFiles={existingServiceData?.existingAttachments}
          disabled={readOnly}
        />
        {!readOnly && (
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
                disabled={
                  loading ||
                  isCreateAdditionalServiceLoading ||
                  isUpdateAdditionalServiceDescriptionLoading ||
                  onConfirmLoading
                }
                loading={
                  loading ||
                  isCreateAdditionalServiceLoading ||
                  isUpdateAdditionalServiceDescriptionLoading ||
                  onConfirmLoading
                }
              >
                {confirmText}
              </Button>
            </Stack>
          </DialogActions>
        )}
      </Card>
      <NotificationComponent />
    </Dialog>
  );
};
