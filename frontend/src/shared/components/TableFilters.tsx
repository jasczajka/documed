import { Box, Button, MenuItem, Stack, TextField } from '@mui/material';
import { ChangeEvent } from 'react';

export type FilterConfig = {
  name: string;
  label: string;
  type: 'text' | 'select' | 'date';
  options?: { value: string; label: string }[];
  width?: number | string;
};

interface TableFiltersProps<T extends Record<string, string>> {
  filters: T;
  filterConfig: FilterConfig[];
  onFilterChange: (name: keyof T, value: string) => void;
  onReset: () => void;
  resultsCount: number;
}

export const TableFilters = <T extends Record<string, string>>({
  filters,
  filterConfig,
  onFilterChange,
  onReset,
}: TableFiltersProps<T>) => {
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    onFilterChange(name as keyof T, value);
  };

  return (
    <Box sx={{ mb: 2 }}>
      <Button variant="outlined" onClick={onReset} sx={{ mr: 2 }}>
        Wyczyść filtry
      </Button>
      <Stack direction="row" spacing={2} sx={{ mb: 2 }} flexWrap="wrap">
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
                  <MenuItem value="">All</MenuItem>
                  {filter.options?.map((option) => (
                    <MenuItem key={option.value} value={option.value}>
                      {option.label}
                    </MenuItem>
                  ))}
                </TextField>
              );
            case 'date':
              return (
                <TextField
                  {...commonProps}
                  type="date"
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
