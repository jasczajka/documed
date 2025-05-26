import { CardHeader } from '@mui/material';
import { FC } from 'react';

const VisitsPage: FC = () => {
  return (
    <div className="flex flex-col">
      <CardHeader title={'Wizyty i Usługi Dodatkowe'} />
      {/* <VisitsTable visits={exampleVisits} /> */}
    </div>
  );
};

export default VisitsPage;
