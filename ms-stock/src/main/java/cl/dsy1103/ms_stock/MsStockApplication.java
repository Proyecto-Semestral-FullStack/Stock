package cl.dsy1103.ms_stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Clase principal de la aplicación.
 *
 * @SpringBootApplication: Habilita Spring Boot (escanea @Component, @Service, @Repository, etc)
 * @EnableDiscoveryClient: Registra este microservicio en Eureka
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MsStockApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsStockApplication.class, args);
	}
}