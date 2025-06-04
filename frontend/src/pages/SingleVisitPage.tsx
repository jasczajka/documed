import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { addDays, format } from 'date-fns';
import { SinglePrescriptionTable } from 'modules/prescriptions/components/SinglePrescriptionTable/SinglePrescriptionTable';
import {
  ReferralWithTempId,
  VisitReferralsTable,
} from 'modules/referrals/components/VisitReferralsTable';
import { SingleVisitHeader } from 'modules/visit/components/SingleVisitHeader';
import { getVisitStatusLabel } from 'modules/visit/utils';
import { FC, useCallback, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useParams } from 'react-router';
import { useGetFilesForVisit } from 'shared/api/generated/attachment-controller/attachment-controller';
import { UpdateVisitDTO, VisitWithDetailsStatus } from 'shared/api/generated/generated.schemas';
import {
  useAddMedicineToPrescription,
  useCreatePrescription,
  useGetMedicinesForPrescription,
  useGetPrescriptionForVisit,
  useRemoveMedicineFromPrescription,
  useUpdatePrescriptionExpirationDate,
} from 'shared/api/generated/prescription-controller/prescription-controller';
import {
  useCreateReferral,
  useGetAllReferralsForVisit,
  useGetAllReferralTypes,
  useRemoveReferral,
} from 'shared/api/generated/referral-controller/referral-controller';
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

  const [referralOperations, setReferralOperations] = useState<{
    toAdd: Set<ReferralWithTempId>;
    toDelete: Set<number>;
  }>({ toAdd: new Set(), toDelete: new Set() });

  const [medicineOperations, setMedicineOperations] = useState<{
    toAdd: Map<string, number>;
    toDelete: Set<string>;
  }>({ toAdd: new Map(), toDelete: new Set() });

  const [prescriptionExpirationDate, setPrescriptionExpirationDate] = useState<Date>(
    addDays(new Date(), 30),
  );

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
    data: visitPrescription,
    isLoading: isVisitPrescriptionLoading,
    isError: isVisitPrescriptionError,
    refetch: refetchVisitPrescription,
  } = useGetPrescriptionForVisit(visitId);

  const {
    data: prescriptionMedicines,
    isLoading: isPrescriptionMedicinesLoading,
    isError: prescriptionMedicinesError,
    refetch: refetchPrescriptionMedicines,
  } = useGetMedicinesForPrescription(visitPrescription?.id ?? -1, {
    query: { enabled: !!visitPrescription?.id },
  });

  const {
    data: referralTypes,
    isLoading: isReferralTypesLoading,
    isError: isReferralTypesError,
  } = useGetAllReferralTypes();

  const {
    data: visitReferrals,
    isLoading: isVisitReferralsLoading,
    isError: isVisitReferralsError,
    refetch: refetchVisitReferrals,
  } = useGetAllReferralsForVisit(visitId);

  const { mutateAsync: addMedicineToPrescription } = useAddMedicineToPrescription();

  const { mutateAsync: removeMedicineFromPrescription } = useRemoveMedicineFromPrescription();

  const { mutateAsync: createPrescription } = useCreatePrescription();

  const { mutateAsync: updatePrescriptionExpirationDate } = useUpdatePrescriptionExpirationDate();

  const { mutateAsync: createReferral } = useCreateReferral();

  const { mutateAsync: removeReferral } = useRemoveReferral();

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

  const handleAddMedicine = (medicineId: string, amount: number) => {
    setMedicineOperations((prev) => {
      const newToAdd = new Map(prev.toAdd);
      const newToDelete = new Set(prev.toDelete);

      if (newToDelete.has(medicineId)) {
        newToDelete.delete(medicineId);
      }

      newToAdd.set(medicineId, amount);
      return { toAdd: newToAdd, toDelete: newToDelete };
    });
  };

  const handleRemoveMedicine = (medicineId: string) => {
    setMedicineOperations((prev) => {
      const newToAdd = new Map(prev.toAdd);
      const newToDelete = new Set(prev.toDelete);

      if (newToAdd.has(medicineId)) {
        newToAdd.delete(medicineId);
      } else {
        if (prescriptionMedicines?.some((m) => m.id === medicineId)) {
          newToDelete.add(medicineId);
        }
      }

      return { toAdd: newToAdd, toDelete: newToDelete };
    });
  };

  const saveMedicinesToPrescription = async () => {
    let prescriptionId: number;
    const operations: Promise<any>[] = [];

    if (!visitPrescription) {
      const createPrescriptionRes = await createPrescription({
        visitId,
        data: { expirationDate: prescriptionExpirationDate.toISOString() },
      });
      prescriptionId = createPrescriptionRes.id;
    } else {
      prescriptionId = visitPrescription.id;
    }

    medicineOperations.toAdd.forEach((amount, medicineId) => {
      operations.push(
        addMedicineToPrescription({
          prescriptionId,
          medicineId,
          params: { amount },
        }),
      );
    });

    medicineOperations.toDelete.forEach((medicineId) => {
      operations.push(
        removeMedicineFromPrescription({
          prescriptionId,
          medicineId,
        }),
      );
    });

    if (operations.length > 0) {
      await Promise.all(operations);
      setMedicineOperations({ toAdd: new Map(), toDelete: new Set() });
      await refetchVisitPrescription();
      await refetchPrescriptionMedicines();
    }
  };

  const handleRemoveReferral = (referralId: number) => {
    setReferralOperations((prev) => {
      const newToAdd = new Set(prev.toAdd);
      const newToDelete = new Set(prev.toDelete);

      const referralToAdd = Array.from(newToAdd).find(
        (r) => r.id === referralId || r.tempId === referralId.toString(),
      );

      if (referralToAdd) {
        newToAdd.delete(referralToAdd);
      } else {
        newToDelete.add(referralId);
      }

      return { toAdd: newToAdd, toDelete: newToDelete };
    });
  };

  const handlePendingRemoveReferral = (tempId: string) => {
    setReferralOperations((prev) => {
      const newToAdd = new Set(prev.toAdd);
      const newToDelete = new Set(prev.toDelete);

      Array.from(newToAdd).forEach((referral) => {
        if (referral.tempId === tempId) {
          newToAdd.delete(referral);
        }
      });

      return { toAdd: newToAdd, toDelete: newToDelete };
    });
  };

  const handleAddReferral = (newReferral: ReferralWithTempId) => {
    setReferralOperations((prev) => {
      const newToAdd = new Set(prev.toAdd);
      const newToDelete = new Set(prev.toDelete);

      if (newReferral.id && newToDelete.has(newReferral.id)) {
        newToDelete.delete(newReferral.id);
      }

      newToAdd.add(newReferral);

      return { toAdd: newToAdd, toDelete: newToDelete };
    });
  };

  const saveReferrals = async () => {
    const operations: Promise<any>[] = [];

    referralOperations.toAdd.forEach((referralToCreate) => {
      operations.push(
        createReferral({
          data: {
            visitId,
            type: referralToCreate.type,
            diagnosis: referralToCreate.diagnosis,
            expirationDate: referralToCreate.expirationDate,
          },
        }),
      );
    });

    referralOperations.toDelete.forEach((referralIdToDelete) => {
      if (referralIdToDelete) {
        operations.push(removeReferral({ referralId: referralIdToDelete }));
      }
    });

    if (operations.length > 0) {
      await Promise.all(operations);
      setReferralOperations({ toAdd: new Set(), toDelete: new Set() });
      await refetchVisitReferrals();
    }
  };

  const handlePrescriptionExpirationDateUpdate = async () => {
    if (!visitPrescription || !prescriptionExpirationDate) {
      return;
    }

    const serverDateFormatted = format(
      new Date(visitPrescription.expirationDate),
      appConfig.localDateFormat,
    );
    const formDateFormatted = format(
      new Date(prescriptionExpirationDate),
      appConfig.localDateFormat,
    );

    if (serverDateFormatted !== formDateFormatted) {
      await updatePrescriptionExpirationDate({
        prescriptionId: visitPrescription.id,
        data: formDateFormatted,
      });
      setPrescriptionExpirationDate(new Date(prescriptionExpirationDate));
    }
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
          await saveMedicinesToPrescription();
          await saveReferrals();
          await handlePrescriptionExpirationDateUpdate();
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
      await saveMedicinesToPrescription();
      await saveReferrals();
      await handlePrescriptionExpirationDateUpdate();
      await refetchVisitInfo();
      await refetchVisitAttachments();
      showNotification('Pomyślnie zaktualizowano wizytę!', 'success');
    },
    [
      updateVisit,
      deleteFilesMarkedForDeletion,
      saveMedicinesToPrescription,
      saveReferrals,
      handlePrescriptionExpirationDateUpdate,
      refetchVisitInfo,
      refetchVisitAttachments,
    ],
  );

  const isLoading =
    isVisitInfoLoading ||
    isVisitAttachmentsLoading ||
    isVisitPrescriptionLoading ||
    isUpdateVisitLoading ||
    isPrescriptionMedicinesLoading ||
    isReferralTypesLoading ||
    isVisitReferralsLoading;

  const isError = isVisitAttachmentsError || isVisitPrescriptionError || isReferralTypesError;

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
    if (prescriptionMedicinesError) {
      showNotification('Nie udało się pobrać leków recepty', 'error');
    }
    if (isVisitInfoError) {
      showNotification('Nie udało się pobrać danych wizyty', 'error');
    }
    if (isVisitReferralsError) {
      showNotification('Nie udało się pobrać skierowań wizyty', 'error');
    }
  }, [
    isError,
    isUpdateVisitError,
    isCancelVisitError,
    isStartVisitError,
    isFinishVisitError,
    prescriptionMedicinesError,
    isVisitInfoError,
    isVisitInfoError,
  ]);

  useEffect(() => {
    if (visitPrescription?.expirationDate) {
      setPrescriptionExpirationDate(new Date(visitPrescription.expirationDate));
    }
  }, [visitPrescription?.expirationDate]);

  useEffect(() => {
    if (visitInfo) {
      reset({
        interview: visitInfo.interview ?? '',
        diagnosis: visitInfo.diagnosis ?? '',
        recommendations: visitInfo.recommendations ?? '',
      });
    }
    if (visitInfo?.status === VisitWithDetailsStatus.PLANNED && !isPatient) {
      showNotification('Rozpocznij wizytę, aby edytować jej szczegóły', 'warning');
    }
  }, [visitInfo, reset]);

  if (isLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (!visitInfo || !referralTypes) {
    return <NotificationComponent />;
  }

  const { label: visitStatusLabel, color: visitStatusColor } = getVisitStatusLabel(
    visitInfo.status,
  );
  const inputsDisabled = isPatient || visitInfo.status !== VisitWithDetailsStatus.IN_PROGRESS;

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
            {visitInfo.status === VisitWithDetailsStatus.IN_PROGRESS && (
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
            {visitInfo.status === VisitWithDetailsStatus.PLANNED && (
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

            {visitInfo.status === VisitWithDetailsStatus.PLANNED && (
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
      <Box sx={{ width: '70%' }}>
        <SinglePrescriptionTable
          existingMedicines={prescriptionMedicines}
          onAddMedicineToPrescription={handleAddMedicine}
          onRemoveMedicineFromPrescription={handleRemoveMedicine}
          prescriptionExpirationDate={prescriptionExpirationDate}
          handlePrescriptionExpirationDateChange={(newDate) =>
            setPrescriptionExpirationDate(newDate)
          }
          disabled={inputsDisabled}
        />
      </Box>
      <Box sx={{ width: '70%' }}>
        <VisitReferralsTable
          referralTypes={referralTypes}
          visitId={visitId}
          existingReferrals={visitReferrals}
          onRemoveReferral={handleRemoveReferral}
          onPendingRemoveReferral={handlePendingRemoveReferral}
          onAddReferral={handleAddReferral}
          disabled={inputsDisabled}
        />
      </Box>
      <NotificationComponent />
    </form>
  );
};

export default SingleVisitPage;
