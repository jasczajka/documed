import { Box, Typography } from '@mui/material';
import { FC } from 'react';

interface SingleVisitHeaderProps {
  doctorFullName: string;
  serviceTypeName: string;
  visitStatusLabel: string;
  visitStatusColor: string;
  plannedVisitStartDate?: string;
}

export const SingleVisitHeader: FC<SingleVisitHeaderProps> = ({
  doctorFullName,
  serviceTypeName,
  visitStatusLabel,
  visitStatusColor,
  plannedVisitStartDate,
}) => {
  return (
    <Box
      sx={{
        display: 'inline-block',
        width: 'fit-content',
      }}
    >
      <Typography sx={{ paddingBottom: 2 }} variant="h4">
        Wizyta
      </Typography>
      <Typography>
        Specjalista:{' '}
        <Typography component="span" fontWeight="bold">
          dr. {doctorFullName}
        </Typography>
      </Typography>
      <Typography>
        Rodzaj wizyty:{' '}
        <Typography component="span" fontWeight="bold">
          {serviceTypeName}
        </Typography>
      </Typography>
      <Typography>
        Status wizyty:{' '}
        <Typography component="span" fontWeight="bold" color={visitStatusColor}>
          {visitStatusLabel}
        </Typography>
      </Typography>
      {plannedVisitStartDate && (
        <Typography>
          Planowana data wizyty:{' '}
          <Typography component="span" fontWeight="bold">
            {plannedVisitStartDate}
          </Typography>
        </Typography>
      )}
    </Box>
  );
};
