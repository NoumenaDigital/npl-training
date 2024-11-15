terraform {
  required_providers {
    keycloak = {
      source  = "mrparkers/keycloak"
      version = "4.1.0"
    }
  }
}

provider "keycloak" {
  client_id = "admin-cli"
  base_path = ""
}
