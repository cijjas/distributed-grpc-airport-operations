

# Define server address
SERVER="localhost:50051"
LINE="----------------------------------------"
MANIFEST_PATH="../manifest.csv"


sh adminClient.sh -DserverAddress=$SERVER -Daction=manifest -DinPath=$MANIFEST_PATH
sh adminClient.sh -DserverAddress=$SERVER -Daction=addSector -Dsector=A
