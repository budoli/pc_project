package kd.mgproj.domain.embedding;

import kd.mgproj.domain.embedding.dto.response.EmbeddingSearchResponse;
import kd.mgproj.domain.embedding.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/embedding")
public class EmbedingController {
    private final EmbeddingService embeddingService;

    @GetMapping("/ai-search")
    public ResponseEntity<List<EmbeddingSearchResponse>> search(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(
                embeddingService.searchPolicies(query,5)
        );
    }
}
