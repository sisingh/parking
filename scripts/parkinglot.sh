#!/bin/bash
if [ $# -ne 2 ]; then
touch ~/Infile.txt
echo "create_parking_lot 4
park KA-05-MH-1384 Silver
park HR-26-CW-0181 Black
park MG-14-N-2493 Black
park KA-05-MH-9702 Gold
leave 2
status
park KA-20-M-2222 Black
park DL-12-AA-9999 White
registration_numbers_for_cars_with_colour Brown
slot_numbers_for_cars_with_colour Black
slot_number_for_registration_number KA-05-MH-1384 
slot_number_for_registration_number MH-04-AY-1111" > ~/Infile.txt
else
  echo "Usage: $0 [inputfile]"
  exit 1
fi
java -jar ../target/parking-1.0-SNAPSHOT.jar ~/Infile.txt

