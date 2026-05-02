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
