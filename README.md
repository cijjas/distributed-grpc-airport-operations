# TPE1 - POD - Grupo 7

## Integrantes del Grupo
- Christian Ijjas - Legajo: 63555
- Luca Seggiaro - Legajo: 62855
- Manuel Dithurbide - Legajo: 62057
- Tobias Perry - Legajo: 62064

## Instrucciones para Compilar

Para compilar el proyecto utilizando Maven, se debe de tener instalado Maven y Java JDK 22.0.1 en el sistema. 

Correr el siguiente comando para limpiar y compilar el proyecto:
   ```mvn clean package```


## Instrucciones para Ejecutar el Servidor

1. Navegar hasta el directorio `./resources/scripts`.
   ```cd ./resources/scripts```
3. Ejecutar el siguiente comando para iniciar el servidor, especificando el puerto deseado con `-Dport=xxxx`:
   ```sh server.sh -Dport=XXXXX```

## Instrucciones para Ejecutar cada Cliente

1. Navegar hasta el directorio `./resources/scripts`.
2. Ejecutar el archivo bash correspondiente al cliente deseado.
ej: ```sh eventsClient.sh {parametros}```

