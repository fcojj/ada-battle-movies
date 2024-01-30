package br.com.ada.moviesbatle.ranking.boundary;

import br.com.ada.moviesbatle.match.boundary.dto.StartMatchRequestDTO;
import br.com.ada.moviesbatle.match.boundary.dto.StartMatchResponseDTO;
import br.com.ada.moviesbatle.match.control.MatchRepository;
import br.com.ada.moviesbatle.match.entity.Match;
import br.com.ada.moviesbatle.quiz.control.QuizRepository;
import br.com.ada.moviesbatle.quiz.entity.Quiz;
import br.com.ada.moviesbatle.security.control.AccessTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RankingControllerIT {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    private HttpEntity<MultiValueMap<String, String>> requestWithToken;
    private String token;

    @BeforeEach
    void setup(){
        StartMatchRequestDTO startMatchRequestDTO = new StartMatchRequestDTO("player1","qwe123");
        ResponseEntity<StartMatchResponseDTO> response = template.postForEntity("/match/start", startMatchRequestDTO, StartMatchResponseDTO.class);
        token = Objects.requireNonNull(response.getBody()).getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ token);
        requestWithToken = new HttpEntity<>(headers);
    }

    @AfterEach
    void finish(){
        ResponseEntity<String> response = template.postForEntity("/match/finish", requestWithToken, String.class);
        var result = response.getBody();
        assertEquals("Logout completed successfully.", result);
        cleanDatabase();
    }


    @Test
    void whenGetRanking_GivenTheMatchWithoutQuizzesDone_ThenReturnRankingEmpty() {

        ResponseEntity<RankingResponseDTO[]> result = template.exchange("/ranking", HttpMethod.GET, requestWithToken, RankingResponseDTO[].class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, Objects.requireNonNull(result.getBody()).length);
    }

    @Test
    void whenGetRanking_GivenTheMatchWithQuizzesDone_ThenReturnRanking() {
        var matchSaved = matchRepository.save(Match.builder().token(null).username("player1").amountError(0).amountHits(1).isFinished(true).build());
        quizRepository.save(Quiz.builder().isActive(false).match(matchSaved).movieWinId("a").movieId1("a").movieId2("b").build());

        ResponseEntity<RankingResponseDTO[]> result = template.exchange("/ranking", HttpMethod.GET, requestWithToken, RankingResponseDTO[].class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("player1", Objects.requireNonNull(result.getBody())[0].getUsername());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP), Objects.requireNonNull(result.getBody())[0].getScores());
    }

    private void cleanDatabase() {
        quizRepository.deleteAll();
        matchRepository.deleteAll();
        accessTokenRepository.deleteAll();
    }
}