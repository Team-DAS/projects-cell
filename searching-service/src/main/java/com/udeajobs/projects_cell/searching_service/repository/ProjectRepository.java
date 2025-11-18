package com.udeajobs.projects_cell.searching_service.repository;

import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.*;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OpenSearchProjectRepository {

    private static final String INDEX = "projects";

    private final OpenSearchClient client;

    public void save(ProjectDocument document) throws IOException {
        IndexResponse response = client.index(i -> i
                .index(INDEX)
                .id(document.getId())
                .document(document)
        );

        log.debug("Index response: {}", response.result());
    }

    public Optional<ProjectDocument> findByProjectId(String projectId) throws IOException {
        GetResponse<ProjectDocument> response = client.get(g -> g
                        .index(INDEX)
                        .id(projectId),
                ProjectDocument.class
        );

        if (!response.found()) return Optional.empty();
        return Optional.of(response.source());
    }

    public void deleteByProjectId(String projectId) throws IOException {
        DeleteResponse response = client.delete(d -> d
                .index(INDEX)
                .id(projectId)
        );

        log.debug("Delete response: {}", response.result());
    }
}
