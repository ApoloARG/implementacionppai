import axios from 'axios';

const API = '/api/consultar-ubicacion-bolsin';
const DEMO = '/api/demo';

// Cada funcion representa un evento del actor sobre la PantallaConsultarUbicacionBolsin
export const opcConsultarUbicacionBolsin = () => axios.post(`${API}/opcion`).then((r) => r.data);

export const tomarSeleccionBolsinEnMapa = (numeroBolsin) =>
  axios.post(`${API}/seleccionar-bolsin`, { numeroBolsin }).then((r) => r.data);

export const tomarConfirmacionEnvioCorreo = (confirma) =>
  axios.post(`${API}/confirmar-envio-correo`, { confirma }).then((r) => r.data);

export const cancelarCU = () => axios.post(`${API}/cancelar`).then((r) => r.data);

// Soporte de la demo (cambiar el usuario logueado)
export const listarUsuariosDemo = () => axios.get(`${DEMO}/usuarios`).then((r) => r.data);
export const iniciarSesionDemo = (nombreUsuario) =>
  axios.post(`${DEMO}/sesion`, { nombreUsuario }).then((r) => r.data);

export const mensajeDeError = (error) =>
  error?.response?.data?.mensaje ||
  (error?.response ? `Error ${error.response.status}` : 'No se pudo conectar con el servidor (¿está levantado el backend en el 8081?)');
