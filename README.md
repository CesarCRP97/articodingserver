
# Servidor Web de Articoding

Este proyecto ofrece un API REST para la gestión de una comunidad para Articoding.

Se trata de un servidor basado en SpringBoot que ofrece los recursos a un cliente [Articoding](https://github.com/henarmd/articodingclient) y al juego para utilizar una comunidad común.


## Despliegue

El proyecto Maven esta sobre Spring Boot, para su despliegue:

1. Instalar [Maven](https://maven.apache.org/download.cgi)
2. Instalar un cliente de bbdd como mysql o [MariaDB](https://mariadb.org/download/). En el caso de MariaDb
    - Al instalar, mariadb te pide definir una contraseña.
    - Dejar lo demás por defecto e instalar.



3. Crea una base de datos vacía, usando HeidiSQL:
    1. Para la conexión con la base de datos la contraseña por defecto: *123456*
    [](https://github.com/CesarCRP97/articodingserver/Imagen1Heidi.png) 
    *Recuerda cambiar la contraseña en el fichero \src\main\resources\application.properties  en caso de introducir una contraseña distinta a la por defecto*
    2. Crea una base de datos con el nombre "articoding" (imágenes 2 y 3)
        - Clic derecho en la raiz -> *crear nuevo*  -> *base de datos*
        1. [](https://github.com/CesarCRP97/articodingserver/Imagen2Heidi.png) 
        2. [](https://github.com/CesarCRP97/articodingserver/Imagen3Heidi.png) 

- Si da error al ejecutar el jar, crear base de datos con nombre "articoding" dentro del esquema anterior.



4. Modificar las propiedades de acceso de bbdd en el fichero .\src\main\resources\application.properties (en caso de haber configurado la db con Mariadb tal y como indica el paso 3, no hace falta cambiar nada):
    - datasource.url: (en nuestro caso es MariaDB en local)
    - datasource.username: (de la DB)
    - datasource.password: (de la DB)

5. Abrir terminal en la raiz del proyecto

6. Empaquetar la solución ```mvn package```

7. Desplegar el jar generado con: java ```java -jar ```
    - El .jar generado se guarda en la carpeta .\target
    - Para desplegar el jar, abre el terminal en la ubicación del .jar e introducir el comando *java -jar [articodingserver-X.X.X.jar]*


## Resources

- Articoding Game:  <https://github.com/OskarFreestyle/Articoding23-24>
- ArticodingClient: <https://github.com/CesarCRP97/articodingclient>

