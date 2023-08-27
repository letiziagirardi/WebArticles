#!/bin/bash

build_service() {
  service=$1
  echo "Building $service"
  mvn clean install
  cp target/*.jar docker/app.jar
}


# create_user(){
#   # Prompt the user for userId
#   read -p "Please enter userId: " userId
#
#   # Prompt the user for password
#   read -s -p "Please enter password: " password
#   echo
#
#   # Make the curl request with user input
#   curl -X POST "http://localhost:8080/gestuser/utenti/inserisci" \
#        -H "Content-Type: application/json" \
#        -d "{
#            \"userId\": \"$userId\",
#            \"password\": \"$password\",
#            \"attivo\": \"Si\",
#            \"ruoli\": [
#                \"USER\",
#                \"ADMIN\"
#            ]
#        }" \
#        -u "Admin:MagicaBula_2018"
#
# }

build_main() {
  docker compose down
#  docker builder prune -a -f  forcefully clean up unused build cache
  docker image prune -a
  echo "docker compose down: clean up"

  cd articles_web_service_v2
  build_service "articles"

  cd ../priceart_web_service_v2
  build_service "price"

  cd ../promo_web_service_v2
  build_service "promo"

  cd ../gestuser
  build_service "gestuser"

  cd ../gateway
  build_service "gateway"

  cd ..
  docker compose up -d

  # # Prompt the user for input
  # echo "Please enter the command: "
  # read command
  # echo "command $command"
  #





}

case $1 in
  build)
    build_main
    ;;
  a)
    echo "Romanian"
    ;;
  Italy|"San Marino"|Switzerland|"Vatican City")
    echo "Italian"
    ;;
  *)
    echo "unknown"
    ;;
esac

# case $command in
#   createUser)
#     create_user
#     ;;
#   )
#     echo "Romanian"
#     ;;
#   Italy|"San Marino"|Switzerland|"Vatican City")
#     echo "Italian"
#     ;;
#   *)
#     echo "unknown"
#     ;;
# esac
