#!/bin/bash

echo "Arrêt de tous les microservices Motus..."

# Tue tous les processus Spring Boot sur les ports 8080-8084
for port in 8080 8081 8082 8083 8084; do
    PID=$(lsof -ti:$port)
    if [ -n "$PID" ]; then
        echo "Arrêt du service sur le port $port (PID: $PID)"
        kill -9 $PID
    fi
done

echo "Tous les services sont arrêtés."
