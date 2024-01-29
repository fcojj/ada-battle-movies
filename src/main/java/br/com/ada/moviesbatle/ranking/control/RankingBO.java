package br.com.ada.moviesbatle.ranking.control;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RankingBO {
    private String username;
    private BigDecimal scores;
}
