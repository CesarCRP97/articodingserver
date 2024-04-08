# Servidor Web de Articoding

Este proyecto ofrece un API REST para la gestión de una comunidad para Articoding.

Se trata de un servidor basado en SpringBoot que ofrece los recursos a un
 [Articodingclient](https://github.com/henarmd/articodingclient) y al juego [Articoding](https://github.com/OskarFreestyle/Articoding23-24) para utilizar una comunidad común.

## Despliegue ##

El proyecto Maven esta sobre Spring Boot, para su despliegue:

1. Instalar [Maven](https://maven.apache.org/download.cgi)


2. Instalar un cliente de bbdd como mysql o [MariaDB](https://mariadb.org/download/).

3. Crear una base de datos

4. Modificar las propiedades en el fichero.\src\main\resources\application.properties:
    #### Ejemplo application.properties ####

    ```properties
        jwt.secret=javainuse
        spring.datasource.url=jdbc:mariadb://localhost:3306/articoding
        spring.datasource.username=root
        spring.datasource.password=123456
        spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
        spring.jpa.hibernate.ddl-auto=update
    ```
    `jwt.secret` : clave secreta utilizada para firmar y verificar tokens JWT (JSON Web Tokens). Debe ser una clave secreta, idealmente aleatoria.

    `spring.datasource.url` : url de la base de datos se conecta la aplicación. `jdbc` significa que es una base de datos MariaDB, `localhost` significa que está guardada en la máquina local, `:3306` es la forma de especificar que el puerto al que se quiere conectar es el 3306, `articoding` es el nombre de la base de datos. 

    `spring.datasource.username` : nombre de usuario utilizado para autentiarse en la base de datos.

    `spring.datasource.password` : contraseña utilizada utilizada para autenticarse en la base de datos.

    `spring.datasource.driver-class-name` : controlador JDBC utilizado para conectarse a la base de datos

    `spring.jpa.hibernate.ddl-auto` : propieda que define el comportamiento de Hibernate con respecto al esquema de la base de datos. En este caso, se utiliza la opción `update`, lo que significará que Hibernate actualizará el esquema de la base de datos según sea necesario, pero no eliminará los datos existentes.

5. Abrir terminal en la raiz del proyecto.

6. Empaquetar la solución ```mvn package```.

7. Desplegar el jar generado con: java ```java -jar ```.
    - El .jar generado se guarda en la carpeta .\target
    - Para desplegar el jar, hay que abrir el terminal en la ubicación del .jar e introducir el comando *java
      -jar <articodingserver-X.X.X.jar>*
    - El servidor escucha en el puerto 8080



----   
### Ejemplo creación base de datos usando Mariadb y HeidiSQL ###


1. Instalar [MariaDB](https://mariadb.org/download/):
    - Al instalar, mariadb te pide definir una contraseña.
    - Dejar lo demás por defecto e instalar.

<p align="center">
<img src="https://github.com/CesarCRP97/articodingserver/blob/master/imagesReadme/Imagen1Heidi.png">

2. **Recuerda cambiar la contraseña en el fichero \src\main\resources\application.properties**

3. Crea una base de datos con el nombre "articoding" (imágenes 2 y 3)
    - Clic derecho en la raiz -> *crear nuevo*  -> *base de datos*

<p align="center">
<img src="https://github.com/CesarCRP97/articodingserver/blob/master/imagesReadme/Imagen2Heidi.png">
</p>
<p align="center">
<img src="https://github.com/CesarCRP97/articodingserver/blob/master/imagesReadme/Imagen3Heidi.png">

## Resources

- Articoding Game:  <https://github.com/OskarFreestyle/Articoding23-24>
- ArticodingClient: <https://github.com/CesarCRP97/articodingclient>