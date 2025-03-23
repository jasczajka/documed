export const readFileAsUrl = async (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();

    reader.onload = () => {
      resolve(reader.result as string);
    };

    reader.onerror = () => {
      reject(new Error('Failed to read file'));
    };

    reader.readAsDataURL(file);
  });
};

export const formatFileSize = (sizeInBytes: number): string => {
  const sizeInKB = sizeInBytes / 1024;
  const sizeInMB = sizeInKB / 1024;
  const sizeInGB = sizeInMB / 1024;

  if (sizeInGB >= 1) {
    return `${parseFloat(sizeInGB.toFixed(2))} GB`;
  }
  if (sizeInMB >= 1) {
    return `${parseFloat(sizeInMB.toFixed(2))} MB`;
  }
  if (sizeInKB >= 1) {
    return `${parseFloat(sizeInKB.toFixed(2))} KB`;
  }
  return `${sizeInBytes} B`;
};

export const formatFileName = (fileName: string): string => {
  return fileName.length > 50 ? `${fileName.slice(0, 50)}...` : fileName;
};
