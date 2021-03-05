resource "google_sql_database_instance" "master" {
  name = "master-instance"
  database_version = "POSTGRES_11"
  region = var.region

  depends_on = [
    google_service_networking_connection.private_vpc_connection]

  settings {
    # Second-generation instance tiers are based on the machine
    # type. See argument reference below.
    tier = "db-f1-micro"
    availability_type = "ZONAL"
    disk_size = 10
    disk_type = "PD_HDD"


    location_preference {
      zone = var.zone
    }

    ip_configuration {
      ipv4_enabled = false
      private_network = google_compute_network.vpc.id
    }

    backup_configuration {
      enabled = false
    }
  }
}

resource "google_sql_user" "users" {
  name = var.sql_user
  instance = google_sql_database_instance.master.name
  password = var.sql_password
}

resource "google_sql_database" "database" {
  name = var.sql_database
  instance = google_sql_database_instance.master.name
}