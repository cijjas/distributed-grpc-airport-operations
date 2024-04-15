#!/bin/bash

# sh adminClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dsector=sectorName | -Dcounters=counterCount | -DinPath=manifestPath ]

## addSector -Dsector=sectorName
## addCounters -Dsector=sectorName -Dcounters=counterCount
## manifest -DinPath=manifestPath




CLIENT_JARS="../client/target/tpe1-g7-client-2024.1Q/lib/jars/*"
TARGET_CLIENT="ar.edu.itba.pod.tpe1.client.admin.AdminClient"


tar -xzf "../client/target/tpe1-g7-client-2024.1Q-bin.tar.gz" -C "../client/target/"
java -cp "$CLIENT_JARS"  "$TARGET_CLIENT" "$@"

