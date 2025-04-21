#!/bin/bash

CONTAINER_NAME="local-postgres"
VOLUME_NAME="pgdata"

echo "🗑 Removing container '$CONTAINER_NAME' (if exists)..."
docker rm -f $CONTAINER_NAME 2>/dev/null || echo "No container to remove."

echo "🧹 Removing volume '$VOLUME_NAME'..."
docker volume rm $VOLUME_NAME 2>/dev/null || echo "No volume to remove."
