variable "admin_username" {
  type        = string
  description = "Portfolio admin username"
  sensitive   = false
}

variable "admin_password" {
  type        = string
  description = "Portfolio admin password"
  sensitive   = true
}

variable "docker_host" {
  type        = string
  description = "Docker daemon socket to connect to"
  default     = "unix:///var/run/docker.sock"
}

variable "image_tag" {
  type        = string
  description = "Tag for the locally built portfolio-app image"
  default     = "portfolio-app:latest"
}
