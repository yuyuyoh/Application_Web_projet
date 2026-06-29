#!/bin/bash

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Démarrage de tous les microservices Motus..."

# Discovery (Eureka, 8761) — doit démarrer en premier
echo "Lancement discovery-service..."
cd "$BASE_DIR/discovery-service"
mvn spring-boot:run > "$BASE_DIR/logs/discovery.log" 2>&1 &
echo "PID discovery-service : $!"

sleep 10

# Dictionnaire (8082)
echo "Lancement dictionnaire-service..."
cd "$BASE_DIR/dictionnaire-service"
mvn spring-boot:run > "$BASE_DIR/logs/dictionnaire.log" 2>&1 &
echo "PID dictionnaire-service : $!"

sleep 5

# Joueur (8081)
echo "Lancement joueur-service..."
cd "$BASE_DIR/joueur-service"
mvn spring-boot:run > "$BASE_DIR/logs/joueur.log" 2>&1 &
echo "PID joueur-service : $!"

sleep 5

# Score (8084)
echo "Lancement score-service..."
cd "$BASE_DIR/score-service"
mvn spring-boot:run > "$BASE_DIR/logs/score.log" 2>&1 &
echo "PID score-service : $!"

sleep 5

# Auth (8085)
echo "Lancement auth-service..."
cd "$BASE_DIR/auth-service"
mvn spring-boot:run > "$BASE_DIR/logs/auth.log" 2>&1 &
echo "PID auth-service : $!"

sleep 5

# Partie (8083)
echo "Lancement partie-service..."
cd "$BASE_DIR/partie-service"
mvn spring-boot:run > "$BASE_DIR/logs/partie.log" 2>&1 &
echo "PID partie-service : $!"

sleep 5

# API Gateway (8080)
echo "Lancement api-gateway..."
cd "$BASE_DIR/api-gateway"
mvn spring-boot:run > "$BASE_DIR/logs/gateway.log" 2>&1 &
echo "PID api-gateway : $!"

echo ""
echo "Tous les services sont en cours de démarrage !"
echo "Attends ~60 secondes puis ouvre : http://localhost:8080/swagger-ui.html"
echo ""
echo "Eureka dashboard      : http://localhost:8761"
echo "Swagger central       : http://localhost:8080/swagger-ui.html"
echo ""
echo "Pour voir les logs    : tail -f logs/partie.log"
echo "Pour tout arrêter     : ./stop-all.sh"
