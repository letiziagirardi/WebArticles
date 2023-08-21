#!/bin/bash

build_service() {
  service=$1
  echo "Building $service"
  mvn clean install
  cp target/*.jar docker/app.jar
}

build_mongodb(){
  sleep 10
  # mongosh --host localhost --port 27018 --authenticationDatabase admin < createMongoUsers.txt
  mongosh --host localhost --port 27018 -u admin -p 123_Stella --authenticationDatabase admin < createMongoUsers.txt
}

build_main() {
  docker compose down
  docker builder prune -a -f
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

  # building MongoDB gestuser
  build_mongodb


}

case $1 in
  build)
    build_main
    ;;
  Romania|Moldova)
    echo "Romanian"
    ;;
  Italy|"San Marino"|Switzerland|"Vatican City")
    echo "Italian"
    ;;
  *)
    echo "unknown"
    ;;
esac
