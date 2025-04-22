#!/bin/bash

CONTAINER_NAME="local-postgres"
IMAGE="postgres:16"
SQL_INIT_PATH="configuration/ansible/queries/documed_ddl.sql"

INIT_SQL_ABS="$(pwd)/$SQL_INIT_PATH"

DB_USER="admin"
DB_PASSWORD="4444"
DB_NAME="prod_db"

if [ ! -f "$INIT_SQL_ABS" ]; then
  echo "SQL init file not found at $SQL_INIT_PATH"
  exit 1
fi

if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
  if [ "$(docker inspect -f '{{.State.Running}}' $CONTAINER_NAME)" == "true" ]; then
    echo "Postgres container '$CONTAINER_NAME' is already running."
  else
    echo "Starting existing container '$CONTAINER_NAME'..."
    docker start $CONTAINER_NAME
  fi
else
  echo "Creating and starting new Postgres container '$CONTAINER_NAME'..."
  docker run --name $CONTAINER_NAME \
    -e POSTGRES_USER=$DB_USER \
    -e POSTGRES_PASSWORD=$DB_PASSWORD \
    -e POSTGRES_DB=$DB_NAME \
    -p 5433:5432 \
    -v pgdata:/var/lib/postgresql/data \
    -v "$INIT_SQL_ABS":/docker-entrypoint-initdb.d/init.sql \
    -d $IMAGE
fi
