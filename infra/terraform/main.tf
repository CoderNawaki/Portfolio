terraform {
  required_version = ">= 1.0"
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = var.docker_host
}

# ---------------------------------------------------------------------------
# Network
# ---------------------------------------------------------------------------
resource "docker_network" "portfolio" {
  name   = "portfolio-network"
  driver = "bridge"
}

# ---------------------------------------------------------------------------
# Volumes
# ---------------------------------------------------------------------------
resource "docker_volume" "postgres_data" {
  name = "postgres-data"
}

resource "docker_volume" "grafana_storage" {
  name = "grafana-storage"
}

# ---------------------------------------------------------------------------
# Images – pulled from registries
# ---------------------------------------------------------------------------
resource "docker_image" "postgres" {
  name         = "postgres:16-alpine"
  keep_locally = true
}

resource "docker_image" "redis" {
  name         = "redis:7-alpine"
  keep_locally = true
}

resource "docker_image" "prometheus" {
  name         = "prom/prometheus"
  keep_locally = true
}

resource "docker_image" "grafana" {
  name         = "grafana/grafana-oss"
  keep_locally = true
}

resource "docker_image" "loki" {
  name         = "grafana/loki:latest"
  keep_locally = true
}

resource "docker_image" "tempo" {
  name         = "grafana/tempo:2.3.1"
  keep_locally = true
}

resource "docker_image" "promtail" {
  name         = "grafana/promtail:latest"
  keep_locally = true
}

# ---------------------------------------------------------------------------
# Image – built locally from Dockerfile
# ---------------------------------------------------------------------------
resource "docker_image" "portfolio_app" {
  name = var.image_tag
  build {
    context    = "${path.module}/../.."
    dockerfile = "Dockerfile"
  }

  triggers = {
    dockerfile_sha1 = sha1(file("${path.module}/../../Dockerfile"))
    gradle_sha1     = sha1(join("", [for f in fileset("${path.module}/../..", "build.gradle") : file("${path.module}/../../${f}")]))
  }
}

# ---------------------------------------------------------------------------
# Containers – ordered to respect inter-dependencies
# ---------------------------------------------------------------------------
resource "docker_container" "db" {
  name  = "portfolio-db"
  image = docker_image.postgres.name

  env = [
    "POSTGRES_DB=portfolio",
    "POSTGRES_USER=portfolio_user",
    "POSTGRES_PASSWORD=portfolio_pass",
  ]

  volumes {
    volume_name    = docker_volume.postgres_data.name
    container_path = "/var/lib/postgresql/data"
  }

  networks_advanced {
    name = docker_network.portfolio.name
  }

  healthcheck {
    test     = ["CMD-SHELL", "pg_isready -U portfolio_user -d portfolio"]
    interval = "5s"
    timeout  = "5s"
    retries  = 5
  }

  restart = "unless-stopped"
}

resource "docker_container" "redis" {
  name  = "portfolio-redis"
  image = docker_image.redis.name

  networks_advanced {
    name = docker_network.portfolio.name
  }

  healthcheck {
    test     = ["CMD", "redis-cli", "ping"]
    interval = "5s"
    timeout  = "3s"
    retries  = 10
  }

  restart = "unless-stopped"
}

# Wait for PostgreSQL and Redis to be healthy before starting the app.
resource "null_resource" "wait_for_dependencies" {
  depends_on = [docker_container.db, docker_container.redis]

  provisioner "local-exec" {
    command = "${path.module}/wait-for-dependencies.sh"
  }

  triggers = {
    db_image    = docker_image.postgres.name
    redis_image = docker_image.redis.name
  }
}

resource "docker_container" "portfolio_app" {
  depends_on = [null_resource.wait_for_dependencies]

  name  = "portfolio-app"
  image = docker_image.portfolio_app.name

  ports {
    internal = 8081
    external = 8081
  }

  env = [
    "PORTFOLIO_ADMIN_USERNAME=${var.admin_username}",
    "PORTFOLIO_ADMIN_PASSWORD=${var.admin_password}",
    "SPRING_DATASOURCE_URL=jdbc:postgresql://portfolio-db:5432/portfolio",
    "SPRING_DATASOURCE_USERNAME=portfolio_user",
    "SPRING_DATASOURCE_PASSWORD=portfolio_pass",
    "SPRING_DATA_REDIS_HOST=portfolio-redis",
    "SPRING_DATA_REDIS_PORT=6379",
    "MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT=http://portfolio-tempo:4318/v1/traces",
  ]

  networks_advanced {
    name = docker_network.portfolio.name
  }

  log_driver = "json-file"
  log_opts = {
    tag = "{{.Name}}"
  }

  restart = "unless-stopped"
}

resource "docker_container" "prometheus" {
  depends_on = [docker_container.portfolio_app]

  name  = "portfolio-prometheus"
  image = docker_image.prometheus.name

  ports {
    internal = 9090
    external = 9090
  }

  volumes {
    host_path      = abspath("${path.module}/../../prometheus.yml")
    container_path = "/etc/prometheus/prometheus.yml"
  }

  networks_advanced {
    name = docker_network.portfolio.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "loki" {
  name  = "portfolio-loki"
  image = docker_image.loki.name

  ports {
    internal = 3100
    external = 3100
  }

  command = ["-config.file=/etc/loki/local-config.yaml"]

  networks_advanced {
    name = docker_network.portfolio.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "tempo" {
  name  = "portfolio-tempo"
  image = docker_image.tempo.name

  ports {
    internal = 3200
    external = 3200
  }
  ports {
    internal = 4318
    external = 4318
  }

  command = ["-config.file=/etc/tempo.yaml"]

  volumes {
    host_path      = abspath("${path.module}/../../tempo/tempo.yml")
    container_path = "/etc/tempo.yaml"
  }

  networks_advanced {
    name = docker_network.portfolio.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "promtail" {
  depends_on = [docker_container.loki]

  name  = "portfolio-promtail"
  image = docker_image.promtail.name

  user = "root"

  volumes {
    host_path      = "/var/lib/docker/containers"
    container_path = "/var/lib/docker/containers"
    read_only      = true
  }
  volumes {
    host_path      = "/var/run/docker.sock"
    container_path = "/var/run/docker.sock"
    read_only      = true
  }
  volumes {
    host_path      = abspath("${path.module}/../../promtail-config.yml")
    container_path = "/etc/promtail/config.yml"
  }

  command = ["-config.file=/etc/promtail/config.yml"]

  networks_advanced {
    name = docker_network.portfolio.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "grafana" {
  depends_on = [docker_container.prometheus, docker_container.loki, docker_container.tempo]

  name  = "portfolio-grafana"
  image = docker_image.grafana.name

  ports {
    internal = 3000
    external = 3000
  }

  volumes {
    volume_name    = docker_volume.grafana_storage.name
    container_path = "/var/lib/grafana"
  }
  volumes {
    host_path      = abspath("${path.module}/../../grafana/provisioning")
    container_path = "/etc/grafana/provisioning"
    read_only      = true
  }
  volumes {
    host_path      = abspath("${path.module}/../../grafana/dashboards")
    container_path = "/var/lib/grafana/dashboards"
    read_only      = true
  }

  networks_advanced {
    name = docker_network.portfolio.name
  }

  restart = "unless-stopped"
}
