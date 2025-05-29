import { Box, Typography } from '@mui/material';
import { FC } from 'react';

export interface PatientInfoPanelProps {
  patientId: number;
  patientFullName: string;
  patientAge: number | null;
}

export const PatientInfoPanel: FC<PatientInfoPanelProps> = ({
  patientId,
  patientFullName,
  patientAge,
}) => {
  return (
    <Box
      sx={{
        display: 'inline-block',
        px: 3,
        py: 2,
        border: 1,
        borderRadius: 1,
        borderColor: 'primary.light',
        width: 'fit-content',
      }}
    >
      <Typography>
        Pacjent #<strong>{patientId}</strong>
      </Typography>
      <Typography>
        Imię i nazwisko: <strong>{patientFullName}</strong>
      </Typography>
      <Typography>
        Wiek: <strong>{patientAge ?? ''}</strong>
      </Typography>
    </Box>
  );
};
