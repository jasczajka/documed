import { UploadFile } from '@mui/icons-material';
import { Box, Card, CardHeader, Typography } from '@mui/material';
import { FC, useCallback, useEffect, useMemo, useState } from 'react';
import { ErrorCode, FileError, FileRejection, useDropzone } from 'react-dropzone';
import { FileInfoDTO } from 'shared/api/generated/generated.schemas';
import FileCard from './FileCard';
import { formatFileName, formatFileSize, readFileAsUrl } from './utils';

const ACCEPT_FILE_TYPES = {}; // extend if needed
const MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB
const MAX_FILE_COUNT = 5;

const getAcceptedExtensions = (fileTypes: Record<string, any>) => {
  return Object.keys(fileTypes)
    .map((mimeType) => `.${mimeType.split('/')[1]}`)
    .join(', ');
};

const getPlErrorMessage = (error: FileError) => {
  switch (error.code) {
    case ErrorCode.FileInvalidType:
      return `Akceptowane formaty plików: ${getAcceptedExtensions(ACCEPT_FILE_TYPES)}`;
    case ErrorCode.FileTooLarge:
      return 'Plik jest za duży';
    case ErrorCode.FileTooSmall:
      return 'Plik jest za mały';
    case ErrorCode.TooManyFiles:
      return `Maksymalna ilość plików to ${MAX_FILE_COUNT}`;
    default:
      return error.message;
  }
};

const getPlFileRejections = (fileRejections: FileRejection[]) => {
  return fileRejections.map((reject) => ({
    file: reject.file,
    errors: reject.errors.map((error) => ({
      code: error.code,
      message: getPlErrorMessage(error),
    })),
  }));
};

interface TrackedFile {
  file: File;
  id?: number;
  status: 'loading' | 'error' | 'loaded' | 'uploaded';
  downloadUrl?: string;
  size?: number;
  errors?: string[];
}

interface FileUploadProps {
  title?: string;
  onConfirmUpload: (file: File) => Promise<{ downloadUrl: string; fileId: number }>;
  onDeleteUploaded: (fileId: number) => void;
  onHasUnuploadedFiles?: (hasUnuploadedFiles: boolean) => void;
  className?: string;
  uploadFileLoading?: boolean;
  disabled?: boolean;
  initialFiles?: FileInfoDTO[];
}

