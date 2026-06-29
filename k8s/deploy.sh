#!/bin/bash
set -e

echo "=== Motus — Déploiement Kubernetes (Minikube) ==="

# 1. Vérifier que minikube est démarré
if ! minikube status | grep -q "Running"; then
  echo "Démarrage de Minikube..."
  minikube start --memory=3500 --cpus=2
fi

# 2. Pointer Docker vers le daemon Minikube (pour charger les images localement)
echo ""
echo "Connexion au daemon Docker de Minikube..."
eval $(minikube docker-env)

# 3. Build de toutes les images
echo ""
echo "Build des images Docker..."
SERVICES=(discovery-service config-service dictionnaire-service joueur-service score-service partie-service auth-service api-gateway frontend)

for svc in "${SERVICES[@]}"; do
  echo "  → Build motus/$svc:latest"
  docker build -t "motus/$svc:latest" "../$svc" -q
done

# 4. Appliquer les manifests Kubernetes
echo ""
echo "Déploiement des manifests Kubernetes..."
kubectl apply -f 00-namespace.yaml
kubectl apply -f 01-discovery-service.yaml
kubectl apply -f 02-config-service.yaml
kubectl apply -f 03-dictionnaire-service.yaml
kubectl apply -f 04-joueur-service.yaml
kubectl apply -f 05-score-service.yaml
kubectl apply -f 06-partie-service.yaml
kubectl apply -f 07-auth-service.yaml
kubectl apply -f 08-api-gateway.yaml
kubectl apply -f 09-frontend.yaml

# 5. Afficher les URLs et laisser Kubernetes démarrer en arrière-plan
MINIKUBE_IP=$(minikube ip)
echo ""
echo "=== Manifests appliqués ==="
echo ""
echo "Les pods démarrent en arrière-plan (3-5 min)."
echo "Pour suivre l'avancement :"
echo "  kubectl get pods -n motus"
echo ""
echo "URLs d'accès (disponibles quand tous les pods sont Running) :"
echo "  Frontend    → http://$MINIKUBE_IP:30000"
echo "  API Gateway → http://$MINIKUBE_IP:30080"
echo "  Swagger     → http://$MINIKUBE_IP:30080/swagger-ui.html"
echo "  Eureka      → http://$MINIKUBE_IP:30761"
echo ""
echo "Etat actuel des pods :"
kubectl get pods -n motus
