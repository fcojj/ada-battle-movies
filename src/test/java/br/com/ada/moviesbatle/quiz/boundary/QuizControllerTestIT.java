package br.com.ada.moviesbatle.quiz.boundary;

import br.com.ada.moviesbatle.match.boundary.dto.StartMatchRequestDTO;
import br.com.ada.moviesbatle.match.boundary.dto.StartMatchResponseDTO;
import br.com.ada.moviesbatle.match.control.MatchRepository;
import br.com.ada.moviesbatle.quiz.control.MovieBO;
import br.com.ada.moviesbatle.quiz.control.MovieService;
import br.com.ada.moviesbatle.quiz.control.QuizRepository;
import br.com.ada.moviesbatle.security.control.AccessTokenRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuizControllerTestIT {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @MockBean
    private MovieService movieService;

    private HttpEntity<MultiValueMap<String, Object>> requestWithToken;

    private MovieBO movie1;

    private MovieBO movie2;

    private String token;

    @BeforeEach
    void setup() throws IOException {
        StartMatchRequestDTO startMatchRequestDTO = new StartMatchRequestDTO("player1","qwe123");
        ResponseEntity<StartMatchResponseDTO> response = template.postForEntity("/match/start", startMatchRequestDTO, StartMatchResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        token = Objects.requireNonNull(response.getBody()).getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ token);
        requestWithToken = new HttpEntity<>(headers);

        movie1  = buildMovie("/movie1_omdb_response.json");
        movie2  = buildMovie("/movie2_omdb_response.json");
    }

    @AfterEach
    void finish(){
        ResponseEntity<String> response = template.postForEntity("/match/finish", requestWithToken, String.class);
        var result = response.getBody();
        assertEquals("Logout completed successfully.", result);
        cleanDatabase();
    }

    @Test
    void whenGetQuiz_GivenTokenValidAndWithoutQuizzesPendingByReply_ThenReturnQuiz() {
        when(movieService.getRandomMovies()).thenReturn(List.of(movie1,movie2));

        ResponseEntity<QuizDTO> result = template.exchange("/quiz", HttpMethod.GET, requestWithToken, QuizDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(buildQuizDTO());
    }

    @Test
    void whenReplyQuiz_GivenTokenValidAndWithQuizzesPendingByReplyAndCorrectAnswer_ThenReturnAnswerWithHitTrue() {
        when(movieService.getRandomMovies()).thenReturn(List.of(movie1,movie2));
        var quizRequestResult = template.exchange("/quiz", HttpMethod.GET, requestWithToken, QuizDTO.class);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        var requestWithTokenAndBody = new HttpEntity<>(QuizAnswerRequestDTO.builder().quizId(quizRequestResult.getBody().getQuizId())
                                                                                     .matchId(quizRequestResult.getBody().getMatchId())
                                                                                     .selectedMovieId("tt0000012")
                                                                                     .build(), headers);

        ResponseEntity<QuizAnswerDTO> result = template.exchange("/quiz", HttpMethod.POST, requestWithTokenAndBody, QuizAnswerDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(buildQuizAnswerDTO(quizRequestResult.getBody().getMatchId(),
                                                                                             quizRequestResult.getBody().getQuizId()));
    }

    private QuizAnswerDTO buildQuizAnswerDTO(Long matchId, Long quizId) {
        return QuizAnswerDTO.builder().hit(true)
                                      .amountErrors(0)
                                      .isMatchOver(false)
                                      .quizId(matchId)
                                      .matchId(quizId)
                                      .build();
    }

    private QuizDTO buildQuizDTO() {
        return QuizDTO.builder().quizId(1L)
                .matchId(1L)
                .movie1(MovieDTO.builder().id("tt0000009")
                        .title("Miss Jerry")
                        .posterImageURL("https://m.media-amazon.com/images/M/MV5BNTUwNzM4NWYtMDkwOC00ZmQwLTk1OGQtNjAwMmU2MjI3Y2QxXkEyXkFqcGdeQXVyNzQ1NDIxMDQ@._V1_SX300.jpg")
                        .build())
                .movie2(MovieDTO.builder().id("tt0000012")
                        .title("The Arrival of a Train")
                        .posterImageURL("https://m.media-amazon.com/images/M/MV5BZjE2MGVkMTAtMWIwYy00YzQ5LWE2YTAtMTU2NGJmNGNjY2IyXkEyXkFqcGdeQXVyNjMxMzM3NDI@._V1_SX300.jpg")
                        .build())
                .build();
    }

    private MovieBO buildMovie(String jsonResponseFilePath) throws IOException {
        InputStream inJson = MovieBO.class.getResourceAsStream(jsonResponseFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(inJson, MovieBO.class);
    }


    private void cleanDatabase() {
        quizRepository.deleteAll();
        matchRepository.deleteAll();
        accessTokenRepository.deleteAll();
    }
}