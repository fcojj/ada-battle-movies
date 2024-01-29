package br.com.ada.moviesbatle.match.control;

import br.com.ada.moviesbatle.match.boundary.TokenException;
import br.com.ada.moviesbatle.match.entity.Match;
import br.com.ada.moviesbatle.security.control.AccessTokenRepository;
import br.com.ada.moviesbatle.security.entity.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MatchService {

    private final MatchRepository matchRepository;
    private final AccessTokenRepository accessTokenRepository;

    public MatchService(MatchRepository matchRepository, AccessTokenRepository accessTokenRepository) {
        this.matchRepository = matchRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Transactional
    public void createMatch(String username, Long tokenId) {
        try {
            var token = accessTokenRepository.getReferenceById(tokenId);

            matchRepository.save(Match.builder().token(token).username(username).build());
        } catch (Exception ex){

            log.error("It was not possible to assign token id({}) for a match.", tokenId);
            throw new TokenException("It was not possible to assign token for a match.");
        }
    }

    @Transactional
    public void finishMatch(String token) {
        var fullDataToken = accessTokenRepository.findByToken(token);

        if(fullDataToken.isPresent()){
            var match = fullDataToken.get().getMatch();

            match.setFinished(true);
            match.setPendingQuiz(false);

            matchRepository.save(match);
        }
    }

    public Match getMatch(String token) {
        var fullDataToken = accessTokenRepository.findByToken(token);

        return fullDataToken.map(AccessToken::getMatch).orElseThrow(() -> new TokenException("Token invalid. Token is not associated to match id requested"));
    }

    public Match getMatch(Long matchId) {
        return matchRepository.getReferenceById(matchId);
    }

    @Transactional
    public void changePendingQuizStatus(Long matchId, boolean quizStatus) {
        var match = matchRepository.getReferenceById(matchId);

        match.setPendingQuiz(quizStatus);
        matchRepository.save(match);
    }

    public List<Match> getAllEndsMatches() {
        var matches = matchRepository.getEndsMatches(true);

        if(CollectionUtils.isNotEmpty(matches)){
            return matches;
        }

        return new ArrayList<>();
    }
}
