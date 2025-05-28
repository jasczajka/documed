import { CircularProgress } from '@mui/material';
import { FC } from 'react';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';

export const FullPageLoadingSpinner: FC = () => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-white/80">
      <div className="flex flex-col items-center gap-4">
        <DocuMedLogo className="text-primary w-[150px]" />
        <CircularProgress color="primary" size={30} />
      </div>
    </div>
  );
};
