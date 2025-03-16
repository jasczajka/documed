import { Button, Paper } from '@mui/material';
import { createBrowserRouter, RouteObject, RouterProvider } from 'react-router';
import { LoggedLayout } from '../modules/layouts/LoggedLayout';

const defaultRoutes: RouteObject[] = [
  {
    path: '/',
    element: <LoggedLayout />,
    children: [
      {
        path: '/',
        element: (
          <div className="flex h-full w-full items-center justify-center">
            <Paper>
              <Button>recepta na medyczną dsad dijas tutaj!!!</Button>
            </Paper>
          </div>
        ),
      },
      {
        path: '/test',
        element: (
          <div className="flex h-full w-full items-center justify-center">
            <Paper>
              <Button>recepta na medyczną dsad dijas tutaj!!!</Button>
            </Paper>
          </div>
        ),
      },
    ],
  },
];

export const AppRouter = () => {
  const router = createBrowserRouter(defaultRoutes);
  return <RouterProvider router={router} />;
};
