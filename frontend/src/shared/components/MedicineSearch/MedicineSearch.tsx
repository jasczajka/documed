import { Autocomplete, CircularProgress, TextField } from '@mui/material';
import { useCallback, useEffect, useState } from 'react';
import { Medicine } from 'shared/api/generated/generated.schemas';
import { useSearchMedicines } from 'shared/api/generated/medicine-controller/medicine-controller';
import { useDebounce } from 'use-debounce';

export const MedicineSearch = () => {
  const [open, setOpen] = useState(false);
  const [medicineQuery, setMedicineQuery] = useState('');
  const [options, setOptions] = useState<readonly Medicine[]>([]);

  const [debouncedQuery] = useDebounce(medicineQuery, 300);

  const { data, isFetching } = useSearchMedicines(
    { q: debouncedQuery, limit: 10 },
    {
      query: {
        enabled: Boolean(debouncedQuery.trim()),
      },
    },
  );

  const handleOpen = useCallback(async () => {
    setOpen(true);
  }, []);

  const handleClose = useCallback(() => {
    setOpen(false);
    setOptions([]);
  }, []);

  useEffect(() => {
    if (data) {
      setOptions(data);
    } else {
      setOptions([]);
    }
  }, [data]);

  return (
    <Autocomplete
      loadingText="Ładowanie"
      noOptionsText="Brak opcji"
      open={open}
      onOpen={handleOpen}
      onClose={handleClose}
      isOptionEqualToValue={(option, value) => option.id === value.id}
      getOptionLabel={(option) => `${option.name} ${option.dosage}`}
      options={options}
      loading={isFetching}
      onInputChange={(_, value) => setMedicineQuery(value)}
      renderInput={(params) => (
        <TextField
          {...params}
          label="Lek"
          slotProps={{
            input: {
              ...params.InputProps,
              endAdornment: (
                <>
                  {isFetching ? <CircularProgress color="inherit" size={20} /> : null}
                  {params.InputProps.endAdornment}
                </>
              ),
            },
          }}
        />
      )}
    />
  );
};
