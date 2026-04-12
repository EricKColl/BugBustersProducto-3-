# BugBusters Producto 4

## Descripción general

Este proyecto corresponde al **Producto 4** del equipo **BugBusters** y parte del trabajo realizado en el **Producto 3**.

En esta fase se ha comenzado la migración de la persistencia tradicional con **JDBC** hacia una persistencia basada en **ORM**, utilizando **JPA** con **Hibernate** como proveedor de persistencia, **MySQL** como base de datos, **Railway** como servicio de base de datos remota y **Maven** para la gestión de dependencias.

El proyecto mantiene la estructura general del producto anterior y se prepara para seguir evolucionando bajo el patrón **MVC**.

---

## Estado actual del proyecto

Actualmente, en esta rama se ha dejado preparada la **infraestructura base del Producto 4**, incluyendo:

- Configuración de **Maven**
- Archivo `persistence.xml`
- Conexión JPA con Hibernate hacia la base de datos `producto4`
- Clase utilitaria `JPAUtil` para gestionar `EntityManagerFactory`
- Clase de prueba `PruebaConexionJPA` para verificar que la infraestructura funciona correctamente
- Scripts SQL adaptados de `producto3` a `producto4`

Esta parte corresponde a la base técnica necesaria para que el resto del equipo pueda empezar a trabajar sobre entidades, DAO/Repository e integración por capas.

---

## Tecnologías utilizadas

- **Java**
- **IntelliJ IDEA**
- **Maven**
- **JPA**
- **Hibernate**
- **MySQL**
- **Railway**
- **GitHub**

---

## Estructura general del proyecto

```text
BugBusters Producto 3/
├── src/
│   ├── Controlador/
│   ├── DAO/
│   ├── Database/
│   ├── Excepciones/
│   ├── Factory/
│   ├── META-INF/
│   │   └── persistence.xml
│   ├── Modelo/
│   ├── Util/
│   │   ├── JPAUtil.java
│   │   └── PruebaConexionJPA.java
│   └── Vista/
├── lib/
├── pom.xml
├── README.md
└── .gitignore
```

---

## Requisitos previos

Antes de abrir el proyecto, conviene tener preparado lo siguiente:

- **IntelliJ IDEA**
- **JDK 17** configurado en IntelliJ
- **Conexión a Internet** para que Maven descargue las dependencias
- **Acceso a la base de datos remota** configurada en `persistence.xml`

---

## Cómo descargar y abrir el proyecto correctamente

### 1. Clonar el repositorio

Clona el repositorio desde GitHub en tu equipo.

### 2. Abrir la carpeta raíz del proyecto

En IntelliJ IDEA:

- Selecciona **Open**
- Abre la **carpeta raíz del proyecto**
- No abras archivos sueltos ni subcarpetas internas

### 3. Cargar Maven

Al abrir el proyecto, IntelliJ debería detectar automáticamente el archivo `pom.xml`.

Si aparece alguna opción como:

- **Load Maven Project**
- **Import Maven Changes**
- **Trust Project**

hay que aceptarla para que el entorno se configure correctamente.

### 4. Verificar el SDK

En IntelliJ:

- Ve a **File > Project Structure > Project**
- Comprueba que el **Project SDK** sea **Java 17**

### 5. Esperar a que Maven descargue dependencias

Es importante dejar que IntelliJ termine de descargar todas las librerías necesarias antes de ejecutar nada.

---

## Configuración de la persistencia JPA

La configuración principal está en el archivo:

`src/META-INF/persistence.xml`

En este archivo se define:

- La unidad de persistencia **Producto4PU**
- El proveedor **Hibernate**
- La conexión JDBC a **MySQL**
- La estrategia `hibernate.hbm2ddl.auto`
- El acceso a la base de datos **producto4**

---

## Base de datos

La base de datos utilizada en esta fase es:

`producto4`

Los scripts SQL adaptados al Producto 4 se encuentran en:

`src/Database/`

### Archivos disponibles

- `Tablas Base de Datos.sql`
- `Procedimientos almacenados.sql`
- `Datos-Prueba.sql`

### Orden recomendado de ejecución

1. `Tablas Base de Datos.sql`
2. `Procedimientos almacenados.sql`
3. `Datos-Prueba.sql`

---

## Comprobación de la infraestructura JPA

Para comprobar que la infraestructura ORM está correctamente preparada, ejecutar la clase:

`Util.PruebaConexionJPA`

Si todo está correcto, la consola debería indicar que:

- JPA arranca correctamente
- Hibernate carga la configuración
- La conexión con MySQL funciona
- La consulta de prueba devuelve resultado

Esta clase sirve como verificación rápida antes de que el resto del equipo continúe con entidades y repositorios.

---

## Ejecución del programa heredado del Producto 3

Mientras se completa la migración al Producto 4, la base del programa anterior sigue estando presente.

La clase principal tradicional del programa es:

`Vista.Main`

Esto permite seguir utilizando la base del proyecto mientras se avanza en la migración a ORM.

---

## Archivos locales de IntelliJ

La configuración local de IntelliJ, como la carpeta `.idea` y los archivos `.iml`, no se versiona en GitHub.

Esto se ha hecho intencionadamente para evitar problemas entre distintos equipos, rutas locales, configuraciones de SDK o versiones diferentes de IntelliJ.

Cada integrante del equipo podrá abrir el proyecto en su propio entorno y dejar que IntelliJ regenere su configuración automáticamente.

---

## Trabajo realizado en esta fase

En esta fase se ha dejado preparada la parte de infraestructura y configuración (**core**) del Producto 4, incluyendo:

- Preparación del repositorio para trabajo en ramas
- Creación de la rama de trabajo individual
- Adaptación de los scripts SQL a `producto4`
- Preparación de Maven
- Creación de `persistence.xml`
- Creación de `JPAUtil`
- Prueba mínima de conexión con `PruebaConexionJPA`
- Limpieza del repositorio para hacerlo portable y estable para todo el equipo

---

## Trabajo pendiente para siguientes fases

A partir de esta base, el resto del desarrollo del Producto 4 debe continuar con:

- Mapeo de entidades JPA
- Herencia entre tipos de cliente
- Relaciones entre entidades
- Sustitución progresiva de JDBC por ORM
- Adaptación de DAO/Repository
- Integración con controlador y vista
- Validación funcional completa del programa

---

## Observaciones importantes

- Si IntelliJ tarda en reconocer el proyecto, normalmente basta con cerrar y volver a abrir la carpeta raíz.
- Si Maven no carga automáticamente, puede recargarse manualmente desde la ventana lateral de Maven.
- Si la conexión a la base de datos cambia, habrá que actualizar los datos en `persistence.xml`.
- No se deben subir archivos de configuración local de IntelliJ al repositorio.

---

## Autoría

Proyecto desarrollado por el equipo **BugBusters** dentro del marco académico del **Producto 4**.