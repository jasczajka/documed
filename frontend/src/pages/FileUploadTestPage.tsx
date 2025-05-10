import { FileUpload } from 'shared/components/FileUpload/FileUpload';
import { useFileUpload } from 'shared/hooks/useFileUpload';

const FileUploadTestPage = () => {
  const { uploadFile } = useFileUpload();
  return (
    <main className="flex h-full w-dvw flex-col items-center justify-center">
      <FileUpload onConfirmUpload={(file) => uploadFile(file)} className="m-12" />
    </main>
  );
};

export default FileUploadTestPage;
