import { Box, Button, MenuItem, Stack, TextField } from '@mui/material';
import { ChangeEvent, useCallback, useMemo } from 'react';

export type FilterConfig = {
  name: string;
  label: string;
  type: 'text' | 'select' | 'datetime';
  options?: { value: string; label: string }[];
  width?: number | string;
};

interface TableFiltersProps<T extends Record<string, string>> {
  filters: T;
  filterConfig: FilterConfig[];
  onFilterChange: (name: keyof T, value: string) => void;
  onReset: () => void;
}

export const TableFilters = <T extends Record<string, string>>({
  filters,
  filterConfig,
  onFilterChange,
  onReset,
}: TableFiltersProps<T>) => {
  const handleChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      onFilterChange(name as keyof T, value);
    },
    [onFilterChange],
  );

  const anyFilterActive = useMemo(
    () => Object.values(filters).some((value) => value !== ''),
    [filters],
  );

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
      <Box sx={{ display: 'flex' }}>
        <Button variant={anyFilterActive ? 'contained' : 'outlined'} onClick={onReset}>
          Wyczyść filtry
        </Button>
      </Box>
      <Stack direction="row" spacing={2} sx={{ gap: 2, mb: 6 }} flexWrap="wrap" useFlexGap>
        {filterConfig.map((filter) => {
          const commonProps = {
            key: filter.name,
            label: filter.label,
            name: filter.name,
            value: filters[filter.name] || '',
            onChange: handleChange,
            size: 'small' as const,
            sx: { minWidth: filter.width || 200 },
          };

          switch (filter.type) {
            case 'select':
              return (
                <TextField select {...commonProps}>
                  <MenuItem value="">Wszystkie</MenuItem>
                  {filter.options?.map((option) => (
                    <MenuItem key={option.value} value={option.value}>
                      {option.label}
                    </MenuItem>
                  ))}
                </TextField>
              );
            case 'datetime':
              return (
                <TextField
                  {...commonProps}
                  type="datetime-local"
                  slotProps={{
                    inputLabel: {
                      shrink: true,
                    },
                  }}
                />
              );
            case 'text':
            default:
              return <TextField {...commonProps} />;
          }
        })}
      </Stack>
    </Box>
  );
};
