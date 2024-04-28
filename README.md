
# TPE1 - POD - Grupo 7

#### Índice
- [Integrantes del Grupo](#integrantes-del-grupo)
- [Instrucciones para Compilar](#instrucciones-para-compilar)
- [Instrucciones para Ejecutar el Servidor](#instrucciones-para-ejecutar-el-servidor)
- [Instrucciones para Ejecutar cada Cliente](#instrucciones-para-ejecutar-cada-cliente)
   - [Admin Client](#admin-client)
   - [Counter Client](#counter-client)
   - [Passenger Client](#passenger-client)
   - [Events Client](#events-client)
   - [Query Client](#query-client)

## Integrantes del Grupo
- [Christian Ijjas](https://github.com/cijjas) - Legajo: 63555
- [Luca Seggiaro](https://github.com/Lucaseggi) - Legajo: 62855
- [Manuel Dithurbide](https://github.com/manudithur) - Legajo: 62057
- [Tobias Perry](https://github.com/TobiasPerry) - Legajo: 62064

## Instrucciones para Compilar

Para compilar el proyecto utilizando Maven, se debe de tener instalado Maven y Java mayor a 17 en el sistema. 

Correr el siguiente comando en la raíz para limpiar y compilar el proyecto:
   ```mvn clean package```


## Instrucciones para Ejecutar el Servidor

1. Navegar hasta el directorio `./resources/scripts`.
   
   ```sh
   cd ./resources/scripts
   ```
   
2. Ejecutar el siguiente comando para iniciar el servidor, opcionalmente se puede especificar el puerto deseado con `-Dport=<portNumber>`, por default el puerto es `50051` :

   ```sh
   sh server.sh [ -Dport=<portNumber> ]
   ```

## Instrucciones para Ejecutar cada Cliente

1. Navegar hasta el directorio `./resources/scripts`.
2. Ejecutar el archivo bash correspondiente al cliente deseado.

```sh
sh [adminClient.sh | counterClient.sh | passengerClient.sh | eventsClient.sh | queryClient.sh] -DserverAddress=<serverAddress> -Daction=<action> <params>
```

donde las acciones y los parametros adicionales dependen del cliente al cual se está invocando. Se puede observar en detalle por cliente:
### Admin Client
```sh
sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dsector=sectorName | -Dcounters=counterCount | -DinPath=manifestPath ]
```

| Parameter    | Options                                | Description                                             |
| ------------ | -------------------------------------- | ------------------------------------------------------- |
| `-Daction`   | `addSector`, `addCounters`, `manifest` | La acción que se quiere ejecutar.                       |
| `-Dsector`   | `string: sectorName`                   | Nombre del sector.                                      |
| `-Dcounters` | `number: counterCount`                 | Cantidad de mostradores.                                |
| `-DinPath`   | `path: manifestPath`                   | Ruta al archivo que se quiere usar para importar datos. |

### Counter Client
```sh
sh counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dsector=sectorName | -DcounterFrom=fromVal | -DcounterTo=toVal | -Dflights=flights | -Dairline=airlineName | -DcounterCount=countVal ]
```

| Parameter        | Options                                                                                                      | Description                                |
| ---------------- | ------------------------------------------------------------------------------------------------------------ | ------------------------------------------ |
| `-Daction`       | `listSectors`, `listCounters`, `assignCounters`, `freeCounters`, `checkinCounters`, `listPendingAssignments` | La acción que se quiere ejecutar.          |
| `-Dsector`       | `string: sector`                                                                                             | Nombre del sector.                         |
| `-DcounterFrom`  | `number: fromVal`                                                                                            | Valor desde el cual se quiere especificar. |
| `-DcounterTo`    | `number: toVal`                                                                                              | Valor hasta el cual se quiere especificar. |
| `-Dflight`       | `string: flightCodes`, `string[string]: 'flightCode0\|...\|flightCodeN'`                                     | Códigos de uno o más vuelos.               |
| `-Dairline`      | `string: airlineName`                                                                                        | Nombre de la aerolínea.                    |
| `-DcounterCount` | `number: countVal`                                                                                           | Cantidad de mostradores.                   |




### Passenger Client
```sh
sh passengerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dbooking=booking | -Dsector=sectorName | -Dcounter=counterNumber ]
```

| Parameter   | Options                                               | Description                       |
| ----------- | ----------------------------------------------------- | --------------------------------- |
| `-Daction`  | `fetchCounter`, `passengerCheckin`, `passengerStatus` | La acción que se quiere ejecutar. |
| `-Dbooking` | `string: bookingCode`                                 | Código de reserva.                |
| `-Dsector`  | `string: sectorName`                                  | Nombre del sector.                |
| `-Dcounter` | `number: counterNumber`                               | Número del mostrador.             |
### Events Client
```sh
sh eventsClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -Dairline=airlineName
```

| Parameter   | Options                  | Description                       |
| ----------- | ------------------------ | --------------------------------- |
| `-Daction`  | `register`, `unregister` | La acción que se quiere ejecutar. |
| `-Dairline` | `string: airlineName`    | Nombre de la aerolínea.           |
### Query Client
```sh
sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -DoutPath=query.txt [ -Dsector=sectorName | -Dairline=airlineName | -Dcounter=counterVal ]
```

| Parameter   | Options                | Description                                         |
| ----------- | ---------------------- | --------------------------------------------------- |
| `-Daction`  | `counters`, `checkins` | La acción que se quiere ejecutar.                   |
| `-DoutPath` | `path: outPath`        | Ruta al archivo donde se quiere exportar los datos. |
| `-Dsector`  | `string: sectorName`   | Nombre del sector.                                  |
| `-Dcounter` | `number: counterVal`   | Número del mostrador.                               |
| `-Dairline` | `string: airlineName`  | Nombre de la aerolínea.                             |
