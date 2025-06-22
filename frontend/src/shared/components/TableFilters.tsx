import { Autocomplete, Box, Button, Stack, TextField } from '@mui/material';
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
  const handleInputChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      onFilterChange(name as keyof T, value);
    },
    [onFilterChange],
  );

  const handleSelectChange = useCallback(
    (name: keyof T) => (_event: unknown, value: { value: string; label: string } | null) => {
      onFilterChange(name, value?.value || '');
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
            label: filter.label,
            name: filter.name,
            value: filters[filter.name] || '',
            size: 'small' as const,
            sx: { minWidth: filter.width || 200 },
          };

          switch (filter.type) {
            case 'select': {
              const sortedOptions = [...(filter.options || [])].sort((a, b) =>
                a.label.localeCompare(b.label),
              );
              const allOptions = [{ value: '', label: 'Wszystkie' }, ...sortedOptions];
              const currentValue = allOptions.find(
                (option) => option.value === filters[filter.name],
              );

              return (
                <Autocomplete
                  key={filter.name}
                  options={allOptions}
                  getOptionLabel={(option) => option.label}
                  onChange={handleSelectChange(filter.name)}
                  value={currentValue || null}
                  renderInput={(params) => <TextField {...params} {...commonProps} fullWidth />}
                  slotProps={{
                    popper: { style: { width: 'fit-content' } },
                  }}
                />
              );
            }
            case 'datetime':
              return (
                <TextField
                  key={filter.name}
                  {...commonProps}
                  type="datetime-local"
                  onChange={handleInputChange}
                  slotProps={{
                    inputLabel: {
                      shrink: true,
                    },
                  }}
                />
              );
            case 'text':
            default:
              return <TextField key={filter.name} {...commonProps} onChange={handleInputChange} />;
          }
        })}
      </Stack>
    </Box>
  );
};
