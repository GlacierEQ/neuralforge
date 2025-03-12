# NeuralForge

## Guías

### 📄 ¿Cómo inicializar la base de datos localmente?

1. Abrir `MySQL Client (MariaDB 11.6 (x64))`.
1. Inicia sesión.
1. Escribe los siguientes comandos:
   - `create database p3_neuralforge;`
   - `create user 'admin'@'localhost' identified by 'PASSWORD';` Reemplace 'PASSWORD' por su propia contraseña.
   - `grant all privileges on p3_neuralforge.* to 'admin'@'localhost';`
1. Para verificar que los permisos fueron concedidos, escriba los siguientes comandos:
   - `flush privileges;`
   - `show grants for 'admin'@'localhost';`

### ⚙️ Configuración de Lombok en IntelliJ IDEA

Si usas **IntelliJ IDEA**, asegúrate de activar las anotaciones de **Lombok** para evitar errores de compilación:

1. Asegúrate de que el plugin de **Lombok** está instalado en IntelliJ:
   - Abre `File > Settings > Plugins`.
   - Busca `Lombok` en el Marketplace e instálalo.
1. Habilita el soporte para anotaciones de Lombok:
   - Ve a `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`.
   - Activa la casilla **"Enable annotation processing"**.
1. Reinicia IntelliJ IDEA para aplicar los cambios.

Con estos pasos, las anotaciones como `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, entre otras, funcionarán correctamente en el proyecto.

### 🛠️ Importar una colección en Insomnia

Para importar una colección de Insomnia y poder realizar pruebas de API, sigue estos pasos:

1. Abre **Insomnia**.
1. Dirígete al menú **Application** y selecciona **Import/Export**.
1. En la sección **Import Data**, haz clic en **Import File**.
1. Selecciona el archivo JSON ubicado en [neuralforge > neuralforge_api > insomnia_collection](insomnia_collection).
1. Insomnia importará la colección y la agregará a tu lista de solicitudes.

Con estos pasos, podrás cargar fácilmente una colección de API en Insomnia y comenzar a probar las solicitudes disponibles.

