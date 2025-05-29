import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { format } from 'date-fns';
import { SingleVisitHeader } from 'modules/visit/components/SingleVisitHeader';
import { getVisitStatusLabel } from 'modules/visit/utils';
import { FC, useCallback, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useParams } from 'react-router';
import { useGetFilesForVisit } from 'shared/api/generated/attachment-controller/attachment-controller';
import { UpdateVisitDTO, VisitStatus } from 'shared/api/generated/generated.schemas';
import { useGetPrescriptionForVisit } from 'shared/api/generated/prescription-controller/prescription-controller';
import {
  useCancelPlannedVisit,
  useFinishVisit,
  useGetVisitById,
  useStartVisit,
  useUpdateVisit,
} from 'shared/api/generated/visit-controller/visit-controller';
import { appConfig } from 'shared/appConfig';
import ConfirmationModal from 'shared/components/ConfirmationModal/ConfirmationModal';
import { FileUpload } from 'shared/components/FileUpload/FileUpload';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { PatientInfoPanel } from 'shared/components/PatientInfoPanel';
import { useAuth } from 'shared/hooks/useAuth';
import { useFileUpload } from 'shared/hooks/useFileUpload';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { getAgeFromBirthDate } from 'shared/utils/getAgeFromBirthDate';
import * as Yup from 'yup';

const validationSchema = Yup.object().shape({
  interview: Yup.string().max(
    appConfig.maxTextFieldLength,
    `Maksymalna długość to ${appConfig.maxTextFieldLength} znaków`,
  ),
  diagnosis: Yup.string().max(
    appConfig.maxTextFieldLength,
    `Maksymalna długość to ${appConfig.maxTextFieldLength} znaków`,
  ),
  recommendations: Yup.string().max(
    appConfig.maxTextFieldLength,
    `Maksymalna długość to ${appConfig.maxTextFieldLength} znaków`,
  ),
});

interface FormData {
  interview?: string;
  diagnosis?: string;
  recommendations?: string;
}

