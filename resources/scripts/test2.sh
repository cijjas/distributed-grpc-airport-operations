#!/bin/bash



# 1.1. sh adminClient.sh -DserverAddress=10.6.0.1:50051 -Daction=addSector -Dsector=C
# - Ya existe un sector con ese nombre

# 1.2. sh adminClient.sh -DserverAddress=10.6.0.1:50051 -Daction=addCounters -Dsector=C -Dcounters=3
# - No existe un sector con el nombre indicado
# - La cantidad de mostradores indicada no es positiva

# 1.3. sh adminClient.sh -DserverAddress=10.6.0.1:50051 -Daction=manifest -DinPath=../manifest.csv
# - Ya se agregó un pasajero con ese código de reserva
# - Ya se agregó un vuelo con ese código pero con otra aerolínea

# 2.1. sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=listSectors
# - No existen sectores en el aeropuerto

# 2.2. sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=listCounters -Dsector=C -DcounterFrom=2 -DcounterTo=5
# 2.2.  sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=listCounters -Dsector=C -DcounterFrom=10 -DcounterTo=20
# - No existe un sector con ese nombre
# - fromVal y toVal no forman un rango de uno o más mostradores

# 2.3.  sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=assignCounters -Dsector=C -Dflights='AA123|AA124|AA125' -Dairline=AmericanAirlines -DcounterCount=2
# 2.3.  sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=assignCounters -Dsector=C -Dflights='AA123|AA124|AA125' -Dairline=AmericanAirlines -DcounterCount=2
# - No existe un sector con ese nombre
# - No se agregaron pasajeros esperados con el código de vuelo, para al menos uno de los vuelos solicitados
# - Se agregaron pasajeros esperados con el código de vuelo pero con otra aerolínea, para al menos uno de los vuelos solicitados
# - Ya existe al menos un mostrador asignado para al menos uno de los vuelos solicitados (no se permiten agrandar rangos de mostradores asignados)
# - Ya existe una solicitud pendiente de un rango de mostradores para al menos uno de los vuelos solicitados (no se permiten reiterar asignaciones pendientes)
# - Ya se asignó y luego se liberó un rango de mostradores para al menos uno de los vuelos solicitados (no se puede iniciar el check-in de un vuelo dos o más veces)

# 2.4.  sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=freeCounters -Dsector=C -DcounterFrom=3 -Dairline=AmericanAirlines
# - No existe un sector con ese nombre
# - El rango de mostradores no existe en ese sector (los mostradores no están asignados)
# - El rango de mostradores existe pero no corresponde a esa aerolínea (una aerolínea sólo puede liberar sus rangos)
# - Existen pasajeros esperando a ser atendidos en la cola del rango

# 2.5 sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=checkinCounters -Dsector=C -DcounterFrom=3 -Dairline=AmericanAirlines
# - No existe un sector con ese nombre
# - El rango de mostradores no existe en ese sector (los mostradores no están asignados)
# - El rango de mostradores existe pero no corresponde a esa aerolínea

# 2.6. sh counterClient.sh -DserverAddress=10.6.0.1:50051 -Daction=listPendingAssignments -Dsector=C
# - No existe un sector con ese nombre

# 3.1. sh passengerClient.sh -DserverAddress=10.6.0.1:50051 -Daction=fetchCounter -Dbooking=XYZ345
# - No existe un pasajero esperado con ese código de reserva

# 3.2 sh passengerClient.sh -DserverAddress=10.6.0.1:50051 -Daction=passengerCheckin -Dbooking=ABC123 -Dsector=C -Dcounter=3
# - No existe una reserva con ese código
# - No existe un sector con ese nombre
# - El número de mostrador no corresponde con el inicio de un rango de mostradores asignado a la aerolínea que esté aceptando pasajeros del vuelo de la reserva
# - El pasajero ya ingresó en la cola del rango
# - El pasajero ya realizó el check-in de la reserva

# 3.3 sh passengerClient.sh -DserverAddress=10.6.0.1:50051 -Daction=passengerStatus -Dbooking=ABC123
# - No existe un pasajero esperado con ese código de reserva
# - No hay un rango de mostradores asignados que atiendan pasajeros del vuelo correspondiente al código de reserva indicado


# 4.1. sh eventsClient.sh -DserverAddress=10.6.0.1:50051 -Daction=register -Dairline=AmericanAirlines
# - No existe al menos un pasajero esperado con un vuelo correspondiente a la aerolínea
# - La aerolínea ya se registró para recibir notificaciones

# 4.2. sh eventsClient.sh -DserverAddress=10.6.0.1:50051 -Daction=unregister -Dairline=AmericanAirlines
# - La aerolínea no se registró para recibir notificaciones


# 5.1. sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=counters -DoutPath=../query1.txt
# 5.1. sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=counters -DoutPath=../query1.txt -Dsector=C
# 5.1  sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=counters -DoutPath=../query1.txt -Dsector=Z
# - No se agregó al menos un mostrador en el aeropuerto y en ese caso el archivo de salida no debe crearse


