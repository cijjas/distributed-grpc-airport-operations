#!/bin/bash

# sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dsector=sectorName | -Dcounters=counterCount | -DinPath=manifestPath ]

## addSector -Dsector=sectorName
## addCounters -Dsector=sectorName -Dcounters=counterCount
## manifest -DinPath=manifestPath



# Función para mostrar el mensaje de ayuda
show_help() {
    echo "Uso: $0 -DserverAddress=<dirección del servidor> -Daction=<acción> [opciones adicionales]"
    echo "Acciones disponibles:"
    echo "  - addSector: Agrega un sector"
    echo "    Ejemplo: $0 -DserverAddress=10.6.0.1:50051 -Daction=addSector -Dsector=C"
    echo "  - addCounters: Agrega un rango de mostradores"
    echo "    Ejemplo: $0 -DserverAddress=10.6.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters=3"
    echo "  - manifest: Agrega pasajeros esperados desde un archivo CSV"
    echo "    Ejemplo: $0 -DserverAddress=10.6.0.1:50051 -Daction=manifest -DinPath=../manifest.csv"
}



# Parsear los argumentos de la línea de comandos
while [ $# -gt 0 ]; do
    case "$1" in
        -DserverAddress=*)
            SERVER_ADDRESS="${1#*=}"
            shift 1
            ;;
        -Daction=*)
            ACTION="${1#*=}"
            shift 1
            ;;
        -Dsector=*)
            SECTOR="${1#*=}"
            shift 1
            ;;
        -Dcounters=*)
            COUNTERS="${1#*=}"
            shift 1
            ;;
        -DinPath=*)
            IN_PATH="${1#*=}"
            shift 1
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo "Opción desconocida: $1"
            show_help
            exit 1
            ;;
    esac
done

CLIENT_JARS="../client/target/tpe1-g7-client-2024.1Q/lib/jars/*"
TARGET_CLIENT="ar.edu.itba.pod.tpe1.client.admin.AdminClient"




# Verificar que la acción sea una de las permitidas
case "$ACTION" in
    addSector)
        if [ -z "$SECTOR" ]; then
            echo "Debe especificar el nombre del sector para agregarlo."
            show_help
            exit 1
        fi

        tar -xzf "../client/target/tpe1-g7-client-2024.1Q-bin.tar.gz" -C "../client/target/"
        java -cp "$CLIENT_JARS"  "$TARGET_CLIENT" "$SERVER_ADDRESS" addSector "$SECTOR"

#        java -jar "$CLIENT_JAR" "$TARGET_CLIENT"
        ;;
    addCounters)
        if [ -z "$SECTOR" ]  || [ -z "$COUNTERS" ]; then
            echo "Debe especificar el nombre del sector y la cantidad de mostradores para agregarlos."
            show_help
            exit 1
        fi
        tar -xzf "../client/target/tpe1-g7-client-2024.1Q-bin.tar.gz" -C "../client/target/"
        java -cp "$CLIENT_JARS"  "$TARGET_CLIENT" "$SERVER_ADDRESS" addSector "$SECTOR" "$COUNTERS"
        ;;
    manifest)
        if [ -z "$IN_PATH" ]; then
            echo "Debe especificar la ruta del archivo CSV de entrada."
            show_help
            exit 1
        fi
        tar -xzf "../client/target/tpe1-g7-client-2024.1Q-bin.tar.gz" -C "../client/target/"
        java -cp "$CLIENT_JARS"  "$TARGET_CLIENT" "$SERVER_ADDRESS" addSector "$IN_PATH"
        ;;
    *)
        echo "Acción desconocida: $ACTION"
        show_help
        exit 1
        ;;
esac
