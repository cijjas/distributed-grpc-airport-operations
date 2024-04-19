#!/bin/bash


sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addSector -Dsector=A;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addSector -Dsector=B;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addSector -Dsector=C;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addSector -Dsector=X;

sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=B -Dcounters=3;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=A -Dcounters=5;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=B -Dcounters=7;

sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=A -Dcounters=21;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=C -Dcounters=9;

sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=X -Dcounters=50;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=C -Dcounters=10;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=B -Dcounters=1;

sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=C -Dcounters=2;
sh adminClient.sh -DserverAddress=localhost:50051 -Daction=addCounters -Dsector=A -Dcounters=100;


sh adminClient.sh -DserverAddress=localhost:50051 -Daction=manifest -DinPath=../manifest.csv;
sh counterClient.sh -DserverAddress=localhost:50051 -Daction=listSectors;
