package br.com.ada.moviesbatle.ranking.boundary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RankingResponseDTO {

    private String username;
    private BigDecimal scores;
}