# 5.2. sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=checkins -DoutPath=../query2.txt
# 5.2. sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=checkins -DoutPath=../query2.txt -Dsector=C
# 5.2  sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=checkins -DoutPath=../query2.txt -Dairline=AirCanada
# 5.2  sh queryClient.sh -DserverAddress=10.6.0.1:50051 -Daction=checkins -DoutPath=../query2.txt -Dairline=AirCanada
# - No se realizó ningún check-in hasta el momento y en ese caso el archivo de salida no debe crearse

# Define server address
SERVER_ADDRESS="localhost:50051"

# Define paths for CSV and output files
MANIFEST_PATH="../manifest.csv"

QUERY1_OUTPUT_PATH_0="../query1_0.txt"
QUERY1_OUTPUT_PATH_1="../query1_1.txt"
QUERY1_OUTPUT_PATH_2="../query1_2.txt"
QUERY1_OUTPUT_PATH_NOT_EXISTS="../query1_not.txt"

QUERY2_OUTPUT_PATH_0="../query2_0.txt"
QUERY2_OUTPUT_PATH_1="../query2_1.txt"
QUERY2_OUTPUT_PATH_2="../query2_2.txt"
QUERY2_OUTPUT_PATH_NOT_EXISTS="../query2_not.txt"

LINE="-----------------------------------------------------------------------------------------------------"
ERRORS_LINE="<><><><><><><><><><><><><><><><>ERRORS<><><><><><><><><><><><><><><><><><>"


echo $LINE
echo "Starting test cases..."

echo $LINE
echo "<!> 2.1 aeropuerto no sectores"
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listSectors

# 1.1 Add Sector
echo "1.1. Testing Add Sector..."
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=C

echo $LINE
echo "<!> 5.1 query vacio"
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=counters -DoutPath=$QUERY1_OUTPUT_PATH_NOT_EXISTS -Dsector=C
echo "<!> 5.2 query vacio"
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkins -DoutPath=$QUERY2_OUTPUT_PATH_NOT_EXISTS -Dsector=C -Dairline=AirCanada

echo $LINE

sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=A
# Expected error: Sector already exists
echo $ERRORS_LINE
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=C
echo $LINE

# 1.2 Add Counters
echo "1.2. Testing Add Counters..."
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=C -Dcounters=1
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=C -Dcounters=4
# Expected errors: Sector does not exist, Count is not positive
echo $ERRORS_LINE
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=Z -Dcounters=3
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=C -Dcounters=-1
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=C -Dcounters=0
echo $LINE

# 1.3 Manifest
echo "1.3. Testing Manifest..."
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=manifest -DinPath=$MANIFEST_PATH
# Expected errors: Passenger already added, Flight code reused with different airline
echo $ERRORS_LINE
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=manifest -DinPath=$MANIFEST_PATH
echo $LINE

# 2.1 List Sectors
echo "2.1. Testing List Sectors..."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listSectors
# Expected error: No sectors exist
echo $LINE

# 2.2 List Counters
echo "2.2. Testing List Counters..."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listCounters -Dsector=C -DcounterFrom=2 -DcounterTo=5
# Expected errors: Sector does not exist, Invalid range
echo $ERRORS_LINE
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listCounters -Dsector=Z -DcounterFrom=2 -DcounterTo=5
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listCounters -Dsector=C -DcounterFrom=4 -DcounterTo=2
echo $LINE

# 2.3 Assign Counters
echo "2.3. Testing Assign Counters..."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='AA123|AA124|AA125' -Dairline=AmericanAirlines -DcounterCount=2
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='UA123' -Dairline=UnitedAirlines -DcounterCount=3
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='LT001|LT002' -Dairline=Lufthansa -DcounterCount=2 # se agrega a pending
# Expected errors:1. Sector does not exist,2. No passengers added,3. Flight code reused,4. Counter already assigned,5. Pending assignment exists,6. Counter released
echo $ERRORS_LINE
# - No existe un sector con ese nombre
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=Z -Dflights='AA123|AA124|AA125' -Dairline=AmericanAirlines -DcounterCount=2
# - No se agregaron pasajeros esperados con el código de vuelo, para al menos uno de los vuelos solicitados
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='AA123|AA124|AA125|AA126' -Dairline=AmericanAirlines -DcounterCount=2
# - Se agregaron pasajeros esperados con el código de vuelo pero con otra aerolínea, para al menos uno de los vuelos solicitados
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='AA123|AA124|AA125|BA789' -Dairline=AmericanAirlines -DcounterCount=2
# - Ya existe al menos un mostrador asignado para al menos uno de los vuelos solicitados (no se permiten agrandar rangos de mostradores asignados)
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='AA123|AA124|AA125' -Dairline=AmericanAirlines -DcounterCount=3 # TODO deberia imprimir un mejor mensaeje
# - Ya existe una solicitud pendiente de un rango de mostradores para al menos uno de los vuelos solicitados (no se permiten reiterar asignaciones pendientes)
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='LT001' -Dairline=AmericanAirlines -DcounterCount=2 # TODO no tira exactamente el mismo error
# - Ya se asignó y luego se liberó un rango de mostradores para al menos uno de los vuelos solicitados (no se puede iniciar el check-in de un vuelo dos o más veces)
echo $LINE


