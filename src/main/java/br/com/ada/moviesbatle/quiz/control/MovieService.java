package br.com.ada.moviesbatle.quiz.control;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class MovieService {
    private final String omdbApiUrl;
    private final RestTemplate restTemplate;

    public MovieService(@Value("${omdb.api.url}") String omdbApiUrl,
                        RestTemplate restTemplate) {
        this.omdbApiUrl = omdbApiUrl;
        this.restTemplate = restTemplate;
    }

    public List<MovieBO> getRandomMovies(){
        MovieBO movie1 = getMovie();
        MovieBO movie2 = getMovie();

        return List.of(movie1, movie2);
    }

    private MovieBO getMovie() {
        var movieImdbId = generateMovieImdbId();

        try {
            return restTemplate.getForEntity(omdbApiUrl, MovieBO.class, movieImdbId).getBody();
        } catch (Exception ex){
            var errorMessage = String.format("Error when tried to retrieve the %s movie from OMDb API. %s", movieImdbId, ex.getMessage());

            log.error(errorMessage);
            throw new OMDBRequestException(errorMessage);
        }
    }

    private String generateMovieImdbId() {
        //Generate a numeric string from 00 to 99, more chances to retrieve all attributes with relevant values, without N/A
        var numberWith2Digits = Integer.valueOf(RandomStringUtils.randomNumeric(2));

        //formats the String so that it has the format ttXXXXXXX where the X are digits.
        return "tt" + String.format("%07d", numberWith2Digits);
    }
}
