Terraform — Local Docker Deployment
=====================================

This Terraform configuration manages the full Portfolio stack using the
[Docker provider](https://registry.terraform.io/providers/kreuzwerker/docker)
instead of shelling out to `docker compose`.

Prerequisites
-------------
- [Terraform](https://developer.hashicorp.com/terraform/downloads) >= 1.0
- Docker Desktop (or Docker engine) running locally
- Environment variables set (or update `terraform.tfvars`):
  - `TF_VAR_admin_username` – admin login username
  - `TF_VAR_admin_password` – admin login password

Usage
-----
```bash
cd infra/terraform

# Initialise (downloads the Docker provider plugin)
terraform init

# Preview what will be created
terraform plan

# Build images, create network/volumes, start all containers
terraform apply

# Tear down the entire stack (containers, network, volumes)
terraform destroy
```

What is managed
---------------
| Resource  | Description                          |
|-----------|--------------------------------------|
| Network   | `portfolio-network` (bridge)         |
| Volumes   | `postgres-data`, `grafana-storage`   |
| Images    | All 8 Docker images (build or pull)  |
| Containers| App, PostgreSQL, Redis, Prometheus, Grafana, Loki, Tempo, Promtail |

Differences from docker-compose
-------------------------------
- Containers are managed individually rather than as a Compose project.
- Health-based dependency ordering is emulated with a `null_resource` waiter
  for PostgreSQL and Redis before the app starts.
- The app image is rebuilt only when `Dockerfile` or `build.gradle` changes
  (driven by Terraform's `triggers`).
- Run `terraform destroy` to remove all managed resources.

Access points
-------------
| Service    | URL                          |
|------------|------------------------------|
| App        | http://localhost:8081        |
| Prometheus | http://localhost:9090        |
| Grafana    | http://localhost:3000        |
| Loki       | http://localhost:3100        |
| Tempo      | http://localhost:3200        |
