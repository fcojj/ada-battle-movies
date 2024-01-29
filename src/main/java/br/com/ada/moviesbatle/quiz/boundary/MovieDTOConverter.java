package br.com.ada.moviesbatle.quiz.boundary;

import br.com.ada.moviesbatle.quiz.control.MovieBO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieDTOConverter
{
    public List<MovieDTO> convertFromMovieBOListToMovieDTOList(List<MovieBO> movieBOList){
        return movieBOList.stream().map(movieBO -> new MovieDTO(movieBO.getImdbID(),
                                                                movieBO.getTitle(),
                                                                movieBO.getPosterImageURL())).toList();

    }
}
