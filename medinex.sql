-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 03-11-2025 a las 02:10:10
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `medinex`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chat_historial`
--

CREATE TABLE `chat_historial` (
  `id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `mensaje_usuario` text NOT NULL,
  `respuesta_ia` text NOT NULL,
  `fecha` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `citas_medicas`
--

CREATE TABLE `citas_medicas` (
  `cita_id` bigint(20) NOT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_actualizacion` datetime(6) NOT NULL,
  `fecha_cita` datetime(6) NOT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  `observaciones` varchar(1000) DEFAULT NULL,
  `doctor_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `doctores`
--

CREATE TABLE `doctores` (
  `doctor_id` bigint(20) NOT NULL,
  `activo` bit(1) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `numero_de_preguntas` varchar(255) DEFAULT NULL,
  `puntos_maximos` varchar(255) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `servicio_servicio_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `doctores`
--

INSERT INTO `doctores` (`doctor_id`, `activo`, `descripcion`, `numero_de_preguntas`, `puntos_maximos`, `titulo`, `servicio_servicio_id`) VALUES
(1, b'1', 'Lunes a Viernes: 9:00 a.m. - 1:00 p.m. / 3:00 p.m. - 7:00 p.m.\nSábados: 9:00 a.m. - 1:00 p.m.\nDomingos y feriados: Cerrado.', '5', '19345675', 'Dr. Juan Perez', 1),
(2, b'1', 'Lunes a Viernes: 9:00 a.m. - 1:00 p.m. / 3:00 p.m. - 7:00 p.m.\nSábados: 9:00 a.m. - 1:00 p.m.\nDomingos y feriados: Cerrado.', '3', '71672383', 'Dr. Martin Fernandez', 2),
(3, b'1', 'Lunes a Viernes: 9:00 a.m. - 1:00 p.m. / 3:00 p.m. - 7:00 p.m.\nSábados: 9:00 a.m. - 1:00 p.m.\nDomingos y feriados: Cerrado.', '4', '20232434', 'Dr. Pedro Lopez', 10),
(4, b'0', 'Lunes a Viernes: 9:00 a.m. - 1:00 p.m. / 3:00 p.m. - 7:00 p.m.\nSábados: 9:00 a.m. - 1:00 p.m.\nDomingos y feriados: Cerrado.', '6', '45467812', 'Dr. Pepe Lucho', 4),
(5, b'1', 'Atención las 24 horas del día. (24/7)', '100', '20456723', 'Dr. Antonio Nuñez', 11);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preguntas`
--

CREATE TABLE `preguntas` (
  `pregunta_id` bigint(20) NOT NULL,
  `contenido` varchar(5000) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `opcion1` varchar(255) DEFAULT NULL,
  `opcion2` varchar(255) DEFAULT NULL,
  `opcion3` varchar(255) DEFAULT NULL,
  `opcion4` varchar(255) DEFAULT NULL,
  `respuesta` varchar(255) DEFAULT NULL,
  `doctor_doctor_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `preguntas`
--

INSERT INTO `preguntas` (`pregunta_id`, `contenido`, `imagen`, `opcion1`, `opcion2`, `opcion3`, `opcion4`, `respuesta`, `doctor_doctor_id`) VALUES
(1, '¿Qué horario desea agendar su cita?', NULL, 'Lunes: 8:00 a.m. - 12:00 p.m. / 4:00 p.m. - 8:00 p.m.', 'Martes: 2:00 p.m. - 8:00 p.m.', 'Miércoles: 9:00 a.m. - 1:00 p.m. / 3:00 p.m. - 7:00 p.m.', 'Jueves: 7:00 a.m. - 3:00 p.m.', 'Martes: 2:00 p.m. - 8:00 p.m.', 1),
(2, '¿Cuenta con problemas previos?', NULL, 'Glucosa alta', 'Colesterol alto', 'Alergico a alguna vitamina', 'Sin alergias', 'Sin alergias', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `respuestas_cuestionario`
--

CREATE TABLE `respuestas_cuestionario` (
  `respuesta_id` bigint(20) NOT NULL,
  `es_correcta` bit(1) NOT NULL,
  `fecha_respuesta` datetime(6) NOT NULL,
  `observaciones_adicionales` varchar(1000) DEFAULT NULL,
  `respuesta_texto` varchar(5000) NOT NULL,
  `cita_id` bigint(20) NOT NULL,
  `pregunta_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `rol_id` bigint(20) NOT NULL,
  `rol_nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`rol_id`, `rol_nombre`) VALUES
(1, 'ADMIN'),
(2, 'NORMAL');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicios`
--

CREATE TABLE `servicios` (
  `servicio_id` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `servicios`
--

INSERT INTO `servicios` (`servicio_id`, `descripcion`, `titulo`) VALUES
(1, 'Brindan atención integral a pacientes de todas las edades, abarcando una amplia gama de condiciones y problemas de salud. ', 'Medicina General'),
(2, 'Se realiza un chequeo general, en base a ello se designa estrategias y acciones implementadas para prevenir, reducir o eliminar la propagación de enfermedades, ya sean infecciosas o no infecciosas.', 'Control de Enfermedades'),
(3, 'Se encarga de evaluar, diagnosticar y tratar diversas condiciones cardíacas, así como de promover la salud cardiovascular. ', 'Cardiología'),
(4, 'Se realiza el diagnóstico y tratamiento de enfermedades y condiciones que afectan los órganos reproductores femeninos, como el útero, los ovarios, las trompas de Falopio y la vagina.', 'Ginecología'),
(5, 'Los pacientes recibiran atención médica sin necesidad de acudir físicamente a un centro de salud, utilizando plataformas como videoconferencias o aplicaciones de videollamadas. ', 'Teleconsulta Médica'),
(6, 'Se dedican a la prevención, diagnóstico y tratamiento de enfermedades y problemas de salud específicos de esta etapa de la vida.', 'Pediatría'),
(7, 'Los niveles de creatinina en sangre se miden para evaluar la función renal y se utilizan para detectar problemas como la enfermedad renal.', 'Creatinina'),
(8, 'Se realiza una prueba para evaluar la salud general y detectar enfermedades, especialmente aquellas relacionadas con el tracto urinario, los riñones y la diabetes. ', 'Examen de orina'),
(9, 'Se mide la cantidad de glucosa (un tipo de azúcar) en la sangre. ', 'Glucosa'),
(10, 'Se realiza una revisión médica para comprobar el estado general de salud y detectar afecciones, como la anemia o la leucemia.', 'Prueba de sangre'),
(11, 'Servicio de emergencias médicas disponible las 24 horas del día, 7 días a la semana', 'Emergencias 24/7');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `perfil` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `apellido`, `email`, `enabled`, `nombre`, `password`, `perfil`, `telefono`, `username`) VALUES
(1, 'Ramirez', 'c1@gmail.com', b'1', 'alex', '$2a$10$qkIDBYfoxVdFLLzcW.Iui.DuCAgVbw3.KMQmmkuJ2MxTGcVVY9qAS', 'foto.png', '988212020', 'alex'),
(2, 'Remuzgo', 'tanaka0503@gmail.com', b'1', 'Eduardo', '$2a$10$mi8tlh1ITHFEka/uQ288ZerqjIlKqihzDVJzccq7fyPIdpLgDNCzW', 'default.png', '987345678', 'Tanaka'),
(3, 'lopez', 'lokilopez@gmail.com', b'1', 'loki', '$2a$10$WqRDu0UNy97b6bfifoZ0EO6Bgh4XH3lctc588zSqJ1i4FA3jMMVWe', 'default.png', '999354751', 'loki'),
(4, 'Vera Jara', 'infamousg2k@gmail.com', b'1', 'Ashton Vera Jara', '$2a$10$yu64zHn9.uPgBETWaRWTLumNiiDxRAthior/OzHcmvAydM3fq.aBC', 'default.png', '987267850', 'infamousg2k'),
(5, 'Vera Jara', 'infamousg2kbv@gmail.com', b'1', 'Pancho', '$2a$10$gjSnH7/hXBTvQunfY6h3w.fMkXl/dXgc39xofDvxE9rdoZaK7I6DW', 'default.png', '946267850', 'etejeremy');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_rol`
--

CREATE TABLE `usuario_rol` (
  `usuario_rol_id` bigint(20) NOT NULL,
  `rol_rol_id` bigint(20) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario_rol`
--

INSERT INTO `usuario_rol` (`usuario_rol_id`, `rol_rol_id`, `usuario_id`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 2, 3),
(4, 1, 4),
(5, 2, 5);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `chat_historial`
--
ALTER TABLE `chat_historial`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `citas_medicas`
--
ALTER TABLE `citas_medicas`
  ADD PRIMARY KEY (`cita_id`),
  ADD KEY `FKi4c768t87r3exfa7fc141e60f` (`doctor_id`),
  ADD KEY `FKi88sqltb1cc0vnp2a00mktfy4` (`usuario_id`);

--
-- Indices de la tabla `doctores`
--
ALTER TABLE `doctores`
  ADD PRIMARY KEY (`doctor_id`),
  ADD KEY `FKtsy82dwc0iehhxk0aqaofa8v` (`servicio_servicio_id`);

--
-- Indices de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  ADD PRIMARY KEY (`pregunta_id`),
  ADD KEY `FK18revdh9nmx8ccau9ftvwruly` (`doctor_doctor_id`);

--
-- Indices de la tabla `respuestas_cuestionario`
--
ALTER TABLE `respuestas_cuestionario`
  ADD PRIMARY KEY (`respuesta_id`),
  ADD KEY `FKbqc4dml4qqdol6iokv6bo8i3h` (`cita_id`),
  ADD KEY `FKr41jgcg7g2d6wf56ouo9hb637` (`pregunta_id`),
  ADD KEY `FKmdmnw1ww2ya8iiugfw5t5g0ow` (`usuario_id`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`rol_id`);

--
-- Indices de la tabla `servicios`
--
ALTER TABLE `servicios`
  ADD PRIMARY KEY (`servicio_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD PRIMARY KEY (`usuario_rol_id`),
  ADD KEY `FK7j1tyvjj1tv8gbq7n6f7efccc` (`rol_rol_id`),
  ADD KEY `FKktsemf1f6awjww4da0ocv4n32` (`usuario_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `chat_historial`
--
ALTER TABLE `chat_historial`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `citas_medicas`
--
ALTER TABLE `citas_medicas`
  MODIFY `cita_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `doctores`
--
ALTER TABLE `doctores`
  MODIFY `doctor_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `preguntas`
--
ALTER TABLE `preguntas`
  MODIFY `pregunta_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `respuestas_cuestionario`
--
ALTER TABLE `respuestas_cuestionario`
  MODIFY `respuesta_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `servicios`
--
ALTER TABLE `servicios`
  MODIFY `servicio_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  MODIFY `usuario_rol_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `chat_historial`
--
ALTER TABLE `chat_historial`
  ADD CONSTRAINT `chat_historial_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `citas_medicas`
--
ALTER TABLE `citas_medicas`
  ADD CONSTRAINT `FKi4c768t87r3exfa7fc141e60f` FOREIGN KEY (`doctor_id`) REFERENCES `doctores` (`doctor_id`),
  ADD CONSTRAINT `FKi88sqltb1cc0vnp2a00mktfy4` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `doctores`
--
ALTER TABLE `doctores`
  ADD CONSTRAINT `FKtsy82dwc0iehhxk0aqaofa8v` FOREIGN KEY (`servicio_servicio_id`) REFERENCES `servicios` (`servicio_id`);

--
-- Filtros para la tabla `preguntas`
--
ALTER TABLE `preguntas`
  ADD CONSTRAINT `FK18revdh9nmx8ccau9ftvwruly` FOREIGN KEY (`doctor_doctor_id`) REFERENCES `doctores` (`doctor_id`);

--
-- Filtros para la tabla `respuestas_cuestionario`
--
ALTER TABLE `respuestas_cuestionario`
  ADD CONSTRAINT `FKbqc4dml4qqdol6iokv6bo8i3h` FOREIGN KEY (`cita_id`) REFERENCES `citas_medicas` (`cita_id`),
  ADD CONSTRAINT `FKmdmnw1ww2ya8iiugfw5t5g0ow` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `FKr41jgcg7g2d6wf56ouo9hb637` FOREIGN KEY (`pregunta_id`) REFERENCES `preguntas` (`pregunta_id`);

--
-- Filtros para la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD CONSTRAINT `FK7j1tyvjj1tv8gbq7n6f7efccc` FOREIGN KEY (`rol_rol_id`) REFERENCES `roles` (`rol_id`),
  ADD CONSTRAINT `FKktsemf1f6awjww4da0ocv4n32` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
