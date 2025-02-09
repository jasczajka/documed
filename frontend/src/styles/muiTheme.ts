import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#04615C',
      light: '#378D86',
      dark: '#033E3B',
      contrastText: '#ffffff',
    },
  },
  typography: {
    fontFamily: 'Inter, sans-serif',
  },
});
