import { CardHeader } from '@mui/material';
import SpecialistsTable from 'modules/specialists/SpecialistsTable/SpecialistsTable';
import { FC } from 'react';

const SpecialistsPage: FC = () => {
  return (
    <div className="flex flex-col">
      <CardHeader title={'Specjaliści'} />
      <SpecialistsTable />
    </div>
  );
};

export default SpecialistsPage;
