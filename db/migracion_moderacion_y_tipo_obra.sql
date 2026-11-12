-- Ejecutar UNA sola vez en phpMyAdmin: base db_socialartist > pestaña SQL
USE db_socialartist;

ALTER TABLE publicaciones
  ADD COLUMN tipo_obra      VARCHAR(10)  NOT NULL DEFAULT 'DIGITAL'   AFTER ruta_imagen,
  ADD COLUMN estado         VARCHAR(10)  NOT NULL DEFAULT 'PENDIENTE' AFTER tipo_obra,
  ADD COLUMN motivo_rechazo VARCHAR(255) NULL                         AFTER estado;

-- Las publicaciones que ya existian se marcan como aprobadas para que no desaparezcan
UPDATE publicaciones SET estado = 'APROBADA';

-- Indice para acelerar el explorar y la cola de moderacion
CREATE INDEX idx_publicaciones_estado_tipo ON publicaciones (estado, tipo_obra);
