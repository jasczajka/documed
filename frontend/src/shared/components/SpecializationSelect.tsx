import {
  Box,
  Chip,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
} from '@mui/material';
import { Controller, useFormContext } from 'react-hook-form';
import { Specialization } from 'shared/api/generated/generated.schemas';

type SpecializationSelectProps = {
  specializations: Specialization[];
  label: string;
  isLoading?: boolean;
};

export const SpecializationSelect = ({
  specializations,
  label,
  isLoading,
}: SpecializationSelectProps) => {
  const {
    control,
    formState: { errors },
  } = useFormContext();

  const getSpecializationOptions = (specializations: Specialization[]) =>
    specializations.map((spec) => ({ id: spec.id, name: spec.name }));

  const options = getSpecializationOptions(specializations ?? []);

  return (
    <FormControl error={!!errors.specializationIds}>
      <InputLabel id="specialization-label">{label}</InputLabel>
      <Controller
        name="specializationIds"
        control={control}
        render={({ field }) => (
          <Select
            multiple
            label={label}
            value={field.value}
            onChange={(e) => field.onChange(e.target.value)}
            renderValue={(selected) => (
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                {(selected as number[]).map((id: number) => {
                  const spec = options.find((opt) => opt.id === id);
                  return <Chip key={id} label={spec?.name || id} />;
                })}
              </Box>
            )}
            sx={{ minWidth: 330, width: '100%' }}
            disabled={isLoading}
          >
            {options.map((option) => (
              <MenuItem key={option.id} value={option.id}>
                {option.name}
              </MenuItem>
            ))}
          </Select>
        )}
      />
      {errors.specializationIds && (
        <FormHelperText>
          {(errors.specializationIds as { message?: string })?.message}
        </FormHelperText>
      )}
    </FormControl>
  );
};
