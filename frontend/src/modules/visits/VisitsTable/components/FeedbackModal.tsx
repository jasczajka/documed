import { yupResolver } from '@hookform/resolvers/yup';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Rating,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { FC, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useGiveFeedbackForVisit } from 'shared/api/generated/visit-controller/visit-controller';
import { appConfig } from 'shared/appConfig';
import { useNotification } from 'shared/hooks/useNotification';
import * as Yup from 'yup';

interface FormData {
  rating: number;
  message?: string;
}

const validationSchema = Yup.object().shape({
  rating: Yup.number()
    .required('Rating is required')
    .min(1, 'Proszę wybrać ocenę')
    .max(5, 'Proszę wybrać przynajmniej co najwyżej 5 gwiazdek'),
  message: Yup.string().max(
    appConfig.maxAdditionalInfoVisitLength,
    `Maksymalna długość to ${appConfig.maxAdditionalInfoVisitLength} znaków`,
  ),
});

interface FeedbackModalProps {
  title?: string;
  visitId: number;
  onCancel: () => void;
  onSubmitSuccess?: () => void;
  disabled?: boolean;
  existingValues?: {
    rating: number;
    message?: string;
  };
}

export const FeedbackModal: FC<FeedbackModalProps> = ({
  title = 'Wystawianie opinii',
  visitId,
  onCancel,
  onSubmitSuccess,
  disabled = false,
  existingValues,
}) => {
  const { showNotification, NotificationComponent } = useNotification();
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: existingValues || {
      rating: 0,
      message: '',
    },
  });

  const {
    mutateAsync: giveFeedbackForVisit,
    isPending: isLoading,
    isError,
  } = useGiveFeedbackForVisit();

  const onSubmit = async (data: FormData) => {
    await giveFeedbackForVisit({ id: visitId, data });
    if (onSubmitSuccess) {
      onSubmitSuccess();
    }
    onCancel();
  };

  useEffect(() => {
    if (isError) {
      showNotification('Coś poszło nie tak przy wystawianiu opinii', 'error');
    }
  }, [isError]);

  return (
    <Dialog
      open
      onClose={onCancel}
      slotProps={{
        paper: {
          sx: {
            p: 3,
            borderRadius: 3,
            minWidth: 500,
          },
        },
      }}
      component="form"
      onSubmit={handleSubmit(onSubmit)}
    >
      {title && <DialogTitle>{title}</DialogTitle>}
      <DialogContent>
        <Stack direction="column" spacing={3} width="100%">
          <Stack direction="column" spacing={1}>
            <Controller
              name="rating"
              control={control}
              render={({ field }) => (
                <Rating
                  {...field}
                  size="large"
                  max={5}
                  onChange={(_, value) => field.onChange(value)}
                  disabled={disabled}
                />
              )}
            />
            {errors.rating && (
              <Typography color="error" variant="caption">
                {errors.rating.message}
              </Typography>
            )}
          </Stack>

          <Controller
            name="message"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Dodatkowe informacje dla nas"
                multiline
                rows={4}
                error={!!errors.message}
                helperText={errors.message?.message}
                fullWidth
                disabled={disabled}
              />
            )}
          />
        </Stack>
      </DialogContent>
      {!disabled && (
        <DialogActions sx={{ mt: 2, justifyContent: 'flex-end', gap: 1 }}>
          <Button
            onClick={onCancel}
            color="error"
            variant="outlined"
            sx={{ minWidth: 100 }}
            disabled={isLoading}
          >
            Anuluj
          </Button>
          <Button
            type="submit"
            color="success"
            variant="contained"
            sx={{ minWidth: 100 }}
            disabled={isLoading}
            loading={isLoading}
          >
            Potwierdź
          </Button>
        </DialogActions>
      )}
      <NotificationComponent />
    </Dialog>
  );
};
