import { FC } from 'react';
import { FilePreview, UploadedFilesTable } from 'shared/components/FileUpload/UploadedFilesTable';

interface AttachmentsTabProps {
  attachments: FilePreview[];
}

export const AttachmentsTab: FC<AttachmentsTabProps> = ({ attachments }) => {
  return <UploadedFilesTable files={attachments} />;
};
