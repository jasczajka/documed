import { FC, ReactNode } from 'react';

interface TestBlankPageProps {
  children: ReactNode;
}

const TestBlankPage: FC<TestBlankPageProps> = ({ children }) => {
  return <main className="flex h-full w-dvw flex-col items-center justify-center">{children}</main>;
};

export default TestBlankPage;
