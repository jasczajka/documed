#!/bin/bash

CONTAINER_NAME="local-postgres"
IMAGE="custom-postgres"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_INIT_PATH="$SCRIPT_DIR/../../Configuration/ansible/queries/documed_ddl.sql"
SQL_TEST_DATA_PATH="$SCRIPT_DIR/../../Configuration/ansible/queries/initial_load_test_data.sql"
SQL_CRON_PATH="$SCRIPT_DIR/../../Configuration/ansible/queries/documed_cron_jobs_local_db.sql"

INIT_SQL_ABS="$(realpath "$SQL_INIT_PATH")"
TEST_DATA_SQL_ABS="$(realpath "$SQL_TEST_DATA_PATH")"
CRON_SQL_ABS="$(realpath "$SQL_CRON_PATH")"

DB_USER="admin"
DB_PASSWORD="4444"
DB_NAME="prod_db"

[[ -f "$INIT_SQL_ABS" ]] || { echo "SQL init file not found: $SQL_INIT_PATH"; exit 1; }
[[ -f "$TEST_DATA_SQL_ABS" ]] || { echo "Test data SQL file not found: $SQL_TEST_DATA_PATH"; exit 1; }
[[ -f "$CRON_SQL_ABS" ]] || { echo "Cron SQL file not found: $SQL_CRON_PATH"; exit 1; }


# Build the custom Postgres image from Dockerfile if not already built
if [[ "$(docker images -q $IMAGE)" == "" ]]; then
  echo "Building custom Postgres image..."
  docker build -t $IMAGE ./local-db
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
    -p 5432:5432 \
    -v pgdata:/var/lib/postgresql/data \
    -v "$INIT_SQL_ABS":/docker-entrypoint-initdb.d/init.sql \
    -d $IMAGE
fi

echo "Waiting for PostgreSQL to be ready..."
until docker exec $CONTAINER_NAME pg_isready -U $DB_USER -d $DB_NAME -t 60; do
  echo "Waiting for PostgreSQL to start..."
  sleep 2
done


echo "Loading test data..."
cat "$TEST_DATA_SQL_ABS" \
  | docker exec -i $CONTAINER_NAME \
      psql -U $DB_USER -d $DB_NAME


echo "Scheduling cron jobs..."
cat "$CRON_SQL_ABS" \
  | docker exec -i $CONTAINER_NAME \
      psql -U $DB_USER -d postgres
echo "Done!"
