package cl.dsy1103.ms_stock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * CONFIGURACIÓN de WebClient para llamar a otros microservicios.
 *
 * @Configuration: Spring reconoce esta clase como configuración
 * @Slf4j: Lombok genera un logger para logging
 *
 * En esta clase:
 * - Configuramos WebClient
 * - Integramos con Eureka (descubrimiento de servicios)
 * - Definimos timeouts
 */
@Configuration
@Slf4j
public class WebClientConfig {

    /**
     * Crea un bean de WebClient con Eureka integrado.
     *
     * @LoadBalanced: permite usar nombres de servicio (ms-catalogo)
     *                en lugar de URLs directas
     *
     * Sin @LoadBalanced:
     *   webClient.get().uri("http://localhost:8082/catalogo/...").retrieve()
     *
     * Con @LoadBalanced:
     *   webClient.get().uri("http://ms-catalogo/catalogo/...").retrieve()
     *   Eureka automáticamente resuelve "ms-catalogo" → "localhost:8082"
     *
     * @return WebClient bean
     */
    @Bean
    @LoadBalanced
    public WebClient webClient() {
        log.info("Inicializando WebClient con Eureka LoadBalancer");

        return WebClient.builder()
                // No especificamos baseUrl porque cada servicio tiene su URL
                // Los services especificarán la URL completa
                .build();
    }

    /**
     * ALTERNATIVA: WebClient SIN LoadBalancer (si NO usas Eureka).
     *
     * Descomenta esto y comenta el anterior si decides NO usar Eureka.
     *
     * Útil para desarrollo local simple.
     */
    /*
    @Bean
    public WebClient webClientLocal() {
        log.info("Inicializando WebClient SIN LoadBalancer (desarrollo local)");

        return WebClient.builder()
            .baseUrl("http://localhost:8082")  // URL local de ms-catalogo
            .build();
    }
    */
}
