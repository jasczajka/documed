import { Delete, UploadFile } from '@mui/icons-material';
import { Box, IconButton, LinearProgress, Typography } from '@mui/material';
import { FC } from 'react';

interface FileCardProps {
  fileSize: string;
  fileName: string;
  status: 'loading' | 'error' | 'loaded';
  onDelete: () => void;
  errorMessage?: string;
}

export const FileCard: FC<FileCardProps> = ({
  fileSize,
  fileName,
  status,
  onDelete,
  errorMessage,
}) => {
  const isError = status === 'error';

  return (
    <Box
      color={isError ? 'red' : 'text.secondary'}
      sx={{
        display: 'flex',
        flexDirection: 'row',
        justifyContent: 'space-between',
        padding: 4,
        width: '100%',
      }}
    >
      <Box sx={{ display: 'flex', flexDirection: 'row' }}>
        <UploadFile
          sx={{ display: 'flex', paddingRight: 4, height: 40, width: 40, alignSelf: 'center' }}
          color={isError ? 'error' : 'primary'}
        />
        <Box>
          <Typography variant="subtitle1" color={isError ? 'error' : 'text.primary'}>
            {fileName}
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'row' }}>
            {!isError && (
              <Typography variant="body2" color={isError ? 'error' : 'text.secondary'}>
                {fileSize}
              </Typography>
            )}
            {!isError && (
              <Typography variant="body2" color={isError ? 'error' : 'text.secondary'}>
                &nbsp;&nbsp;•&nbsp;&nbsp;
              </Typography>
            )}
            <Typography variant="body2" color={isError ? 'error' : 'text.secondary'}>
              {status === 'loading'
                ? 'Ładowanie'
                : isError
                  ? errorMessage || 'Nie udało się'
                  : 'Załadowano'}
            </Typography>
          </Box>
          <LinearProgress
            sx={{
              mt: 1,
            }}
            color={isError ? 'error' : 'primary'}
            variant={status === 'loaded' || isError ? 'determinate' : 'indeterminate'}
            value={status === 'loaded' ? 100 : isError ? 0 : undefined}
          />
        </Box>
      </Box>

      <IconButton onClick={onDelete}>
        <Delete color="action" />
      </IconButton>
    </Box>
  );
};

export default FileCard;
