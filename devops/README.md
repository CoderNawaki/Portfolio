# Deployment Manifests

This folder keeps the Kubernetes manifests for the portfolio stack.

## Layout

- `kubernetes/namespace.yaml`: Dedicated namespace for the app
- `kubernetes/configmap.yaml`: Non-secret runtime configuration
- `kubernetes/secret.yaml`: Placeholder secret values that must be replaced for a real deployment
- `kubernetes/postgres-statefulset.yaml`: PostgreSQL backing store
- `kubernetes/redis-deployment.yaml`: Redis cache and rate-limit store
- `kubernetes/app-deployment.yaml`: Spring Boot application
- `kubernetes/app-service.yaml`: ClusterIP service for the app
- `kubernetes/app-ingress.yaml`: Ingress entry point
- `kubernetes/app-hpa.yaml`: Horizontal scaling policy

## Apply

```bash
kubectl apply -k devops/kubernetes
```

Replace the placeholder values in `secret.yaml` before using these manifests outside local review or demo environments.

## Minikube

Use the Minikube overlay for local cluster testing:

```bash
minikube start
eval "$(minikube docker-env)"
docker build -t portfolio:minikube .
kubectl apply -k devops/minikube
minikube service -n portfolio portfolio-app --url
```

The Minikube overlay runs the app on a local `NodePort` and points `PORTFOLIO_SITE_URL` at `http://localhost:30080` so the generated metadata matches the cluster URL more closely.

## Docker Desktop Kubernetes

If you already use Docker Desktop, use the Docker Desktop overlay instead of Minikube:

```bash
docker build -t portfolio:docker-desktop .
kubectl apply -k devops/docker-desktop
kubectl -n portfolio get pods
kubectl -n portfolio get svc portfolio-app
```

Open the app at `http://localhost:30081`.

If the NodePort is not reachable in your Docker Desktop setup, use port-forwarding as a fallback:

```bash
kubectl -n portfolio port-forward svc/portfolio-app 8081:80
```
