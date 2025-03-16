import { FC } from 'react';
import { NavLink, Outlet } from 'react-router';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';

const links = [
  { path: '/', label: 'Wizyty' },
  { path: '/', label: 'About' },
  { path: '/test', label: 'Contact' },
];

export const LoggedLayout: FC = () => {
  return (
    <main className="min-h-full-device w-full">
      <header className="bg-gray flex h-17 items-center pl-28">
        <DocuMedLogo className="text-primary h-6 w-[110px]" />
        <nav className="w-full px-60">
          <ul className="text-secondary [&>*]:hover:text-primary flex gap-14 [&>*]:transition-colors [&>*]:duration-500 [&>*]:ease-in-out">
            {links.map((link) => (
              <li key={link.path}>
                <NavLink
                  to={link.path}
                  className={({ isActive }) =>
                    `transition-colors duration-500 ease-in-out ${
                      isActive ? 'text-primary' : 'hover:text-primary'
                    }`
                  }
                >
                  {link.label}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>
      </header>
      <div className="h-full w-full bg-white">
        <Outlet />
      </div>
    </main>
  );
};
