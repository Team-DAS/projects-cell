package com.udeajobs.projects_cell.searching_service.repository;

import com.udeajobs.projects_cell.searching_service.dto.request.ProjectSearchInput;
import com.udeajobs.projects_cell.searching_service.entity.ProjectDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectSearchRepository {

    private final OpenSearchClient client;
    private static final String INDEX = "projects";

    public SearchResponse<ProjectDocument> search(ProjectSearchInput input) throws IOException {
        SearchRequest request = buildRequest(input);
        return client.search(request, ProjectDocument.class);
    }

    private SearchRequest buildRequest(ProjectSearchInput input) {

        BoolQuery bool = BoolQuery.of(b -> {

            // 🔍 Buscar en description y title (lo único que soporta tu mapping)
            if (input.getSearchTerm() != null && !input.getSearchTerm().isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm
                        .query(input.getSearchTerm())
                        .fields("title^3", "description^2")
                        .fuzziness("AUTO")
                ));
            }

            // Filter por status
            if (input.getStatus() != null) {
                b.filter(f -> f.term(t -> t
                        .field("status.keyword")
                        .value(v -> v.stringValue(input.getStatus()))
                ));
            }

            return b;
        });

        return new SearchRequest.Builder()
                .index(INDEX)
                .query(q -> q.bool(bool))
                .from(input.getPage() * input.getSize())
                .size(input.getSize())
                .sort(s -> s.field(f -> f
                        .field("title.keyword") // porque no tienes indexedAt
                        .order("DESC".equalsIgnoreCase(input.getSortDirection())
                                ? SortOrder.Desc
                                : SortOrder.Asc)
                ))
                .build();
    }
}
