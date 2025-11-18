package com.udeajobs.projects_cell.searching_service.config;

import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuración de OpenSearch para el servicio de búsqueda.
 * <p>
 * Configura la conexión a OpenSearch v2.19 sin autenticación.
 * OpenSearch es compatible con la API de Elasticsearch 7.x.
 * </p>
 *
 * @author UdeAJobs Team
 * @version 2.0
 * @since 2025-11-18
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.udeajobs.projects_cell.searching_service.repository")
public class OpeanSearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String opensearchUri;

    /**
     * Configura el cliente de bajo nivel de OpenSearch sin autenticación.
     * <p>
     * Esta configuración es adecuada para entornos de desarrollo donde
     * OpenSearch se ejecuta sin seguridad habilitada.
     * </p>
     *
     * @return RestClient configurado para OpenSearch
     */
    @Bean
    public RestClient restClient() {
        HttpHost host = HttpHost.create(opensearchUri);

        return RestClient.builder(host).build();
    }

    /**
     * Configura el cliente de OpenSearch.
     * <p>
     * OpenSearchClient es compatible con ElasticsearchClient en términos de API,
     * lo que permite una migración sencilla.
     * </p>
     *
     * @param restClient cliente de bajo nivel
     * @return OpenSearchClient configurado
     */
    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        RestClientTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        return new OpenSearchClient(transport);
    }
}

