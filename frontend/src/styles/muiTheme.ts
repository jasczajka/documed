import { plPL as plPLCore } from '@mui/material/locale';
import { createTheme } from '@mui/material/styles';
import { plPL } from '@mui/x-data-grid/locales';

export const theme = createTheme(
  {
    spacing: 4,
    palette: {
      primary: {
        main: '#04615C',
        light: '#378D86',
        dark: '#033E3B',
        contrastText: '#ffffff',
      },
      secondary: {
        main: 'rgba(0, 0, 0, 0.6)',
      },
    },
    typography: {
      fontFamily: 'Roboto, sans-serif',
    },
    components: {
      MuiSvgIcon: {
        defaultProps: {
          color: 'inherit',
        },
        styleOverrides: {
          root: ({ ownerState }) => ({
            color: ownerState.color === 'inherit' ? '#3E3E3E' : undefined,
          }),
        },
      },
    },
  },
  plPLCore,
  plPL,
);
