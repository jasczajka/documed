import { FC, ReactNode } from 'react';

interface TestBlankPageProps {
  children: ReactNode;
}

export const TestBlankPage: FC<TestBlankPageProps> = ({ children }) => {
  return <main className="flex h-full w-dvw flex-col items-center justify-center">{children}</main>;
};
