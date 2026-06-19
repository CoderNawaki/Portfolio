output "portfolio_app_url" {
  description = "Portfolio application URL"
  value       = "http://localhost:8081"
}

output "prometheus_url" {
  description = "Prometheus dashboard URL"
  value       = "http://localhost:9090"
}

output "grafana_url" {
  description = "Grafana dashboard URL"
  value       = "http://localhost:3000"
}

output "loki_url" {
  description = "Loki HTTP API URL"
  value       = "http://localhost:3100"
}

output "tempo_url" {
  description = "Tempo HTTP API URL"
  value       = "http://localhost:3200"
}
