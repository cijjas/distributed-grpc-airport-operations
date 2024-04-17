#!/bin/bash

CLIENT_JARS="../client/target/tpe1-g7-client-2024.1Q/lib/jars/*"
TARGET_CLIENT="ar.edu.itba.pod.tpe1.client.events.EventsClient"


tar -xzf "../client/target/tpe1-g7-client-2024.1Q-bin.tar.gz" -C "../client/target/"
java -cp "$CLIENT_JARS"  "$TARGET_CLIENT" "$@"

