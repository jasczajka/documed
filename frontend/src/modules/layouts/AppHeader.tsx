import { Logout, Mail, Place, Settings } from '@mui/icons-material';
import {
  Avatar,
  Box,
  CircularProgress,
  ClickAwayListener,
  Fade,
  IconButton,
  ListItem,
  MenuItem,
  MenuList,
  Paper,
  Popper,
  Typography,
} from '@mui/material';
import PopupState, { bindPopper, bindToggle } from 'material-ui-popup-state';
import { memo, useMemo } from 'react';
import { Link, NavLink, useNavigate } from 'react-router';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useSitemap } from 'shared/hooks/useSitemap';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import { useShallow } from 'zustand/react/shallow';

export const AppHeader = memo(() => {
  const sitemap = useSitemap();
  const navigate = useNavigate();
  const { logout, loading, isAdmin, isPatient, isWardClerk } = useAuth();
  const { firstName, lastName, email, facilityId } = useAuthStore(
    useShallow(
      (state) => state.user ?? { firstName: '', lastName: '', email: '', facilityId: undefined },
    ),
  );
  const facilities = useFacilityStore((store) => store.facilities);
  const userName = `${firstName} ${lastName}`;
  const facilityName = useMemo(() => {
    const currentFacility = facilities.find((facility) => facility.id === facilityId);
    return `${currentFacility?.city} ${currentFacility?.address}`;
  }, [facilityId, facilities]);

  const paths = useMemo(() => {
    return [
      ...(!isAdmin ? [{ path: sitemap.visits, label: 'Wizyty' }] : []),
      ...(!isAdmin ? [{ path: sitemap.additionalServices, label: 'Usługi dod.' }] : []),
      ...(!isPatient && !isAdmin ? [{ path: sitemap.patients, label: 'Pacjenci' }] : []),
      ...(isPatient || isWardClerk ? [{ path: sitemap.specialists, label: 'Specjaliści' }] : []),
      ...(isPatient ? [{ path: sitemap.referrals, label: 'Skierowania' }] : []),
      ...(isPatient ? [{ path: sitemap.prescriptions, label: 'Recepty' }] : []),
      ...(isAdmin ? [{ path: sitemap.admin, label: 'Administracja' }] : []),
    ];
  }, [sitemap]);

  return (
    <header className="bg-gray relative flex h-17 items-center pl-28">
      <Link className="text-primary absolute left-28 h-6 w-[110px]" to={sitemap.main}>
        <DocuMedLogo />
      </Link>
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
              <ClickAwayListener
                onClickAway={() => {
                  if (!loading) popupState.close();
                }}
                mouseEvent="onMouseDown"
              >
                <Popper
                  {...bindPopper(popupState)}
                  transition
                  modifiers={[{ name: 'offset', options: { offset: [-100, 10] } }]}
                >
                  {({ TransitionProps }) => (
                    <Fade {...TransitionProps} timeout={350}>
                      <Paper className="shadow-lg">
                        <ListItem sx={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                          <Avatar />
                          <Box>
                            <Typography variant="body1">{userName}</Typography>
                            <Typography variant="body2" color="secondary">
                              <Mail fontSize="small" /> {email}
                            </Typography>
                            <Typography variant="body2" color="secondary">
                              <Place fontSize="small" />
                              {facilityName}
                            </Typography>
                          </Box>
                        </ListItem>
                        <MenuList>
                          <MenuItem
                            className="flex gap-2"
                            onClick={() => navigate(sitemap.settings)}
                          >
                            <Settings /> Ustawienia
                          </MenuItem>
                          <MenuItem className="flex gap-2" onClick={() => logout()}>
                            {loading ? <CircularProgress size={24} /> : <Logout />}Wyloguj się
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
