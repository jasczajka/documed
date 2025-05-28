import { Suspense } from 'react';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { AppRouter } from './pages/AppRouter';

export const App = () => {
  return (
    <Suspense fallback={<FullPageLoadingSpinner />}>
      <AppRouter />
    </Suspense>
  );
};
