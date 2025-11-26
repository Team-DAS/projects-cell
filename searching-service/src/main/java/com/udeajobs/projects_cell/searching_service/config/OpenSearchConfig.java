package com.udeajobs.projects_cell.searching_service.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenSearch para el servicio de búsqueda.
 * Compatible con OpenSearch v2.19.x y reemplazo directo de Elasticsearch.
 */
@Configuration
public class OpenSearchConfig {

    @Value("${opensearch.host}")
    private String host; // solo el hostname SIN credenciales

    @Value("${opensearch.user}")
    private String user;

    @Value("${opensearch.password}")
    private String password;

    @Bean
    public RestClient restClient() {

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(host, 443),
                new UsernamePasswordCredentials(user, password)
        );

        return RestClient.builder(new HttpHost(host, 443, "https"))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        return new OpenSearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }
}

