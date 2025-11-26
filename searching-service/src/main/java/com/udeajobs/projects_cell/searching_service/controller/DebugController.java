package com.udeajobs.projects_cell.searching_service.controller;

import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DebugController {

    private final OpenSearchClient client;

    @GetMapping("/debug/opensearch")
    public String testConnection() {
        try {
            var health = client.cluster().health();
            return "OK - cluster: " + health.clusterName();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    @GetMapping("/debug/index")
    public String index() throws Exception {
        var doc = new ProjectDocument();
        doc.setId("demo-1");
        doc.setProjectId("11111111-1111-1111-1111-111111111111");
        doc.setEmployerId("22222222-2222-2222-2222-222222222222");
        doc.setTitle("Backend Developer Java");
        doc.setDescription("Spring Boot, microservicios, AWS.");
        doc.setStatus("PUBLISHED");

        client.index(i -> i
                .index("projects")
                .id(doc.getId())
                .document(doc)
        );

        return "Indexed OK";
    }
}