# BugBusters Producto 3

## Descripción general

**BugBusters Producto 3** es una aplicación desarrollada en **Java** con una arquitectura organizada por capas y conectada a **MySQL** mediante **JDBC**. Este proyecto forma parte del desarrollo académico del equipo **BugBusters** y representa la evolución del sistema de gestión de una tienda online, incorporando persistencia real en base de datos.

En esta fase del proyecto, el objetivo principal es dotar a la aplicación de una estructura de almacenamiento sólida, organizada y funcional, permitiendo trabajar con entidades como **clientes**, **artículos** y **pedidos**, así como establecer sus relaciones mediante un modelo relacional correctamente diseñado.

El proyecto incluye tanto la estructura del código Java como los scripts SQL necesarios para crear la base de datos, insertar datos de prueba y trabajar con procedimientos almacenados.

---

## Objetivos del producto

Los principales objetivos de este producto son:

- Implementar la persistencia de datos en MySQL.
- Conectar la aplicación Java con la base de datos mediante JDBC.
- Diseñar correctamente las tablas y sus relaciones.
- Definir claves primarias y foráneas.
- Configurar adecuadamente los campos autoincrementales.
- Preparar procedimientos almacenados para inserción, actualización y eliminación.
- Permitir la gestión estructurada de clientes, artículos y pedidos.
- Mantener una organización clara del proyecto para facilitar su comprensión y mantenimiento.

---

## Funcionalidades principales

El sistema está orientado a la gestión básica de una tienda online y contempla las siguientes áreas funcionales:

### Gestión de clientes

- Almacenamiento de datos de clientes.
- Diferenciación entre clientes **estándar** y **premium**.
- Registro de información básica como email, nombre, domicilio y NIF.
- Actualización y eliminación mediante procedimientos almacenados.

### Gestión de artículos

- Almacenamiento de artículos con código único.
- Descripción del artículo.
- Precio de venta.
- Gastos de envío.
- Tiempo de preparación.

### Gestión de pedidos

- Asociación entre clientes y artículos.
- Almacenamiento de cantidad, fecha y estado del pedido.
- Relación entre tablas mediante claves foráneas.
- Operaciones de inserción, modificación y eliminación.

---

## Tecnologías utilizadas

Este proyecto ha sido desarrollado utilizando las siguientes tecnologías:

- **Java**
- **JDBC**
- **MySQL**
- **MySQL Workbench**
- **IntelliJ IDEA**
- **Git**
- **GitHub**

---

## Arquitectura y organización del proyecto

El proyecto está organizado en distintos paquetes para mantener una estructura clara, modular y fácil de mantener.

```text
src/
├── Controlador/
├── DAO/
│   ├── Interfaces/
│   └── MySQL/
├── Database/
│   ├── producto3.sql
│   ├── procedimientos.sql
│   └── datos_prueba.sql
├── Excepciones/
├── Factory/
├── Modelo/
├── Util/
└── Vista/
```

### Descripción de la estructura

#### `Controlador/`
Contiene la lógica de coordinación entre la vista y el modelo.

#### `DAO/`
Incluye la capa de acceso a datos, separada en interfaces y en sus implementaciones para MySQL.

#### `Database/`
Contiene los scripts SQL fundamentales del proyecto:

- Creación de la base de datos y de las tablas.
- Procedimientos almacenados.
- Datos de prueba.

#### `Excepciones/`
Incluye excepciones personalizadas para el control de errores.

#### `Factory/`
Contiene clases relacionadas con la creación o abstracción de objetos DAO.

#### `Modelo/`
Incluye las clases que representan las entidades del sistema.

#### `Util/`
Contiene utilidades generales, especialmente la conexión a la base de datos.

#### `Vista/`
Incluye la parte de ejecución o interacción principal del programa.

---

## Modelo de base de datos

La base de datos del proyecto se estructura en torno a tres tablas principales.

### Tabla `clientes`

Contiene la información de los clientes del sistema.

**Campos principales:**

- `id_cliente`
- `email`
- `nombre`
- `domicilio`
- `nif`
- `tipo_cliente`

### Tabla `articulos`

Contiene la información de los artículos disponibles.

**Campos principales:**

- `id_articulo`
- `codigo`
- `descripcion`
- `precio_venta`
- `gastos_envio`
- `tiempo_preparacion`

### Tabla `pedidos`

Relaciona clientes y artículos mediante los pedidos realizados.

**Campos principales:**

- `id_pedido`
- `id_cliente`
- `id_articulo`
- `cantidad`
- `fecha_hora`
- `estado`

