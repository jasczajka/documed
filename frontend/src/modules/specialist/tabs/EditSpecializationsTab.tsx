import { yupResolver } from '@hookform/resolvers/yup';
import { Autocomplete, Box, Button, Chip, FormControl, TextField, Typography } from '@mui/material';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Specialization } from 'shared/api/generated/generated.schemas';
import * as Yup from 'yup';

interface EditSpecializationsTabProps {
  currentSpecializations: Specialization[];
  allSpecializations: Specialization[];
  onSave: (selected: Specialization[]) => void;
  loading?: boolean;
  disabled?: boolean;
}

type FormValues = {
  specializations: Specialization[];
};

const validationSchema = Yup.object({
  specializations: Yup.array()
    .min(1, 'Musisz wybrać co najmniej jedną specjalizację')
    .required('Specjalizacje są wymagane'),
});

export const EditSpecializationsTab: FC<EditSpecializationsTabProps> = ({
  currentSpecializations,
  allSpecializations,
  onSave,
  loading,
  disabled = false,
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      specializations: currentSpecializations,
    },
    resolver: yupResolver(validationSchema),
  });
  const onSubmit = (data: FormValues) => {
    onSave(data.specializations);
  };

  return (
    <Box
      component={disabled ? 'div' : 'form'}
      onSubmit={handleSubmit(onSubmit)}
      sx={{ display: 'flex', flexDirection: 'column', gap: 6 }}
    >
      <Controller
        name="specializations"
        control={control}
        render={({ field }) => {
          const selected = field.value;
          const availableOptions = allSpecializations.filter(
            (spec) => !selected.some((sel) => sel.id === spec.id),
          );
          return (
            <Box sx={{ display: 'flex', gap: 6, width: '100%' }}>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, width: '50%' }}>
                <Typography variant="body1">Dodaj lub usuń specjalizacje specjalisty</Typography>
                <Box className="flex flex-wrap gap-2">
                  {selected.map((spec) => (
                    <Chip
                      key={spec.id}
                      label={spec.name}
                      onDelete={() => field.onChange(selected.filter((s) => s.id !== spec.id))}
                      color="primary"
                    />
                  ))}
                </Box>
                {errors.specializations && (
                  <Typography color="error" variant="caption">
                    {errors.specializations.message}
                  </Typography>
                )}
              </Box>

              <Box sx={{ width: '50%', display: 'flex' }}>
                <FormControl fullWidth>
                  <Autocomplete
                    options={availableOptions}
                    getOptionLabel={(option) => option.name}
                    onChange={(_, newValue) => {
                      if (newValue) {
                        field.onChange([...selected, newValue]);
                      }
                    }}
                    value={null}
                    noOptionsText="Brak opcji spełniających wyszukiwanie"
                    renderInput={(params) => <TextField {...params} label="Dodaj specjalizację" />}
                    disabled={disabled}
                  />
                </FormControl>
              </Box>
            </Box>
          );
        }}
      />
      <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          loading={loading}
          disabled={loading || disabled}
        >
          Zapisz zmiany
        </Button>
      </Box>
    </Box>
  );
};
