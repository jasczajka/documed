import { CardHeader } from '@mui/material';
import { VisitsTable } from 'modules/visits/VisitsTable/VisitsTable';
import { FC } from 'react';
import { exampleVisits } from 'shared/types/Visit';

const VisitsPage: FC = () => {
  return (
    <div className="flex flex-col gap-4">
      <CardHeader title={'Wizyty i Usługi Dodatkowe'} />
      <VisitsTable visits={exampleVisits} />
    </div>
  );
};

export default VisitsPage;
