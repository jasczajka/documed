import { FC, ReactNode } from 'react';
import { Navigate } from 'react-router';

interface ProtectedRouteProps {
  element: ReactNode;
  isAllowed: boolean;
  redirectTo?: string;
}

export const ProtectedRoute: FC<ProtectedRouteProps> = ({
  element,
  isAllowed,
  redirectTo = '/',
}) => {
  return isAllowed ? <>{element}</> : <Navigate to={redirectTo} replace />;
};
