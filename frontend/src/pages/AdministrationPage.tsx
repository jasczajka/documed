import { CardHeader } from '@mui/material';
import { AdminTabs } from 'modules/admin/AdminTabs';
import { FC } from 'react';

const AdministrationPage: FC = () => {
  return (
    <div className="flex flex-col">
      <CardHeader title={'Administracja'} />
      <AdminTabs />
    </div>
  );
};

export default AdministrationPage;