const SingleVisitPage: FC = () => {
  const { id } = useParams();
  const visitId = Number(id);
  const { showNotification, NotificationComponent } = useNotification();
  const { uploadFile, deleteFile } = useFileUpload();
  const { isPatient } = useAuth();
  const { openModal } = useModal();

  const [fileUploadLoading, setFileUploadLoading] = useState(false);
  const [fileIdsToDeleteOnConfirm, setFileIdsToDeleteOnConfirm] = useState<number[]>([]);
  const [hasUnuploadedFiles, setHasUnuploadedFiles] = useState(false);

  const {
    data: visitInfo,
    isLoading: isVisitInfoLoading,
    isError: isVisitInfoError,
    refetch: refetchVisitInfo,
  } = useGetVisitById(visitId);

  const {
    data: visitAttachments,
    isLoading: isVisitAttachmentsLoading,
    isError: isVisitAttachmentsError,
    refetch: refetchVisitAttachments,
  } = useGetFilesForVisit(visitId);

  const {
    // data: visitPrescription,
    isLoading: isVisitPrescriptionLoading,
    isError: isVisitPrescriptionError,
    // refetch: refetchVisitPrescription,
  } = useGetPrescriptionForVisit(visitId);

  const {
    mutateAsync: updateVisit,
    isPending: isUpdateVisitLoading,
    isError: isUpdateVisitError,
  } = useUpdateVisit();

  const {
    mutateAsync: cancelVisit,
    isPending: isCancelVisitLoading,
    isError: isCancelVisitError,
  } = useCancelPlannedVisit();

  const {
    mutateAsync: startVisit,
    isPending: isStartVisitLoading,
    isError: isStartVisitError,
  } = useStartVisit();

  const {
    mutateAsync: finishVisit,
    isPending: isFinishVisitLoading,
    isError: isFinishVisitError,
  } = useFinishVisit();

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    getValues,
    trigger,
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      interview: visitInfo?.interview ?? '',
      diagnosis: visitInfo?.diagnosis ?? '',
      recommendations: visitInfo?.recommendations ?? '',
    },
  });

  const deleteFilesMarkedForDeletion = async () => {
    const fileDeletes = fileIdsToDeleteOnConfirm.map((fileIdToDelete) => {
      return deleteFile({ id: fileIdToDelete });
    });

    await Promise.all(fileDeletes);
  };

  const validateUnuploadedFiles = (): boolean => {
    if (hasUnuploadedFiles) {
      showNotification(
        'Proszę przesłać lub usunąć wszystkie załadowane pliki przed kontynuowaniem',
        'error',
      );
      return false;
    }
    return true;
  };

  const handleCancelVisitClick = async () => {
    openModal('confirmCancelVisitModal', (close) => (
      <ConfirmationModal
        title="Na pewno chcesz anulować wizytę?"
        onConfirm={async () => {
          close();
          await cancelVisit({ id: visitId });
          await refetchVisitInfo();
        }}
        onCancel={close}
      />
    ));
  };

  const handleStartVisitClick = async () => {
    openModal('confirmStartVisitModal', (close) => (
      <ConfirmationModal
        title="Na pewno chcesz rozpocząć wizytę?"
        message=""
        onConfirm={async () => {
          close();
          await startVisit({ id: visitId });
          await refetchVisitInfo();
        }}
        onCancel={close}
      />
    ));
  };

  const handleFinishVisitClick = async () => {
    const isValid = await trigger();

    if (!isValid) {
      return;
    }

    openModal('confirmFinishVisitModal', (close) => (
      <ConfirmationModal
        title="Na pewno chcesz zakończyć wizytę?"
        onConfirm={async () => {
          if (!validateUnuploadedFiles()) {
            return;
          }
          const formData = getValues();

          close();
          await finishVisit({ id: visitId, data: formData });
          await deleteFilesMarkedForDeletion();
          await refetchVisitInfo();
          await refetchVisitAttachments();
          showNotification('Wizyta została zakończona', 'success');
        }}
        onCancel={close}
      />
    ));
  };

  const onSubmit = useCallback(
    async (updateData: UpdateVisitDTO) => {
      if (!validateUnuploadedFiles()) {
        return;
      }

      await updateVisit({ id: visitId, data: updateData });
      await deleteFilesMarkedForDeletion();
      await refetchVisitInfo();
      await refetchVisitAttachments();
      showNotification('Pomyślnie zaktualizowano wizytę!', 'success');
    },
    [updateVisit, refetchVisitInfo, showNotification],
  );

  const isLoading =
    isVisitInfoLoading ||
    isVisitAttachmentsLoading ||
    isVisitPrescriptionLoading ||
    isUpdateVisitLoading;

  const isError = isVisitInfoError || isVisitAttachmentsError || isVisitPrescriptionError;

  useEffect(() => {
    if (isError) {
      showNotification('Coś poszło nie tak', 'error');
    }
    if (isUpdateVisitError) {
      showNotification('Nie udało się zaktualizować danych wizyty', 'error');
    }
    if (isCancelVisitError) {
      showNotification('Nie udało się anulować wizyty', 'error');
    }
    if (isStartVisitError) {
      showNotification('Nie udało się rozpocząć wizyty', 'error');
    }
    if (isFinishVisitError) {
      showNotification('Nie udało się zakończyć wizyty', 'error');
    }
    if (visitInfo?.status === VisitStatus.PLANNED && !isPatient) {
      showNotification('Rozpocznij wizytę, aby edytować jej szczegóły', 'warning');
    }
  }, [
    isError,
    isUpdateVisitError,
    isCancelVisitError,
    isStartVisitError,
    isFinishVisitError,
    visitInfo,
    isPatient,
  ]);

  useEffect(() => {
    if (visitInfo) {
      reset({
        interview: visitInfo.interview ?? '',
        diagnosis: visitInfo.diagnosis ?? '',
        recommendations: visitInfo.recommendations ?? '',
      });
    }
  }, [visitInfo, reset]);

  if (isLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (!visitInfo) {
    return null;
  }

  const { label: visitStatusLabel, color: visitStatusColor } = getVisitStatusLabel(
    visitInfo.status,
  );
  const inputsDisabled = isPatient || visitInfo.status !== VisitStatus.IN_PROGRESS;

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-8">
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
          py: 6,
        }}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <SingleVisitHeader
            visitId={visitInfo.id}
            doctorFullName={visitInfo.doctorFullName}
            serviceTypeName={visitInfo.serviceName}
            visitStatusLabel={visitStatusLabel}
            visitStatusColor={visitStatusColor}
            plannedVisitStartDate={
              visitInfo.date ? format(new Date(visitInfo.date), 'dd.MM.yyyy') : undefined
            }
          />
          <PatientInfoPanel
            patientId={visitInfo.patientId}
            patientPesel={visitInfo.patientPesel}
            patientFullName={visitInfo.patientFullName}
            patientAge={getAgeFromBirthDate(new Date(visitInfo.patientBirthDate))}
          />
        </Box>
        {!isPatient && (
          <Box sx={{ display: 'flex', flexDirection: 'row', gap: 3 }}>
            {visitInfo.status === VisitStatus.IN_PROGRESS && (
              <Button
                color="primary"
                variant="contained"
                loading={isFinishVisitLoading || isVisitInfoLoading}
                disabled={isFinishVisitLoading || isVisitInfoLoading}
                onClick={handleFinishVisitClick}
              >
                Zakończ wizytę
              </Button>
            )}
            {visitInfo.status === VisitStatus.PLANNED && (
              <Button
                color="error"
                variant="contained"
                loading={isCancelVisitLoading || isVisitInfoLoading}
                disabled={isCancelVisitLoading || isVisitInfoLoading}
                onClick={handleCancelVisitClick}
              >
                Anuluj wizytę
              </Button>
            )}

            {visitInfo.status === VisitStatus.PLANNED && (
              <Button
                color="primary"
                variant="contained"
                loading={isStartVisitLoading || isVisitInfoLoading}
                disabled={isStartVisitLoading || isVisitInfoLoading}
                onClick={handleStartVisitClick}
              >
                Rozpocznij wizytę
              </Button>
            )}
            <Button
              type="submit"
              color="secondary"
              variant="contained"
              loading={isUpdateVisitLoading || isVisitInfoLoading}
              disabled={isUpdateVisitLoading || isVisitInfoLoading || inputsDisabled}
            >
              Zapisz zmiany
            </Button>
          </Box>
        )}
      </Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 8, width: '70%' }}>
        <TextField
          value={visitInfo.patientInformation ?? ''}
          label="Informacje od pacjenta"
          multiline
          minRows={5}
          slotProps={{ input: { readOnly: true } }}
          sx={{
            pointerEvents: 'none',
          }}
        />

        <Controller
          name="interview"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Wywiad"
              multiline
              minRows={10}
              error={!!errors.interview}
              helperText={errors.interview?.message}
              disabled={inputsDisabled}
            />
          )}
        />

        <Controller
          name="diagnosis"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Diagnoza"
              multiline
              minRows={8}
              error={!!errors.diagnosis}
              helperText={errors.diagnosis?.message}
              disabled={inputsDisabled}
            />
          )}
        />

        <Controller
          name="recommendations"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Zalecenia"
              multiline
              minRows={8}
              error={!!errors.recommendations}
              helperText={errors.recommendations?.message}
              disabled={inputsDisabled}
            />
          )}
        />
      </Box>

      <FileUpload
        onConfirmUpload={async (file) => {
          setFileUploadLoading(true);
          const res = await uploadFile(file, visitId);
          await refetchVisitAttachments();
          setFileUploadLoading(false);
          return res;
        }}
        onDeleteUploaded={(fileId) => {
          setFileIdsToDeleteOnConfirm([...fileIdsToDeleteOnConfirm, fileId]);
        }}
        onHasUnuploadedFiles={setHasUnuploadedFiles}
        initialFiles={visitAttachments}
        disabled={inputsDisabled}
        uploadFileLoading={fileUploadLoading}
      />

      <NotificationComponent />
    </form>
  );
};

export default SingleVisitPage;
