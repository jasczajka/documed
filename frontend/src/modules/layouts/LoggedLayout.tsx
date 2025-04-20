import { FC, memo } from 'react';
import { Outlet } from 'react-router';
import { ModalContainer } from 'shared/components/Modal/ModalContainer';
import { AppHeader } from './AppHeader';

export const LoggedLayoutComponent: FC = () => {
  return (
    <>
      <ModalContainer />
      <main className="min-h-full-device w-full min-w-[1440px]">
        <AppHeader />
        <div className="h-full w-full bg-white px-28 pt-4">
          <Outlet />
        </div>
      </main>
    </>
  );
};

export const LoggedLayout = memo(LoggedLayoutComponent);
LoggedLayout.displayName = 'LoggedLayout';
