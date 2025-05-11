import { Delete, Upload, UploadFile } from '@mui/icons-material';
import { Box, IconButton, LinearProgress, Link, Typography } from '@mui/material';
import { FC } from 'react';

interface FileCardProps {
  fileSize: string;
  fileName: string;
  status: 'loading' | 'error' | 'loaded' | 'uploaded';
  onDelete: () => void;
  onConfirmUpload?: () => Promise<void>;
  downloadUrl?: string;
  errorMessage?: string;
  loading?: boolean;
}

export const FileCard: FC<FileCardProps> = ({
  fileSize,
  fileName,
  status,
  onDelete,
  onConfirmUpload,
  downloadUrl,
  errorMessage,
  loading,
}) => {
  const isError = status === 'error';
  const isSuccessfullyUploaded = status === 'uploaded';
  const isSuccessfullyLoaded = status === 'loaded';
  const isLoading = status === 'loading';

  const getIconColor = () => {
    if (isSuccessfullyUploaded) {
      return 'success';
    }
    if (isError) {
      return 'error';
    }
    return 'primary';
  };

  const getStatusLabel = () => {
    if (isSuccessfullyLoaded) {
      return 'Plik gotowy do załadowania';
    }
    if (isSuccessfullyUploaded) {
      return 'Udało się załadować plik';
    }
    if (isLoading) {
      return 'Ładowanie';
    }
    if (isError) {
      return errorMessage ?? 'Wystąpił błąd';
    }
  };

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
          color={getIconColor()}
        />
        <Box>
          <Typography variant="subtitle1" color={isError ? 'error' : 'text.primary'}>
            {downloadUrl ? (
              <Link href={downloadUrl} target="_blank" rel="noopener noreferrer" underline="hover">
                {fileName}
              </Link>
            ) : (
              fileName
            )}
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
              {getStatusLabel()}
            </Typography>
          </Box>
          <LinearProgress
            sx={{
              mt: 1,
            }}
            color={getIconColor()}
            variant={
              isSuccessfullyLoaded || isSuccessfullyUploaded || isError
                ? 'determinate'
                : 'indeterminate'
            }
            value={isSuccessfullyLoaded || isSuccessfullyUploaded ? 100 : isError ? 0 : undefined}
          />
        </Box>
      </Box>
      <Box>
        {!isLoading && (
          <IconButton onClick={onDelete} disabled={loading}>
            <Delete color="action" />
          </IconButton>
        )}
        {isSuccessfullyLoaded && (
          <IconButton onClick={onConfirmUpload} loading={loading} disabled={loading}>
            <Upload color="action" />
          </IconButton>
        )}
      </Box>
    </Box>
  );
};

export default FileCard;
