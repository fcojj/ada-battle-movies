package br.com.ada.moviesbatle.ranking.boundary;

import br.com.ada.moviesbatle.quiz.boundary.MovieDTO;
import br.com.ada.moviesbatle.ranking.control.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/ranking")
@Slf4j
public class RankingController {
    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Operation(summary = "Get player score ranking")
    @ApiResponse(responseCode = "200", description = "Get player score list sorted by scores",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovieDTO.class)))})
    @ApiResponse(responseCode = "401", description = "Invalid access token.", content = {@Content(mediaType = "application/json")})
    @GetMapping
    @CrossOrigin
    public List<RankingResponseDTO> getRanking() {
        var rankingBOList = rankingService.getRanking();

        return rankingBOList.stream().map(rankingBO -> new RankingResponseDTO(rankingBO.getUsername(), rankingBO.getScores()))
                                    .sorted(Comparator.comparing(RankingResponseDTO::getScores).reversed()).toList();


    }
}

