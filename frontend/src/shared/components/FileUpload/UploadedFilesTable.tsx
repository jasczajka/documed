import { Download } from '@mui/icons-material';
import { Card } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';
import { FC } from 'react';
import { formatFileName } from './utils';

export interface FilePreview {
  id: number;
  fileName: string;
  downloadUrl: string;
}

interface UploadedFilesTableProps {
  files: FilePreview[];
  className?: string;
}

export const UploadedFilesTable: FC<UploadedFilesTableProps> = ({ files, className }) => {
  const columns: GridColDef<FilePreview>[] = [
    {
      field: 'fileName',
      headerName: 'Nazwa pliku',
      flex: 1,
      valueGetter: (_, row) => formatFileName(row.fileName),
    },
    {
      field: 'actions',
      headerName: 'Akcje',
      type: 'actions',
      width: 100,
      getActions: (params: { row: FilePreview }) => [
        <GridActionsCellItem
          key="download"
          icon={<Download />}
          onClick={() => window.open(params.row.downloadUrl, '_blank')}
          label="Download"
        />,
      ],
    },
  ];

  return (
    <Card className={className} sx={{ width: '100%' }}>
      <DataGrid
        rows={files}
        columns={columns}
        initialState={{
          pagination: {
            paginationModel: { page: 0, pageSize: 5 },
          },
        }}
        pageSizeOptions={[5, 10, 25]}
        disableRowSelectionOnClick
        disableColumnMenu
        hideFooterSelectedRowCount
      />
    </Card>
  );
};
