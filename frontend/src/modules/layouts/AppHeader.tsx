// AppHeader.tsx
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
import { memo, useMemo } from 'react';
import { NavLink } from 'react-router';
import { useSitemap } from 'shared/hooks/useSitemap';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';

export const AppHeader = memo(() => {
  const sitemap = useSitemap();

  const paths = useMemo(() => {
    // @TODO replace these with a useRoles hook that will determine whether user is backoffice / admin / patient
    const isAdmin = true;
    const isPatient = true;

    return [
      { path: sitemap.visits, label: 'Wizyty i Usługi Dodatkowe' },
      ...(!isPatient ? [{ path: sitemap.patients, label: 'Pacjenci' }] : []),
      { path: sitemap.specialists, label: 'Specjaliści' },
      { path: sitemap.referrals, label: 'Skierowania' },
      { path: sitemap.prescriptions, label: 'Recepty' },
      ...(isAdmin ? [{ path: sitemap.admin, label: 'Administracja' }] : []),
    ];
  }, [sitemap]);

  return (
    <header className="bg-gray relative flex h-17 items-center pl-28">
      <DocuMedLogo className="text-primary absolute left-28 h-6 w-[110px]" />
      <nav className="w-full px-60">
        <ul className="text-secondary [&>*]:hover:text-primary flex min-w-96 justify-center gap-14 [&>*]:transition-colors [&>*]:duration-500 [&>*]:ease-in-out">
          {paths.map(({ path, label }) => (
            <li key={path}>
              <NavLink
                to={path}
                className={({ isActive }) =>
                  `relative pb-2 transition-colors duration-500 ease-in-out ${
                    isActive ? 'text-primary' : 'hover:text-primary'
                  }`
                }
              >
                {({ isActive }) => (
                  <>
                    {label}
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
              <ClickAwayListener onClickAway={() => popupState.close} mouseEvent="onMouseDown">
                <Popper
                  {...bindPopper(popupState)}
                  transition
                  modifiers={[{ name: 'offset', options: { offset: [-44, 10] } }]}
                >
                  {({ TransitionProps }) => (
                    <Fade {...TransitionProps} timeout={350}>
                      <Paper className="shadow-lg">
                        <MenuList>
                          <MenuItem className="flex gap-2">
                            <Settings /> Ustawienia
                          </MenuItem>
                          <MenuItem className="flex gap-2">
                            <Person /> Konto
                          </MenuItem>
                          <MenuItem className="flex gap-2">
                            <Logout /> Wyloguj się
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
  );
});

AppHeader.displayName = 'AppHeader';
