package br.com.ada.moviesbatle.quiz.control;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class MovieServiceTest {

    @Value("${omdb.api.url}")
    private String omdbApiUrl;

    @Captor
    private ArgumentCaptor<String> generatedMovieImdbIdCaptor;

    @Mock
    private RestTemplate restTemplate;

    private MovieService movieService;

    private MovieBO movie1;
    private MovieBO movie2;


    @BeforeEach
    void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        movie1  = buildMovie("/movie1_omdb_response.json");
        movie2  = buildMovie("/movie2_omdb_response.json");
        movieService = new MovieService(omdbApiUrl,restTemplate);
    }

    @Test
    void whenGeRandomMovies_ThenReturnTwoMoviesFromOmdbAPI() {
        when(restTemplate.getForEntity(eq(omdbApiUrl), eq(MovieBO.class), generatedMovieImdbIdCaptor.capture()))
                .thenReturn(new ResponseEntity<>(movie1,HttpStatus.OK))
                .thenReturn(new ResponseEntity<>(movie2,HttpStatus.OK));

        var result = movieService.getRandomMovies();

        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(List.of(movie1,movie2));
    }

    @Test
    void whenGeRandomMovies_GivenExceptionWhenRequestOmdbApiThenThrowOMDBRequestException() {
        when(restTemplate.getForEntity(eq(omdbApiUrl), eq(MovieBO.class), generatedMovieImdbIdCaptor.capture()))
                .thenReturn(new ResponseEntity<>(movie1,HttpStatus.OK))
                .thenThrow(HttpClientErrorException.class);

        assertThrows(OMDBRequestException.class, () -> movieService.getRandomMovies());
    }

    private MovieBO buildMovie(String jsonResponseFilePath) throws IOException {
        InputStream inJson = MovieBO.class.getResourceAsStream(jsonResponseFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(inJson, MovieBO.class);
    }

}