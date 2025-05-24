import { Typography } from '@mui/material';
import { FC } from 'react';

interface PatientInfoPanelProps {
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
    <Typography
      sx={{
        width: '100%',
        px: '12px',
        py: '8px',
        border: 1,
        borderRadius: '4px',
        borderColor: 'primary.light',
      }}
    >
      <span>Pacjent nr: </span>
      <span className="font-bold">{patientId}</span>
      <br />
      <span>Imię i nazwisko: </span>
      <span className="font-bold">{patientFullName}</span>
      <br />
      <span>Wiek: </span>
      <span className="font-bold">{patientAge ?? ''}</span>
    </Typography>
  );
};
