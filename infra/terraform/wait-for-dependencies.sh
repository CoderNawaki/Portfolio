#!/bin/sh
set -e

echo "Waiting for PostgreSQL..."
until docker exec portfolio-db pg_isready -U portfolio_user -d portfolio 2>/dev/null; do
  sleep 2
done
echo "PostgreSQL is ready."

echo "Waiting for Redis..."
until docker exec portfolio-redis redis-cli ping 2>/dev/null | grep -q PONG; do
  sleep 2
done
echo "Redis is ready."
