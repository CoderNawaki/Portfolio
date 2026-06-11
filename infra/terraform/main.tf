terraform {
  required_version = ">= 1.0"
}

resource "null_resource" "deploy" {
  # change triggers to force re-run when apply is called
  triggers = {
    always_run = timestamp()
  }

  provisioner "local-exec" {
    working_dir = "${path.module}/../.."
    command     = "docker compose pull && docker compose up -d"
  }

  provisioner "local-exec" {
    when        = destroy
    working_dir = "${path.module}/../.."
    command     = "docker compose down"
  }
}
