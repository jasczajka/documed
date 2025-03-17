import { Logout, Person, Settings } from '@mui/icons-material';
import {
  ClickAwayListener,
  Fade,
  IconButton,
  MenuItem,
  MenuList,
  Paper,
  Popper,
} from '@mui/material';
import PopupState, { bindPopper, bindToggle } from 'material-ui-popup-state';
import { FC } from 'react';
import { NavLink, Outlet } from 'react-router';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';

// @TODO replace these with a useRoles hook that will determine whether user is backoffice / admin / patient
const isAdmin = true;
const isPatient = true;

const paths = [
  { path: '/visits', label: 'Wizyty' },
  ...(!isPatient ? [{ path: '/patients', label: 'Pacjenci' }] : []),
  { path: '/specialists', label: 'Specjaliści' },
  { path: '/referrals', label: 'Skierowania' },
  { path: '/prescriptions', label: 'Recepty' },
  ...(isAdmin ? [{ path: '/admin', label: 'Administracja' }] : []),
];

export const LoggedLayout: FC = () => {
  return (
    <main className="min-h-full-device w-full">
      <header className="bg-gray relative flex h-17 items-center pl-28">
        <DocuMedLogo className="text-primary absolute left-28 h-6 w-[110px]" />
        <nav className="w-full px-60">
          <ul className="text-secondary [&>*]:hover:text-primary flex min-w-96 justify-center gap-14 [&>*]:transition-colors [&>*]:duration-500 [&>*]:ease-in-out">
            {paths.map((paths) => (
              <li key={paths.path}>
                <NavLink
                  to={paths.path}
                  className={({ isActive }) =>
                    `relative pb-2 transition-colors duration-500 ease-in-out ${
                      isActive ? 'text-primary' : 'hover:text-primary'
                    }`
                  }
                >
                  {({ isActive }) => (
                    <>
                      {paths.label}
                      <div
                        className={`bg-primary absolute bottom-0 left-0 h-[2px] rounded-full transition-all duration-500 ${
                          isActive ? 'w-full' : 'w-0 group-hover:w-full'
                        }`}
                      />
                    </>
                  )}
                </NavLink>
                <div />
              </li>
            ))}
          </ul>
        </nav>
        <div className="absolute right-11 flex items-center gap-6">
          <PopupState variant="popper" popupId="settings-popper">
            {(popupState) => (
              <>
                <IconButton {...bindToggle(popupState)} color="primary">
                  <Settings />
                </IconButton>
                <ClickAwayListener onClickAway={() => popupState.close()} mouseEvent="onMouseDown">
                  <Popper
                    {...bindPopper(popupState)}
                    transition
                    modifiers={[
                      {
                        name: 'offset',
                        options: {
                          offset: [-44, 10],
                        },
                      },
                    ]}
                  >
                    {({ TransitionProps }) => (
                      <Fade {...TransitionProps} timeout={350}>
                        <Paper className="shadow-lg">
                          <MenuList>
                            <MenuItem className="flex gap-2">
                              <Settings />
                              Ustawienia
                            </MenuItem>
                            <MenuItem className="flex gap-2">
                              <Person />
                              Konto
                            </MenuItem>
                            <MenuItem className="flex gap-2">
                              <Logout />
                              Wyloguj się
                            </MenuItem>
                          </MenuList>
                        </Paper>
                      </Fade>
                    )}
                  </Popper>
                </ClickAwayListener>
              </>
            )}
          </PopupState>
        </div>
      </header>
      <div className="h-full w-full bg-white pt-28 pl-28">
        <Outlet />
      </div>
    </main>
  );
};
