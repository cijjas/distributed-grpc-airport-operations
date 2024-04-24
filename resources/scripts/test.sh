#!/bin/bash

# Dirección y puerto del servidor
SERVER_ADDRESS="localhost:50051"

sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=C

# Función para agregar mostradores en un sector específico
add_counters() {
    sector=$1
    counters=$2
    sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=$sector -Dcounters=$counters
}

# Simular múltiples usuarios intentando agregar mostradores al mismo tiempo
for i in {1..40}; do
    # Cada usuario intenta agregar un rango de 3 mostradores en el sector "C"
    add_counters "C" 3 &
done

wait
echo "Completed all addCounter requests."
