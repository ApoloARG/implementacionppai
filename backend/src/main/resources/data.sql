-- =====================================================================
-- Datos de prueba del CU 36 "Consultar seguimiento de bolsines"
-- Los codigos de CM y los bolsines 101..110 coinciden con los datos del
-- mock del GPS Tracker de la catedra (dsi-api-bolsines), si no la API
-- responde 404 por "codigo de comision no coincide".
-- =====================================================================

-- ---------- COMISIONES MEDICAS ----------
INSERT INTO comisiones_medicas (codigo, nombre, direccion, email, telefono) VALUES
('CM-CBA-01', 'Comisión Médica Córdoba',       'Av. Colón 1250, Córdoba',            'cm.cordoba@cm.gob.ar',      '0351-4221100'),
('CM-CBA-02', 'Comisión Médica Villa María',   'Bv. España 420, Villa María',        'cm.villamaria@cm.gob.ar',   '0353-4523300'),
('CM-ROS-02', 'Comisión Médica Rosario',       'Córdoba 1452, Rosario',              'cm.rosario@cm.gob.ar',      '0341-4405500'),
('CM-MDZ-03', 'Comisión Médica Mendoza',       'San Martín 1100, Mendoza',           'cm.mendoza@cm.gob.ar',      '0261-4298800'),
('CM-BA-01',  'Comisión Médica Central',       'Av. Belgrano 1400, C.A.B.A.',        'cm.central@cm.gob.ar',      '011-43456700'),
('CM-TUC-01', 'Comisión Médica Tucumán',       '24 de Septiembre 650, Tucumán',      'cm.tucuman@cm.gob.ar',      '0381-4224400'),
('CM-SLA-01', 'Comisión Médica Salta',         'Caseros 780, Salta',                 'cm.salta@cm.gob.ar',        '0387-4311200'),
('CM-JUJ-01', 'Comisión Médica Jujuy',         'Belgrano 920, San Salvador de Jujuy','cm.jujuy@cm.gob.ar',        '0388-4227700'),
('CM-NQN-01', 'Comisión Médica Neuquén',       'Av. Argentina 300, Neuquén',         'cm.neuquen@cm.gob.ar',      '0299-4483300'),
('CM-MDQ-01', 'Comisión Médica Mar del Plata', 'Av. Independencia 2100, Mar del Plata','cm.mardelplata@cm.gob.ar','0223-4951100'),
('CM-LPL-01', 'Comisión Médica La Plata',      'Calle 7 N° 1050, La Plata',          'cm.laplata@cm.gob.ar',      '0221-4227800');

-- ---------- ROLES ----------
INSERT INTO roles (nombre, descripcion) VALUES
('Encargado de Bolsines',      'Arma, envía y recibe los bolsines de la CM'),
('Gerente de Comision Medica', 'Gerente responsable de la Comisión Médica');

-- ---------- USUARIOS ----------
INSERT INTO usuarios (nombre, contrasena) VALUES
('lperez',      '1234'),
('rdiaz',       '1234'),
('afernandez',  '1234'),
('jsosa',       '1234');

-- ---------- EMPLEADOS ----------
-- Encargados de Bolsines (tienen usuario)
INSERT INTO empleados (legajo, apellido, nombre, email, usuario_nombre, rol_nombre, cm_codigo) VALUES
(1001, 'Pérez',     'Lucía',   'lucia.perez@cm.gob.ar',     'lperez',     'Encargado de Bolsines', 'CM-CBA-01'),
(1002, 'Díaz',      'Ramiro',  'ramiro.diaz@cm.gob.ar',     'rdiaz',      'Encargado de Bolsines', 'CM-ROS-02'),
(1003, 'Fernández', 'Ana',     'ana.fernandez@cm.gob.ar',   'afernandez', 'Encargado de Bolsines', 'CM-TUC-01'),
(1004, 'Sosa',      'Julián',  'julian.sosa@cm.gob.ar',     'jsosa',      'Encargado de Bolsines', 'CM-LPL-01');

