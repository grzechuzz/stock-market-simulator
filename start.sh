#!/usr/bin/env sh
set -eu

if [ "$#" -ne 1 ]; then
  echo "Usage: ./start.sh <port>" >&2
  exit 1
fi

PORT="$1"

case "$PORT" in
  *[!0-9]* | "")
    echo "Port must be a number." >&2
    exit 1
    ;;
esac

if [ "$PORT" -lt 1 ] || [ "$PORT" -gt 65535 ]; then
  echo "Port must be between 1 and 65535." >&2
  exit 1
fi

if [ ! -f ".env" ]; then
  if [ ! -f ".env.example" ]; then
    echo ".env.example not found." >&2
    exit 1
  fi

  cp ".env.example" ".env"
  echo "Created .env from .env.example."
fi

export APP_PORT="$PORT"

if docker compose version >/dev/null 2>&1; then
  exec docker compose up --build
fi

if command -v docker-compose >/dev/null 2>&1; then
  exec docker-compose up --build
fi

echo "Docker Compose is not available." >&2
exit 1
