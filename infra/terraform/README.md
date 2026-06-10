Terraform to manage local docker-compose deployment

This Terraform configuration uses a null_resource with local-exec provisioners to run docker compose for this repository's docker-compose.yml.

Usage

1. Install Terraform (>= 1.0)
2. From repo root, run:

   cd infra/terraform
   terraform init
   terraform apply

This will run: docker compose -f <repo-root>/docker-compose.yml pull && docker compose -f <repo-root>/docker-compose.yml up -d

To tear down the compose stack:

   terraform destroy

Notes

- Terraform uses local-exec; it requires Docker and Docker Compose available on the machine running Terraform.
- State is stored locally by default. For collaboration, configure a remote backend (e.g., S3, Terraform Cloud).
- This is intentionally minimal (learning resource). Future iterations can use the Docker provider or Kubernetes provider as needed.