# 2.5 Check-in Counters
echo "2.5. Testing Check-in Counters..."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=C -DcounterFrom=1 -Dairline=AmericanAirlines
# Expected errors: Sector does not exist, Range does not exist, Wrong airline
echo $ERRORS_LINE
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=Z -DcounterFrom=3 -Dairline=AmericanAirlines
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=C -DcounterFrom=2 -Dairline=AmericanAirlines
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=C -DcounterFrom=1 -Dairline=AirCanada
echo $LINE

# 2.6 List Pending Assignments
echo "2.6. Testing List Pending Assignments..."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listPendingAssignments -Dsector=C
# Expected error: Sector does not exist
echo $ERRORS_LINE
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listPendingAssignments -Dsector=Z
echo $LINE

# 3.1 Fetch Counter
echo "3.1. Testing Fetch Counter..."
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=fetchCounter -Dbooking=XYZ238
# Expected error: No expected passenger with booking code
echo $ERRORS_LINE
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=fetchCounter -Dbooking=XYZ999
echo $LINE

# 3.2 Passenger Check-in
echo "3.2. Testing Passenger Check-in..."
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ234 -Dsector=C -Dcounter=1 # TODO fijarse bien rangees
# Expected errors:1. No booking,2. Sector does not exist,3. Counter number mismatch,4. Already in queue,5. Check-in already done
echo $ERRORS_LINE
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ999 -Dsector=C -Dcounter=1
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ234 -Dsector=Z -Dcounter=1
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ234 -Dsector=C -Dcounter=2 # TODO counter number is wrong
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ234 -Dsector=C -Dcounter=1 # TODO already in line error mas especifico
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ234 -Dsector=C -Dcounter=1 # TODO already checked in error mas especifico
echo $LINE
echo $LINE
echo "<?> 2.5 realizar checkin"
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=C -DcounterFrom=1 -Dairline=AmericanAirlines
echo $LINE


## 3.3 Check-in Status
#echo "3.3. Testing Check-in Status..."
#sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerStatus -Dbooking=ABC123
#sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerStatus -Dbooking=XYZ234
## Expected errors: No expected passenger, No counter range for passenger
#sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerStatus -Dbooking=XYZ999
#sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerStatus -Dbooking=JKL012
#echo $LINE
#
## 4.1 Register Airline
#echo "4.1. Testing Register Airline..."
#sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=register -Dairline=AmericanAirlines
## Expected errors: No expected passengers, Airline already registered
#sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=register -Dairline=AmericanAirlines
#sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=register -Dairline=AerolineasArgentinas
#
## 4.2 Unregister Airline
#echo "4.2. Testing Unregister Airline..."
#sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=unregister -Dairline=AmericanAirlines
## Expected error: Airline not registered
#sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=unregister -Dairline=AirCanada

# 5.1 Query Counters
echo "5.1. Testing Query Counters..."
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=counters -DoutPath=$QUERY1_OUTPUT_PATH_0
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=counters -DoutPath=$QUERY1_OUTPUT_PATH_1 -Dsector=C
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=counters -DoutPath=$QUERY1_OUTPUT_PATH_2 -Dsector=A
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=counters -DoutPath=$QUERY1_OUTPUT_PATH_2 -Dsector=Z
echo $LINE

# 5.2 Query Check-ins
echo "5.2. Testing Query Check-ins..."
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkins -DoutPath=$QUERY2_OUTPUT_PATH_0
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkins -DoutPath=$QUERY2_OUTPUT_PATH_1 -Dsector=C
sh queryClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkins -DoutPath=$QUERY2_OUTPUT_PATH_2 -Dairline=AmericanAirlines
# Expected errors: No check-ins performed
echo $LINE
############

# 2.4 Free Counters
echo "2.4. Testing Free Counters..."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=C -DcounterFrom=1 -Dairline=AmericanAirlines
# Expected errors: Sector does not exist, Range does not exist in sector, Wrong airline, Passengers waiting
echo $ERRORS_LINE
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=Z -DcounterFrom=1 -Dairline=AmericanAirlines
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=A -DcounterFrom=1 -Dairline=AmericanAirlines
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=C -DcounterFrom=3 -Dairline=AmericanAirlines
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=C -DcounterFrom=1 -Dairline=AirCanada
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=C -DcounterFrom=2 -Dairline=AmericanAirlines

echo "--- deberia ser el ultimo error de 2.3"
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=C -Dflights='AA123' -Dairline=AmericanAirlines -DcounterCount=2
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listCounters -Dsector=C -DcounterFrom=1 -DcounterTo=5
echo $LINE
