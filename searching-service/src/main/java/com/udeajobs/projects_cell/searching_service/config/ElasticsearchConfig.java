package com.udeajobs.projects_cell.searching_service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuración de Elasticsearch para el servicio de búsqueda.
 * <p>
 * Configura la conexión a Elasticsearch con credenciales desde variables de entorno.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 1.0
 * @since 2025-11-04
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.udeajobs.projects_cell.searching_service.repository")
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    /**
     * Configura el cliente de bajo nivel de Elasticsearch sin autenticación.
     * <p>
     * Esta configuración es adecuada para entornos de desarrollo donde
     * Elasticsearch se ejecuta sin seguridad habilitada.
     * </p>
     *
     * @return RestClient configurado
     */
    @Bean
    public RestClient restClient() {
        HttpHost host = HttpHost.create(elasticsearchUri);

        return RestClient.builder(host).build();
    }

    /**
     * Configura el cliente de Elasticsearch.
     *
     * @param restClient cliente de bajo nivel
     * @return ElasticsearchClient configurado
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        RestClientTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }
}