-- Gerentes de Comisión Médica (GCM) - destinatarios del correo del CU 31
INSERT INTO empleados (legajo, apellido, nombre, email, usuario_nombre, rol_nombre, cm_codigo) VALUES
(2001, 'Gómez',     'Martín',   'gerente.cordoba@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-CBA-01'),
(2002, 'Ruiz',      'Carla',    'gerente.villamaria@cm.gob.ar',  NULL, 'Gerente de Comision Medica', 'CM-CBA-02'),
(2003, 'Molina',    'Pablo',    'gerente.rosario@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-ROS-02'),
(2004, 'Ortiz',     'Valeria',  'gerente.mendoza@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-MDZ-03'),
(2005, 'Romero',    'Diego',    'gerente.central@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-BA-01'),
(2006, 'Herrera',   'Silvina',  'gerente.tucuman@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-TUC-01'),
(2007, 'Castro',    'Hernán',   'gerente.salta@cm.gob.ar',       NULL, 'Gerente de Comision Medica', 'CM-SLA-01'),
(2008, 'Medina',    'Paula',    'gerente.jujuy@cm.gob.ar',       NULL, 'Gerente de Comision Medica', 'CM-JUJ-01'),
(2009, 'Acosta',    'Federico', 'gerente.neuquen@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-NQN-01'),
(2010, 'Luna',      'Mariana',  'gerente.mardelplata@cm.gob.ar', NULL, 'Gerente de Comision Medica', 'CM-MDQ-01'),
(2011, 'Benítez',   'Gustavo',  'gerente.laplata@cm.gob.ar',     NULL, 'Gerente de Comision Medica', 'CM-LPL-01');
-- (rdiaz es EB de Rosario y NO es gerente: el loop de obtenerGerenteDeCM() lo descarta por sosGerente())

-- ---------- SESION ACTUAL (sin fecha de fin) ----------
INSERT INTO sesiones (fecha_hora_inicio, fecha_hora_fin, usuario_nombre) VALUES
('2026-03-07 08:00:00', '2026-03-07 08:30:00', 'rdiaz'),
('2026-03-07 09:00:00', NULL,                  'lperez');

-- ---------- ESTADOS (ambito Bolsin) ----------
INSERT INTO estados (id, ambito, nombre, descripcion) VALUES
(1, 'Bolsin', 'Creado',   'Bolsín registrado, todavía sin cerrar'),
(2, 'Bolsin', 'Cerrado',  'Bolsín cerrado con precinto'),
(3, 'Bolsin', 'Enviado',  'Bolsín retirado por el correo, en tránsito'),
(4, 'Bolsin', 'Recibido', 'Bolsín recibido en la CM destino');

-- ---------- BOLSINES ----------
-- 101..110: existen en el mock del GPS Tracker (mismo origen y destino)
INSERT INTO bolsines (numero_bolsin, fecha, numero_precinto, peso, cm_origen_codigo, cm_destino_codigo) VALUES
(101, '2026-03-06', 'PR-000101', 1250.0, 'CM-CBA-01', 'CM-ROS-02'),
(102, '2026-03-06', 'PR-000102',  980.0, 'CM-ROS-02', 'CM-MDZ-03'),
(103, '2026-03-06', 'PR-000103', 1430.0, 'CM-MDZ-03', 'CM-BA-01'),
(104, '2026-03-06', 'PR-000104',  760.0, 'CM-BA-01',  'CM-CBA-01'),
(105, '2026-03-06', 'PR-000105', 1100.0, 'CM-TUC-01', 'CM-SLA-01'),
(106, '2026-03-06', 'PR-000106',  890.0, 'CM-SLA-01', 'CM-JUJ-01'),
(107, '2026-03-06', 'PR-000107', 1320.0, 'CM-NQN-01', 'CM-MDQ-01'),
(108, '2026-03-06', 'PR-000108', 1010.0, 'CM-MDQ-01', 'CM-BA-01'),
(109, '2026-03-06', 'PR-000109',  950.0, 'CM-LPL-01', 'CM-CBA-01'),
(110, '2026-03-06', 'PR-000110', 1180.0, 'CM-CBA-02', 'CM-ROS-02');
-- 111..114: solo existen en nuestro sistema (no en el mock del GPS)
INSERT INTO bolsines (numero_bolsin, fecha, numero_precinto, peso, cm_origen_codigo, cm_destino_codigo) VALUES
(111, '2026-03-06', 'PR-000111', 1040.0, 'CM-CBA-01', 'CM-MDZ-03'),  -- Enviado, el GPS no lo conoce -> A3
(112, '2026-03-06', 'PR-000112',  870.0, 'CM-CBA-01', 'CM-BA-01'),   -- Enviado, el GPS no lo conoce -> A3
(113, '2026-03-07', 'PR-000113',  640.0, 'CM-CBA-01', 'CM-TUC-01'),  -- Creado  -> no se consulta
(114, '2026-03-01', 'PR-000114', 1500.0, 'CM-CBA-01', 'CM-ROS-02');  -- Recibido -> no se consulta

