package kd.mgproj.domain.embedding;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kd.mgproj.domain.embedding.dto.response.EmbeddingSearchResponse;
import kd.mgproj.domain.embedding.service.EmbeddingService;
import lombok.RequiredArgsConstructor;

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
			embeddingService.searchPolicies(query, 5)
		);
	}
}
