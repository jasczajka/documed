import { Box, Card, CardHeader, Typography } from '@mui/material';
import { FC } from 'react';
import FileCard from './FileCard';
import { formatFileName, formatFileSize } from './utils';

interface FilePreview {
  fileName: string;
  fileSize?: number;
  downloadUrl: string;
}

interface ReadOnlyFileUploadProps {
  files: FilePreview[];
  className?: string;
}

export const ReadOnlyFileUpload: FC<ReadOnlyFileUploadProps> = ({ files, className }) => {
  return (
    <Card className={className} sx={{ width: '100%', maxWidth: 600 }}>
      <CardHeader title={<Typography variant="body2">Załączniki</Typography>} />
      <Box>
        {files.map((file) => (
          <FileCard
            key={file.fileName}
            fileName={formatFileName(file.fileName)}
            fileSize={file.fileSize ? formatFileSize(file.fileSize) : 'Błąd odczytu rozmiaru'}
            status="uploaded"
            downloadUrl={file.downloadUrl}
            onDelete={() => {}}
          />
        ))}
      </Box>
    </Card>
  );
};
