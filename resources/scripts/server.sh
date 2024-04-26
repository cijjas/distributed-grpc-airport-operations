#!/bin/bash

SERVER_JARS="../../server/target/tpe1-g7-server-2024.1Q/lib/jars/*"
TARGET_SERVER="ar.edu.itba.pod.tpe1.server.Server"
TARGET_DIR="../../server/target/tpe1-g7-server-2024.1Q"

if [ ! -d "$TARGET_DIR" ]; then
    tar -xzf "../../server/target/tpe1-g7-server-2024.1Q-bin.tar.gz" -C "../../server/target/"
fi

java -cp "$SERVER_JARS" "$TARGET_SERVER" "$@"
