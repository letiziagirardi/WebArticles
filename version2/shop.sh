#!/bin/bash

build_service() {
  service=$1
  echo "Building $service"
  mvn clean install
  cp target/*.jar docker/app.jar
}

build_main() {
  docker compose down
  docker image prune -a -f
  echo "docker compose down: clean up"

  if [ -d "database" ]; then
    rm -rd "database"
  fi

  mkdir "database"
  unzip DB/database

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

  pip install bcrypt


}

case $1 in
  build)
    build_main
    ;;
  *)
    echo "unknown"
    ;;
esac
