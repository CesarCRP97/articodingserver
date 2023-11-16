
# Servidor Web de Articoding

Este proyecto ofrece un API REST para la gestión de una comunidad para Articoding.

Se trata de un servidor basado en SpringBoot que ofrece los recursos a un cliente [Articoding](https://github.com/henarmd/articodingclient) y al juego para utilizar una comunidad común.


## Despliegue

El proyecto Maven esta sobre Spring Boot, para su despliegue:

1. Instalar [Maven](https://maven.apache.org/download.cgi)
2. Instalar un cliente de bbdd como mysql o mariaDB basta con crear el esquema vacio ```CREATE SCHEMA articoding```
- Si da error al ejecutar el jar, crear base de datos con nombre "articoding" dentro del esquema anterior.

4. Abrir terminal y acceder a la raiz del proyecto

5. Modificar las propiedades de acceso de bbdd en el fichero application.properties
    - datasource.url: (en nuestro caso es MariaDB en local)
    - datasource.username: (de la DB)
    - datasource.password: (de la DB)
    

6. Empaquetar la solución ```mvn package```

7. Desplegar el jar generado con: java ```java -jar ```


## Resources

- ArticodingClient: <https://github.com/henarmd/articodingclient>

