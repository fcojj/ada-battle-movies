package br.com.ada.moviesbatle.match.control;

import br.com.ada.moviesbatle.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

     @Query("select match from Match match JOIN FETCH match.quizzes where match.isFinished=:status")
     List<Match> getEndsMatches(@Param("status") boolean status);

}
