package br.com.ada.moviesbatle.ranking.control;

import br.com.ada.moviesbatle.match.control.MatchService;
import br.com.ada.moviesbatle.match.entity.Match;
import br.com.ada.moviesbatle.quiz.entity.Quiz;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RankingService {

    private final MatchService matchService;

    public RankingService(MatchService matchService) {
        this.matchService = matchService;
    }

    public List<RankingBO> getRanking() {
        var matches = matchService.getAllEndsMatches();
        List<RankingBO> rankingList = new ArrayList<>();

        for(Match match : matches){
            var amountQuizzes = getAmountQuizzes(match.getQuizzes());
            var amountHits = match.getAmountHits();

            double hitsPercentage = amountQuizzes == 0 ? 0.0 : ((double) amountHits / amountQuizzes * 100);
            double scores = amountQuizzes * hitsPercentage;

            BigDecimal bigDecimalScores = BigDecimal.valueOf(scores).setScale(2, RoundingMode.HALF_UP);
            rankingList.add(RankingBO.builder().username(match.getUsername()).scores(bigDecimalScores).build());
        }

        return rankingList;
    }

    private int getAmountQuizzes(Set<Quiz> quizzes) {
        if(CollectionUtils.isEmpty(quizzes)){
            return 0;
        }

        return quizzes.size();
    }
}
