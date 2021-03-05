variable "project_id" {
  description = "project id"
}

variable "region" {
  description = "region"
}

variable "gke_username" {
  default = ""
  description = "gke username"
}

variable "gke_password" {
  default = ""
  description = "gke password"
}

variable "gke_num_nodes" {
  default = 1
  description = "number of gke nodes"
}

variable "zone" {
  description = "single zone"
}

variable "sql_user" {
  description = "database user"
}

variable "sql_password" {
  description = "database password"
}

variable "sql_database" {
  description = "default database"
}