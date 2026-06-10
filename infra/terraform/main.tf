terraform {
  required_version = ">= 1.0"
}

resource "null_resource" "deploy" {
  # change triggers to force re-run when apply is called
  triggers = {
    always_run = timestamp()
  }

  provisioner "local-exec" {
    # pull images then start compose in repo root
    command = "docker compose -f \"${path.module}/../../docker-compose.yml\" pull && docker compose -f \"${path.module}/../../docker-compose.yml\" up -d"
  }

  provisioner "local-exec" {
    when    = "destroy"
    command = "docker compose -f \"${path.module}/../../docker-compose.yml\" down"
  }
}
