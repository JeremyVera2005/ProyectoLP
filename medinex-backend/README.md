# MEDINEX Backend - Sistema de Gestión Clínica

## Descripción
Sistema backend para la gestión de clínicas MEDINEX, desarrollado con Spring Boot, que incluye funcionalidades para la gestión de doctores, pacientes, citas, servicios y un sistema de preguntas/evaluaciones.

## Características Principales

### 🔐 Autenticación y Autorización
- Autenticación JWT
- Roles de usuario (ADMIN, NORMAL)
- Filtros de seguridad personalizados

### 👨‍⚕️ Gestión de Doctores
- CRUD completo de doctores
- Asignación de especialidades
- Gestión de preguntas por doctor

### 🏥 Gestión de Servicios
- CRUD de servicios médicos
- Categorización de servicios

### ❓ Sistema de Preguntas
- Creación y gestión de preguntas
- Evaluación de doctores
- Resultados y estadísticas

### 👥 Gestión de Usuarios
- Registro de usuarios
- Perfiles de usuario
- Gestión de roles

## Tecnologías Utilizadas

### Backend
- **Spring Boot 2.6.6**
- **Spring Security** - Para autenticación y autorización
- **Spring Data JPA** - Para persistencia de datos
- **MySQL** - Base de datos
- **JWT** - Para tokens de autenticación
- **Maven** - Gestión de dependencias

### Seguridad
- Encriptación de contraseñas con BCrypt
- Tokens JWT para sesiones
- Validación de entrada
- Manejo global de excepciones

## Instalación y Configuración

### Prerrequisitos
- JDK 11 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

### Configuración de Base de Datos
1. Crear base de datos en MySQL:
```sql
CREATE DATABASE medinex;
```

2. Configurar credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/medinex
spring.datasource.username=root
spring.datasource.password=tu_password
```

### Instalación
1. Clonar el repositorio
2. Navegar al directorio del proyecto
3. Ejecutar:
```bash
./mvnw clean install
```

### Ejecutar la aplicación
```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api`

## Endpoints Principales

### Autenticación
- `POST /api/generate-token` - Generar token JWT
- `GET /api/actual-usuario` - Obtener usuario actual

### Usuarios
- `POST /api/usuarios/` - Registrar usuario
- `GET /api/usuarios/{username}` - Obtener usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

### Doctores
- `GET /api/doctor/` - Listar doctores
- `POST /api/doctor/` - Crear doctor
- `PUT /api/doctor/` - Actualizar doctor
- `DELETE /api/doctor/{id}` - Eliminar doctor

### Servicios
- `GET /api/servicio/` - Listar servicios
- `POST /api/servicio/` - Crear servicio
- `PUT /api/servicio/` - Actualizar servicio
- `DELETE /api/servicio/{id}` - Eliminar servicio

### Preguntas
- `GET /api/pregunta/doctor/{id}` - Obtener preguntas por doctor
- `POST /api/pregunta/` - Crear pregunta
- `PUT /api/pregunta/` - Actualizar pregunta
- `DELETE /api/pregunta/{id}` - Eliminar pregunta
- `POST /api/pregunta/evaluar-doctor` - Evaluar doctor

## Mejoras Implementadas

### 🔧 Corrección de Errores
- ✅ Eliminación de variables no utilizadas
- ✅ Corrección de tipos raw en collections
- ✅ Reemplazo de métodos deprecated
- ✅ Mejora en anotaciones de Spring Security

### 🚀 Mejoras de Código
- ✅ Manejo global de excepciones
- ✅ Validación de entrada de datos
- ✅ Logging estructurado con SLF4J
- ✅ Configuración CORS mejorada
- ✅ Utilities para validaciones comunes

### 📊 Configuración Mejorada
- ✅ Configuración de logging detallada
- ✅ Configuración de base de datos optimizada
- ✅ Configuración de JWT centralizada
- ✅ Configuración de CORS flexible

## Estructura del Proyecto

```
src/main/java/com/sistema/rep/
├── configuraciones/          # Configuraciones de Spring
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtils.java
│   ├── MySecurityConfig.java
│   ├── GlobalExceptionHandler.java
│   └── CorsConfig.java
├── controladores/            # Controladores REST
│   ├── AuthenticationController.java
│   ├── DoctorController.java
│   ├── PreguntaController.java
│   ├── ServicioController.java
│   └── UsuarioController.java
├── excepciones/             # Excepciones personalizadas
│   ├── UsuarioFoundException.java
│   └── UsuarioNotFoundException.java
├── modelo/                  # Entidades JPA
│   ├── Doctor.java
│   ├── Pregunta.java
│   ├── Servicio.java
│   ├── Usuario.java
│   └── ...
├── repositorios/           # Repositorios JPA
│   ├── DoctorRepository.java
│   ├── PreguntaRepository.java
│   └── ...
├── servicios/              # Servicios de negocio
│   ├── impl/
│   ├── DoctorService.java
│   ├── PreguntaService.java
│   └── ...
├── utils/                  # Utilidades
│   └── ValidationUtils.java
└── SistemaBackendMedinex.java
```

## Logging
El sistema incluye logging estructurado con diferentes niveles:
- `INFO` - Información general
- `DEBUG` - Información detallada para desarrollo
- `WARN` - Advertencias
- `ERROR` - Errores críticos

## Seguridad
- Contraseñas encriptadas con BCrypt
- Tokens JWT con expiración configurable
- Validación de entrada en todos los endpoints
- Manejo seguro de excepciones

## Contribución
1. Fork el proyecto
2. Crear una rama para tu feature
3. Commit los cambios
4. Push a la rama
5. Abrir un Pull Request

## Licencia
Este proyecto está bajo la Licencia MIT.

## Contacto
Para soporte técnico o consultas, contactar al equipo de desarrollo.
