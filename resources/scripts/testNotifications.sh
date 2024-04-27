#!/bin/bash
# Test the Events Service Register for events for AirCanada and AmericanAirlines on other terminals
SERVER_ADDRESS=localhost:50051


echo "Creating Counters"
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=A
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=C
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=D
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addSector -Dsector=Z
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=A -Dcounters=1
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=C -Dcounters=3
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=D -Dcounters=2
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=C -Dcounters=2
sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=manifest -DinPath=../manifest.csv

# Test the Counter Reservation Service
echo "Reservations....."
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listSectors
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listCounters -Dsector=C -DcounterFrom=2 -DcounterTo=50
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=A -Dflights='AC987' -Dairline=AirCanada -DcounterCount=1
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=assignCounters -Dsector=A -Dflights='AA123' -Dairline=AmericanAirlines -DcounterCount=2

sh adminClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=addCounters -Dsector=A -Dcounters=2

sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=listCounters -Dsector=C -DcounterFrom=1 -DcounterTo=50
# Test the Passenger Check-in Service
echo "Testing Passenger Check-in....."
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerStatus -Dbooking=XYZ234
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=fetchCounter -Dbooking=XYZ234
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=ABC123 -Dsector=A -Dcounter=1
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerCheckin -Dbooking=XYZ234 -Dsector=A -Dcounter=9
sh passengerClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=passengerStatus -Dbooking=ABC123

echo "---------------"
echo "Checking in"
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=A -DcounterFrom=1 -Dairline=AirCanada
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=checkinCounters -Dsector=A -DcounterFrom=9 -Dairline=AmericanAirlines



echo "---------------"
echo "Freeing Counters"
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=A -DcounterFrom=9 -Dairline=AmericanAirlines
sh counterClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=freeCounters -Dsector=A -DcounterFrom=1 -Dairline=AirCanada

# End notification service
#sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=unregister -Dairline=AmericanAirlines
sh eventsClient.sh -DserverAddress=$SERVER_ADDRESS -Daction=unregister -Dairline=AirCanada

echo "All tests completed."
