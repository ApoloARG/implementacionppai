import { useEffect, useMemo, useState } from 'react';
import {
  Alert, AppBar, Box, Button, Card, CardContent, Chip, CircularProgress, Container, Dialog, DialogActions,
  DialogContent, DialogContentText, DialogTitle, Divider, FormControl, InputLabel, List, ListItemButton,
  ListItemText, MenuItem, Paper, Select, Stack, TextField, Toolbar, Typography,
} from '@mui/material';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import MapIcon from '@mui/icons-material/Map';
import GpsFixedIcon from '@mui/icons-material/GpsFixed';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
import PersonIcon from '@mui/icons-material/Person';
import theme from './theme';
import MapaBolsines from './components/MapaBolsines';
import {
  cancelarCU, iniciarSesionDemo, listarUsuariosDemo, mensajeDeError, opcConsultarUbicacionBolsin,
  tomarConfirmacionEnvioCorreo, tomarSeleccionBolsinEnMapa,
} from './services/bolsinService';

const TODAS = '__TODAS__';

export default function App() {
  const [usuarios, setUsuarios] = useState([]);
  const [consulta, setConsulta] = useState(null); // respuesta de opcConsultarUbicacionBolsin
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState(null); // { flujo, mensaje }
  const [resultado, setResultado] = useState(null); // { severidad, mensaje }
  const [filtroPrecinto, setFiltroPrecinto] = useState('');
  const [filtroCMDestino, setFiltroCMDestino] = useState(TODAS);
  const [seleccionado, setSeleccionado] = useState(null);
  const [confirmacion, setConfirmacion] = useState(null); // pregunta del paso 8

  const cargarUsuarios = () => listarUsuariosDemo().then(setUsuarios).catch(() => setUsuarios([]));
  useEffect(() => { cargarUsuarios(); }, []);
  const usuarioActivo = usuarios.find((u) => u.sesionActiva);

  const reiniciar = () => {
    setConsulta(null); setError(null); setSeleccionado(null); setConfirmacion(null);
    setFiltroPrecinto(''); setFiltroCMDestino(TODAS);
  };

  // Paso 1: el EB selecciona la opcion
  const opcConsultar = async () => {
    reiniciar(); setResultado(null); setCargando(true);
    try {
      setConsulta(await opcConsultarUbicacionBolsin());
    } catch (e) {
      setError({ flujo: e?.response?.data?.flujo, mensaje: mensajeDeError(e) });
    } finally {
      setCargando(false);
    }
  };

  // Paso 6: filtrar por numero de precinto o CM destino
  const cmsDestino = useMemo(
    () => [...new Set((consulta?.bolsines || []).map((b) => b.nombreCMDestino))].sort(),
    [consulta],
  );
  const bolsinesFiltrados = useMemo(() => (consulta?.bolsines || []).filter((b) =>
    (filtroCMDestino === TODAS || b.nombreCMDestino === filtroCMDestino) &&
    (!filtroPrecinto.trim() || b.numeroPrecinto.toLowerCase().includes(filtroPrecinto.trim().toLowerCase()))),
  [consulta, filtroPrecinto, filtroCMDestino]);
  const sinCoincidenciaPrecinto = consulta && filtroPrecinto.trim() && bolsinesFiltrados.length === 0;

  // Paso 7: el EB selecciona un bolsin del mapa
  const seleccionarBolsin = async (numeroBolsin) => {
    setSeleccionado(numeroBolsin); setResultado(null);
    try {
      setConfirmacion(await tomarSeleccionBolsinEnMapa(numeroBolsin));
    } catch (e) {
      setResultado({ severidad: 'error', mensaje: mensajeDeError(e) });
    }
  };

  // Paso 9 (confirma) o A5 (no confirma)
  const responderConfirmacion = async (confirma) => {
    setConfirmacion(null);
    try {
      const r = await tomarConfirmacionEnvioCorreo(confirma);
      setResultado({ severidad: r.exito ? 'success' : 'info', mensaje: r.mensaje });
      reiniciar(); // fin del caso de uso
    } catch (e) {
      setResultado({ severidad: 'error', mensaje: mensajeDeError(e) });
    }
  };

  // A4: el EB cancela
  const cancelar = async () => {
    try { await cancelarCU(); } catch { /* sin backend igual se limpia la pantalla */ }
    reiniciar();
    setResultado({ severidad: 'info', mensaje: 'Se canceló la consulta de seguimiento de bolsines (A4).' });
  };

  const cambiarUsuario = async (nombreUsuario) => {
    await iniciarSesionDemo(nombreUsuario);
    await cargarUsuarios();
    reiniciar(); setResultado(null);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static" elevation={0}>
        <Toolbar sx={{ gap: 2 }}>
          <MapIcon />
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h6" lineHeight={1.1}>Sistema de Bolsines</Typography>
            <Typography variant="caption" sx={{ opacity: 0.85 }}>CU 36 · Consultar seguimiento de bolsines</Typography>
          </Box>
          <FormControl size="small" sx={{ minWidth: 260, bgcolor: 'rgba(255,255,255,.12)', borderRadius: 1 }}>
            <Select
              value={usuarioActivo?.nombreUsuario || ''}
              displayEmpty
              onChange={(e) => cambiarUsuario(e.target.value)}
              sx={{ color: '#fff', '.MuiSvgIcon-root': { color: '#fff' } }}
              renderValue={() => (
                <Stack direction="row" gap={1} alignItems="center">
                  <PersonIcon fontSize="small" />
                  {usuarioActivo ? `${usuarioActivo.empleado} · ${usuarioActivo.nombreCM}` : 'Sesión (demo)'}
                </Stack>
              )}
            >
              {usuarios.map((u) => (
                <MenuItem key={u.nombreUsuario} value={u.nombreUsuario}>
                  {u.empleado} — {u.nombreCM}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ py: 3 }}>
        {resultado && (
          <Alert severity={resultado.severidad} sx={{ mb: 2 }} onClose={() => setResultado(null)}>
            {resultado.mensaje}
          </Alert>
        )}

        {!consulta && (
          <Card sx={{ maxWidth: 560, mx: 'auto', mt: 6, textAlign: 'center' }}>
            <CardContent sx={{ p: 4 }}>
              <GpsFixedIcon color="primary" sx={{ fontSize: 56 }} />
              <Typography variant="h5" sx={{ mt: 1, mb: 1 }}>Seguimiento de bolsines</Typography>
              <Typography color="text.secondary" sx={{ mb: 3 }}>
                Consultá en el mapa la ubicación de los bolsines enviados por tu Comisión Médica.
              </Typography>
              <Button variant="contained" size="large" onClick={opcConsultar} disabled={cargando}
                startIcon={cargando ? <CircularProgress size={18} color="inherit" /> : <MapIcon />}>
                {cargando ? 'Consultando GPS Tracker…' : 'Consultar ubicación de bolsines'}
              </Button>
              {error && (
                <Alert severity={error.flujo === 'A1' ? 'warning' : 'error'} sx={{ mt: 3, textAlign: 'left' }}>
                  {error.mensaje}
                </Alert>
              )}
            </CardContent>
          </Card>
        )}

        {consulta && (
          <>
            <Paper sx={{ p: 2, mb: 2 }} variant="outlined">
              <Stack direction={{ xs: 'column', md: 'row' }} gap={2} alignItems={{ md: 'center' }}>
                <Box sx={{ flexGrow: 1 }}>
                  <Typography variant="overline" color="text.secondary">Comisión Médica del usuario</Typography>
                  <Typography variant="h6">{consulta.nombreCMUsuario} ({consulta.codigoCMUsuario})</Typography>
                </Box>
                <Chip icon={<GpsFixedIcon />} label={`GPS Tracker: ${consulta.modeloGPS}`} color="primary" variant="outlined" />
                <Chip label={`${consulta.bolsines.length} en el mapa`} color="success" variant="outlined" />
                <Button color="inherit" variant="outlined" onClick={cancelar}>Cancelar</Button>
              </Stack>
            </Paper>

            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '1fr 380px' }, gap: 2, height: { lg: 'calc(100vh - 230px)' } }}>
              <Paper variant="outlined" sx={{ p: 1, minHeight: 440 }}>
                <MapaBolsines mapa={consulta.mapa} bolsines={bolsinesFiltrados}
                  seleccionado={seleccionado} onSeleccionar={seleccionarBolsin} />
              </Paper>

              <Paper variant="outlined" sx={{ p: 2, overflow: 'auto' }}>
                <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 1.5 }}>Filtrar</Typography>
                <Stack gap={1.5}>
                  <TextField size="small" label="Número de precinto" value={filtroPrecinto}
                    onChange={(e) => setFiltroPrecinto(e.target.value)} placeholder="Ej: PR-000101" />
                  <FormControl size="small">
                    <InputLabel>CM destino</InputLabel>
                    <Select label="CM destino" value={filtroCMDestino} onChange={(e) => setFiltroCMDestino(e.target.value)}>
                      <MenuItem value={TODAS}>Todas</MenuItem>
                      {cmsDestino.map((cm) => <MenuItem key={cm} value={cm}>{cm}</MenuItem>)}
                    </Select>
                  </FormControl>
                </Stack>
                {sinCoincidenciaPrecinto && (
                  <Alert severity="warning" sx={{ mt: 1.5 }}>
                    No se encuentra bolsín con el número de precinto ingresado.
                  </Alert>
                )}

                <Divider sx={{ my: 2 }} />
                <Typography variant="subtitle1" fontWeight={600}>Bolsines en tránsito</Typography>
                <Typography variant="caption" color="text.secondary">Seleccioná un bolsín (acá o en el mapa)</Typography>
                <List dense>
                  {bolsinesFiltrados.map((b) => (
                    <ListItemButton key={b.numeroBolsin} selected={seleccionado === b.numeroBolsin}
                      onClick={() => seleccionarBolsin(b.numeroBolsin)} sx={{ borderRadius: 1 }}>
                      <ListItemText
                        primary={`Bolsín ${b.numeroBolsin} · ${b.numeroPrecinto}`}
                        secondary={`→ ${b.nombreCMDestino} · ${b.fechaHoraUltimaActualizacion.replace('T', ' ')}`} />
                      <MailOutlineIcon fontSize="small" color="action" />
                    </ListItemButton>
                  ))}
                </List>

                {consulta.bolsinesSinUbicacion.length > 0 && (
                  <Alert severity="warning" sx={{ mt: 1 }}>
                    El GPS Tracker no pudo informar la ubicación de:
                    <ul style={{ margin: '4px 0 0', paddingLeft: 18 }}>
                      {consulta.bolsinesSinUbicacion.map((b) => (
                        <li key={b.numeroBolsin}>Bolsín {b.numeroBolsin} ({b.numeroPrecinto}) → {b.nombreCMDestino}</li>
                      ))}
                    </ul>
                  </Alert>
                )}
              </Paper>
            </Box>
          </>
        )}
      </Container>

      {/* Paso 8: el sistema consulta si se desea enviar el correo al GCM destino */}
      <Dialog open={!!confirmacion} onClose={() => setConfirmacion(null)} maxWidth="xs" fullWidth>
        <DialogTitle>Informar ubicación del bolsín</DialogTitle>
        <DialogContent>
          <DialogContentText>{confirmacion?.pregunta}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => responderConfirmacion(false)}>No enviar</Button>
          <Button variant="contained" startIcon={<MailOutlineIcon />} onClick={() => responderConfirmacion(true)}>
            Enviar correo
          </Button>
        </DialogActions>
      </Dialog>
    </ThemeProvider>
  );
}