---

## Relaciones entre tablas

La base de datos implementa integridad referencial mediante claves foráneas:

- Cada pedido está asociado a un cliente.
- Cada pedido está asociado a un artículo.

Esto permite mantener la consistencia de los datos y asegurar que no existan pedidos que hagan referencia a registros inexistentes.

---

## Scripts SQL incluidos

### `producto3.sql`

Archivo encargado de crear la estructura de la base de datos, incluyendo:

- Creación de la base de datos.
- Creación de tablas.
- Claves primarias.
- Claves foráneas.
- Relaciones entre tablas.
- Configuración de campos autoincrementales.

### `procedimientos.sql`

Archivo que contiene los procedimientos almacenados del proyecto.

Incluye procedimientos para:

#### Inserción
- `insertar_articulo`
- `insertar_cliente`
- `insertar_pedido`

#### Actualización
- `actualizar_articulo`
- `actualizar_cliente`
- `actualizar_pedido`

#### Eliminación
- `eliminar_articulo`
- `eliminar_cliente`
- `eliminar_pedido`

### `datos_prueba.sql`

Archivo que contiene datos de prueba preparados para poblar la base de datos y facilitar las comprobaciones del sistema.

---

## Requisitos previos

Para poder ejecutar correctamente el proyecto, se recomienda disponer de lo siguiente:

- **Java JDK**
- **MySQL Server**
- **MySQL Workbench** o cualquier cliente compatible con MySQL
- **IntelliJ IDEA** o cualquier IDE compatible con Java
- Conexión configurada correctamente en `ConexionBD.java`

---

## Preparación de la base de datos

Para dejar la base de datos lista, se recomienda seguir este orden de ejecución:

1. Ejecutar la estructura de la base de datos: `producto3.sql`
2. Ejecutar los procedimientos almacenados: `procedimientos.sql`
3. Insertar los datos de prueba: `datos_prueba.sql`

Este orden permite dejar preparada una base de datos completamente funcional para realizar pruebas y validaciones.

---

## Configuración de la conexión

La conexión a la base de datos se realiza mediante la clase:

`src/Util/ConexionBD.java`

Antes de ejecutar el proyecto, es importante revisar y adaptar los siguientes datos según el entorno local:

- URL de conexión
- Nombre de la base de datos
- Usuario
- Contraseña

### Ejemplo típico de configuración

```java
private static final String URL = "jdbc:mysql://localhost:3306/producto3";
private static final String USER = "root";
private static final String PASSWORD = "";
```

---

## Ejecución del proyecto

Una vez preparada la base de datos, el proyecto puede ejecutarse desde el entorno Java configurado en el IDE.

### Pasos recomendados

1. Abrir el proyecto en IntelliJ IDEA.
2. Comprobar que la conexión a MySQL está correctamente configurada.
3. Ejecutar los scripts SQL necesarios.
4. Compilar el proyecto.
5. Ejecutar la clase principal correspondiente.

---

## Estado del proyecto

Este producto representa la fase del proyecto en la que se integra la persistencia real en base de datos. Se ha trabajado especialmente en:

- El diseño relacional.
- La conexión entre Java y MySQL.
- La organización por capas.
- La preparación de procedimientos almacenados.
- La creación de un entorno reproducible mediante datos de prueba.

---

## Buenas prácticas aplicadas

Durante el desarrollo se ha intentado seguir una estructura clara y ordenada, prestando atención a aspectos como:

- Separación de responsabilidades.
- Organización modular del código.
- Uso de nombres comprensibles.
- Preparación de scripts reutilizables.
- Mantenimiento de integridad referencial.
- Uso de procedimientos almacenados para mejorar la gestión SQL.

---

## Finalidad académica

Este proyecto ha sido desarrollado con finalidad académica como parte del trabajo del equipo **BugBusters**, dentro del contexto del **Producto 3**, centrado en la persistencia de datos y la integración con base de datos relacional.

---

## Autoría

Proyecto realizado por el equipo **BugBusters**.

---

## Observaciones finales

Este repositorio recoge el trabajo correspondiente al desarrollo del producto orientado a base de datos y persistencia, ofreciendo una base sólida para continuar con futuras fases del proyecto.

La estructura planteada permite comprender con claridad cómo se organiza la aplicación, cómo se almacenan los datos y cómo se relacionan entre sí las distintas entidades del sistema.

Se ha buscado una solución formal, ordenada y funcional, coherente con los requisitos del producto y adecuada para su evaluación académica.
