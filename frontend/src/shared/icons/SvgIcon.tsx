import { FC } from 'react';

export interface SvgProps extends React.SVGProps<SVGSVGElement> {
  className?: string;
}

export const SvgIcon: FC<SvgProps> = ({ className = '', ...props }) => {
  return <svg className={className} {...props} />;
};

export type IconType = typeof SvgIcon;
