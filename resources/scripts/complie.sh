#!/bin/bash

cd ../..;
mvn clean package;
# shellcheck disable=SC2164
cd resources/scripts;