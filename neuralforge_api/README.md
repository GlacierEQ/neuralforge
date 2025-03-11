# NeuralForge

## Guías

### ¿Cómo inicializar la base de datos localmente?

1. Abrir  `MySQL Client (MariaDB 11.6 (x64))`.
1. Inicia sesión.
1. Escribe los siguientes comandos:
   1. `create database p3_neuralforge;`
   1. `create user 'admin'@'localhost' identified by 'PASSWORD';` Reemplace 'PASSWORD' por su propia contraseña.
   1. `grant all privileges on p3_neuralforge.* to 'admin'@'localhost';`
1. Para verificar que los permisos fueron concedido, escriba los siguientes comandos:
   1. `flush privileges;`
   1. `show grants for 'admin'@'localhost';`