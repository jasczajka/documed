import { yupResolver } from '@hookform/resolvers/yup';
import {
  Button,
  Dialog,
  DialogActions,
  DialogTitle,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from '@mui/material';
import dayjs from 'dayjs';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { ReferralType, ReferralTypeDTO } from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import * as Yup from 'yup';

interface FormData {
  expirationDate: Date;
  type: ReferralType;
  diagnosis: string;
}

const validationSchema = Yup.object().shape({
  expirationDate: Yup.date()
    .required('Data ważności jest wymagana')
    .min(new Date(), 'Data ważności musi być w przyszłości'),

  type: Yup.mixed<ReferralType>().required('Typ skierowania jest wymagany'),
  diagnosis: Yup.string()
    .required('Diagnoza jest wymagana')
    .max(
      appConfig.maxTextFieldLength,
      `Maksymalna długość to ${appConfig.maxTextFieldLength} znaków`,
    ),
});

interface AddReferralToVisitModalProps {
  referralTypes: ReferralTypeDTO[];
  onSubmit: (data: FormData) => void;
  onCancel: () => void;
  disabled?: boolean;
}

export const AddReferralToVisitModal: FC<AddReferralToVisitModalProps> = ({
  referralTypes,
  onSubmit,
  onCancel,
  disabled = false,
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      expirationDate: dayjs().add(7, 'day').toDate(),
      diagnosis: '',
    },
  });

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
      <DialogTitle>Dodaj skierowanie do wizyty</DialogTitle>
      <Controller
        name="expirationDate"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Data ważności"
            type="date"
            fullWidth
            error={!!errors.expirationDate}
            helperText={errors.expirationDate?.message}
            slotProps={{
              inputLabel: { shrink: true },
              htmlInput: {
                min: dayjs().format('YYYY-MM-DD'),
                max: dayjs().add(365, 'day').format('YYYY-MM-DD'),
              },
            }}
            value={dayjs(field.value).format('YYYY-MM-DD')}
            onChange={(e) => {
              const date = new Date(e.target.value);
              field.onChange(isNaN(date.getTime()) ? e.target.value : date);
            }}
            disabled={disabled}
          />
        )}
      />

      <FormControl fullWidth margin="normal" error={!!errors.type}>
        <InputLabel id="referral-type-label">Typ skierowania</InputLabel>
        <Controller
          name="type"
          control={control}
          render={({ field }) => (
            <Select
              {...field}
              labelId="referral-type-label"
              label="Typ skierowania"
              error={!!errors.type}
              disabled={disabled}
            >
              {referralTypes.map((type) => (
                <MenuItem key={type.code} value={type.code}>
                  {type.description}
                </MenuItem>
              ))}
            </Select>
          )}
        />
        <FormHelperText>{errors.type?.message}</FormHelperText>
      </FormControl>

      <Controller
        name="diagnosis"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Diagnoza"
            fullWidth
            multiline
            rows={4}
            error={!!errors.diagnosis}
            helperText={errors.diagnosis?.message}
            disabled={disabled}
          />
        )}
      />
      <DialogActions sx={{ mt: 2, justifyContent: 'flex-end', gap: 1 }}>
        <Button onClick={onCancel} color="error" variant="outlined" sx={{ minWidth: 100 }}>
          Anuluj
        </Button>
        <Button type="submit" color="success" variant="contained" sx={{ minWidth: 100 }}>
          Potwierdź
        </Button>
      </DialogActions>
    </Dialog>
  );
};
