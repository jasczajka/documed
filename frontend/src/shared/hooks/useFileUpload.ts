import {
  useCompleteUpload,
  useDeleteFile,
  useGenerateUploadUrl,
} from 'shared/api/generated/attachment-controller/attachment-controller';

export const useFileUpload = () => {
  const {
    mutateAsync: generateUploadUrl,
    isPending: isGenerateUploadUrlLoading,
    error: uploadUrlError,
  } = useGenerateUploadUrl();

  const {
    mutateAsync: completeUpload,
    isPending: isCompleteUploadLoading,
    error: completeUploadError,
  } = useCompleteUpload();

  const {
    mutateAsync: deleteFile,
    isPending: isDeleteFileLoading,
    error: deleteFileError,
  } = useDeleteFile();

  const uploadFile = async (
    file: File,
    visitId?: number,
    additionalServiceId?: number,
  ): Promise<{ downloadUrl: string; fileId: number }> => {
    const { name: fileName, size: fileSizeBytes } = file;

    const uploadUrlResponse = await generateUploadUrl({
      data: {
        fileName,
        fileSizeBytes,
        visitId,
        additionalServiceId,
      },
    });

    // calling the presigned PUT to s3 link directly
    await fetch(uploadUrlResponse.uploadUrl, {
      method: 'PUT',
      body: file,
    });

    const downloadUrl = await completeUpload({
      data: {
        attachmentId: uploadUrlResponse.attachmentId,
        s3Key: uploadUrlResponse.s3Key,
      },
    });
    return { downloadUrl, fileId: uploadUrlResponse.attachmentId };
  };

  return {
    uploadFile,
    deleteFile,
    isLoading: isGenerateUploadUrlLoading || isCompleteUploadLoading || isDeleteFileLoading,
    uploadError: uploadUrlError || completeUploadError || deleteFileError,
  };
};
