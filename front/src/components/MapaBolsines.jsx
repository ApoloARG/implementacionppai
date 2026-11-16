import { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Tooltip, useMap } from 'react-leaflet';
import L from 'leaflet';
import { Box, Button, Typography } from '@mui/material';

// Iconos propios (los default de Leaflet se rompen con Vite)
const crearIcono = (color) =>
  L.divIcon({
    className: '',
    html: `<div style="width:28px;height:28px;border-radius:50% 50% 50% 0;background:${color};
      transform:rotate(-45deg);border:3px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,.35)"></div>`,
    iconSize: [28, 28],
    iconAnchor: [14, 28],
    popupAnchor: [0, -28],
  });
const ICONO = crearIcono('#1f4e79');
const ICONO_SELECCIONADO = crearIcono('#e07a1f');

function AjustarVista({ mapa, bolsines }) {
  const map = useMap();
  useEffect(() => {
    if (bolsines.length > 1) {
      map.fitBounds(bolsines.map((b) => [b.latitud, b.longitud]), { padding: [50, 50], maxZoom: 12 });
    } else {
      map.setView([mapa.centroLatitud, mapa.centroLongitud], mapa.zoom);
    }
  }, [map, mapa, bolsines]);
  return null;
}

export default function MapaBolsines({ mapa, bolsines, seleccionado, onSeleccionar }) {
  return (
    <Box sx={{ height: '100%', minHeight: 420, borderRadius: 2, overflow: 'hidden' }}>
      <MapContainer
        center={[mapa.centroLatitud, mapa.centroLongitud]}
        zoom={mapa.zoom}
        style={{ height: '100%', width: '100%' }}
        scrollWheelZoom
      >
        <TileLayer url={mapa.urlTeselas} attribution={mapa.atribucion} />
        <AjustarVista mapa={mapa} bolsines={bolsines} />
        {bolsines.map((b) => (
          <Marker
            key={b.numeroBolsin}
            position={[b.latitud, b.longitud]}
            icon={seleccionado === b.numeroBolsin ? ICONO_SELECCIONADO : ICONO}
          >
            <Tooltip direction="top" offset={[0, -26]}>Bolsín {b.numeroBolsin}</Tooltip>
            <Popup>
              <Typography variant="subtitle2">Bolsín {b.numeroBolsin}</Typography>
              <Typography variant="body2">Precinto: {b.numeroPrecinto}</Typography>
              <Typography variant="body2">Destino: {b.nombreCMDestino}</Typography>
              <Typography variant="body2">
                Última actualización: {b.fechaHoraUltimaActualizacion.replace('T', ' ')}
              </Typography>
              <Typography variant="body2" sx={{ mb: 1 }}>
                ({b.latitud.toFixed(4)}, {b.longitud.toFixed(4)})
              </Typography>
              <Button size="small" variant="contained" onClick={() => onSeleccionar(b.numeroBolsin)}>
                Seleccionar bolsín
              </Button>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </Box>
  );
}
