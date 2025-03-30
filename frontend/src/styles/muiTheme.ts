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
    },
    typography: {
      fontFamily: 'Roboto, sans-serif',
      allVariants: {
        color: '#3E3E3E',
      },
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