export const FileUpload: FC<FileUploadProps> = ({
  title = 'Załączniki',
  onConfirmUpload,
  onDeleteUploaded,
  onHasUnuploadedFiles,
  className,
  uploadFileLoading,
  disabled = false,
  initialFiles,
}) => {
  const [acceptedFiles, setAcceptedFiles] = useState<TrackedFile[]>([]);
  const [rejectedFiles, setRejectedFiles] = useState<TrackedFile[]>([]);

  const dropZoneDisabled = useMemo(
    () => acceptedFiles.length >= MAX_FILE_COUNT || disabled,
    [acceptedFiles, disabled],
  );

  const hasUnuploadedFiles = useMemo(() => {
    return acceptedFiles.some((file) => file.status === 'loaded' && !file.id);
  }, [acceptedFiles]);

  const isEmpty = !acceptedFiles.length && !rejectedFiles.length;

  const { getRootProps, getInputProps } = useDropzone({
    accept: ACCEPT_FILE_TYPES,
    maxSize: MAX_FILE_SIZE,
    disabled: dropZoneDisabled,
    onDrop: async (accepted: File[], rejected: FileRejection[]) => {
      const remainingSlots = MAX_FILE_COUNT - acceptedFiles.length;

      if (remainingSlots <= 0) {
        return;
      }

      const filesToAdd = accepted.slice(0, remainingSlots).map((file) => ({
        file,
        status: 'loading' as const,
      }));

      setAcceptedFiles((prev) => [...prev, ...filesToAdd]);

      for (const trackedFile of filesToAdd) {
        try {
          await readFileAsUrl(trackedFile.file);
          setAcceptedFiles((prev) =>
            prev.map((f) => (f.file === trackedFile.file ? { ...f, status: 'loaded' } : f)),
          );
        } catch {
          setAcceptedFiles((prev) => prev.filter((f) => f.file !== trackedFile.file));
          setRejectedFiles((prev) => [
            ...prev,
            { file: trackedFile.file, status: 'error', errors: ['Failed to load file'] },
          ]);
        }
      }

      const formattedRejections = getPlFileRejections(rejected);
      setRejectedFiles((prev) => [
        ...prev,
        ...formattedRejections.map(({ file, errors }) => ({
          file,
          status: 'error' as const,
          errors: errors.map((e) => e.message),
        })),
      ]);
    },
  });

  const handleDeleteAcceptedFile = useCallback(
    (fileToDelete: TrackedFile) => {
      setAcceptedFiles((prev) =>
        prev.map((f) => (f === fileToDelete ? { ...f, status: 'loading' } : f)),
      );
      if (fileToDelete.id) {
        onDeleteUploaded(fileToDelete.id);
      }
      setAcceptedFiles((prev) => prev.filter((f) => f.file !== fileToDelete.file));
    },
    [setAcceptedFiles],
  );

  const handleDeleteRejectedFile = useCallback(
    (fileToDelete: File) => {
      setRejectedFiles((prev) => prev.filter((f) => f.file !== fileToDelete));
    },
    [rejectedFiles],
  );

  const handleConfirmUpload = async (fileToUpload: File) => {
    try {
      setAcceptedFiles((prev) =>
        prev.map((f) => (f.file === fileToUpload ? { ...f, status: 'loading' } : f)),
      );
      const data = await onConfirmUpload(fileToUpload);
      setAcceptedFiles((prev) =>
        prev.map((f) =>
          f.file === fileToUpload
            ? { ...f, status: 'uploaded', downloadUrl: data.downloadUrl, id: data.fileId }
            : f,
        ),
      );
    } catch (e) {
      console.error('error uploading files: ', e);
      setAcceptedFiles((prev) =>
        prev.map((f) =>
          f.file === fileToUpload
            ? { ...f, status: 'error', errors: ['Błąd podczas przesyłania do systemu'] }
            : f,
        ),
      );
    }
  };

  useEffect(() => {
    if (onHasUnuploadedFiles) {
      onHasUnuploadedFiles(hasUnuploadedFiles);
    }
  }, [hasUnuploadedFiles]);

  useEffect(() => {
    if (initialFiles) {
      const preloaded = initialFiles.map((f) => ({
        file: new File([], f.fileName),
        id: f.id,
        downloadUrl: f.downloadUrl,
        status: 'uploaded' as const,
        size: f.sizeBytes,
      }));
      setAcceptedFiles(preloaded);
    }
  }, [initialFiles]);

  return (
    <Card className={className} sx={{ width: '100%', maxWidth: 600 }}>
      <CardHeader title={<Typography variant="body2">{title}</Typography>} />
      {!disabled && (
        <Box
          {...getRootProps()}
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 2,
            border: '1px dashed #D3D3D3',
            padding: 4,
            mb: isEmpty ? 2 : 0,
            textAlign: 'center',
            cursor: dropZoneDisabled ? 'not-allowed' : 'pointer',
            '&:hover': {
              backgroundColor: dropZoneDisabled ? undefined : 'rgba(0, 0, 0, 0.05)',
            },
          }}
        >
          <input {...getInputProps()} disabled={acceptedFiles.length >= MAX_FILE_COUNT} />
          <UploadFile color="primary" />
          <Typography variant="subtitle1">Kliknij lub przeciągnij i upuść</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Maksymalnie {MAX_FILE_COUNT} plików o rozmiarze max. {formatFileSize(MAX_FILE_SIZE)}
          </Typography>
        </Box>
      )}

      <Box>
        {acceptedFiles.map((file) => (
          <FileCard
            key={`${file.file.name} - ${file.id}`}
            fileName={formatFileName(file.file.name)}
            fileSize={
              file.file.size === 0 && file.size
                ? formatFileSize(file.size)
                : formatFileSize(file.file.size)
            }
            status={file.status}
            downloadUrl={file.downloadUrl}
            onDelete={() => handleDeleteAcceptedFile(file)}
            onConfirmUpload={() => handleConfirmUpload(file.file)}
            loading={uploadFileLoading}
            disabled={disabled}
          />
        ))}
      </Box>
      <Box>
        {rejectedFiles.map(({ file, errors }) => (
          <FileCard
            key={file.name}
            fileName={formatFileName(file.name)}
            fileSize={formatFileSize(file.size)}
            status="error"
            errorMessage={errors?.join(', ')}
            onDelete={() => handleDeleteRejectedFile(file)}
            loading={uploadFileLoading}
            disabled={disabled}
          />
        ))}
      </Box>
    </Card>
  );
};