-- ---------- CAMBIOS DE ESTADO ----------
-- Bolsines enviados: Creado (cerrado) -> Enviado (actual, sin fecha de fin)
INSERT INTO cambios_estado_bolsin (id, fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_legajo, bolsin_numero) VALUES
(1,  '2026-03-06 09:00:00', '2026-03-07 07:30:00', 1, 1001, 101),
(2,  '2026-03-07 07:30:00', NULL,                  3, 1001, 101),
(3,  '2026-03-06 09:10:00', '2026-03-07 07:35:00', 1, 1002, 102),
(4,  '2026-03-07 07:35:00', NULL,                  3, 1002, 102),
(5,  '2026-03-06 09:20:00', '2026-03-07 07:40:00', 1, NULL, 103),
(6,  '2026-03-07 07:40:00', NULL,                  3, NULL, 103),
(7,  '2026-03-06 09:30:00', '2026-03-07 07:45:00', 1, NULL, 104),
(8,  '2026-03-07 07:45:00', NULL,                  3, NULL, 104),
(9,  '2026-03-06 09:40:00', '2026-03-07 07:50:00', 1, 1003, 105),
(10, '2026-03-07 07:50:00', NULL,                  3, 1003, 105),
(11, '2026-03-06 09:50:00', '2026-03-07 07:55:00', 1, NULL, 106),
(12, '2026-03-07 07:55:00', NULL,                  3, NULL, 106),
(13, '2026-03-06 10:00:00', '2026-03-07 08:00:00', 1, NULL, 107),
(14, '2026-03-07 08:00:00', NULL,                  3, NULL, 107),
(15, '2026-03-06 10:10:00', '2026-03-07 08:05:00', 1, NULL, 108),
(16, '2026-03-07 08:05:00', NULL,                  3, NULL, 108),
(17, '2026-03-06 10:30:00', '2026-03-07 08:15:00', 1, NULL, 110),
(18, '2026-03-07 08:15:00', NULL,                  3, NULL, 110),
(19, '2026-03-06 11:00:00', '2026-03-07 08:20:00', 1, 1001, 111),
(20, '2026-03-07 08:20:00', NULL,                  3, 1001, 111),
(21, '2026-03-06 11:10:00', '2026-03-07 08:25:00', 1, 1001, 112),
(22, '2026-03-07 08:25:00', NULL,                  3, 1001, 112);
-- 109 (La Plata): ya fue Recibido -> el EB de La Plata no tiene bolsines enviados (A1)
INSERT INTO cambios_estado_bolsin (id, fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_legajo, bolsin_numero) VALUES
(23, '2026-03-05 10:20:00', '2026-03-06 07:30:00', 1, 1004, 109),
(24, '2026-03-06 07:30:00', '2026-03-07 11:00:00', 3, 1004, 109),
(25, '2026-03-07 11:00:00', NULL,                  4, NULL, 109);
-- 113: recién creado (no enviado)
INSERT INTO cambios_estado_bolsin (id, fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_legajo, bolsin_numero) VALUES
(26, '2026-03-07 08:40:00', NULL,                  1, 1001, 113);
-- 114: ya recibido en destino
INSERT INTO cambios_estado_bolsin (id, fecha_hora_inicio, fecha_hora_fin, estado_id, responsable_legajo, bolsin_numero) VALUES
(27, '2026-03-01 09:00:00', '2026-03-02 07:30:00', 1, 1001, 114),
(28, '2026-03-02 07:30:00', '2026-03-03 10:00:00', 3, 1001, 114),
(29, '2026-03-03 10:00:00', NULL,                  4, 2003, 114);
