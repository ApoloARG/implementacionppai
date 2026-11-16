import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: { main: '#1f4e79' },
    secondary: { main: '#e07a1f' },
    background: { default: '#f3f5f8' },
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: '"Inter", "Segoe UI", Roboto, Helvetica, Arial, sans-serif',
    h6: { fontWeight: 600 },
  },
});

export default theme;
