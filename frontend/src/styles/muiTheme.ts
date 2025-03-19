import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
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
      styleOverrides: {
        root: {
          color: '#3E3E3E',
        },
      },
    },
  },
});
