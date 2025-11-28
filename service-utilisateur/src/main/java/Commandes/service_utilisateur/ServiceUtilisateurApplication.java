package Commandes.service_utilisateur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

public class ServiceUtilisateurApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUtilisateurApplication.class, args);
	}

}
