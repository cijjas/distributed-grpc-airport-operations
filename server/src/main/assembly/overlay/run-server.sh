#!/bin/bash


MAIN_CLASS="ar.edu.itba.pod.tpe1.server.server.Server"

java  $JAVA_OPTS -cp 'lib/jars/*' $MAIN_CLASS "$@"
