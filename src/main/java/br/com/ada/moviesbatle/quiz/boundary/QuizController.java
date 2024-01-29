package br.com.ada.moviesbatle.quiz.boundary;


import br.com.ada.moviesbatle.match.boundary.TokenException;
import br.com.ada.moviesbatle.quiz.control.QuizService;
import br.com.ada.moviesbatle.security.service.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
@Slf4j
public class QuizController {
    private final QuizService quizService;
    private final QuizDTOConverter quizDTOConverter;
    private final QuizValidator quizValidator;
    private final JwtTokenService tokenService;

    public QuizController(QuizService quizService,
                          QuizDTOConverter quizDTOConverter,
                          QuizValidator quizValidator, JwtTokenService tokenService) {
        this.quizService = quizService;
        this.quizDTOConverter = quizDTOConverter;
        this.quizValidator = quizValidator;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Get a quiz with two random movies")
    @ApiResponse(responseCode = "200", description = "List with data about two random movies.",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QuizDTO.class)))})
    @ApiResponse(responseCode = "401", description = "Invalid access token.", content = {@Content(mediaType = "application/json")})
    @ApiResponse(responseCode = "400", description = "Invalid request. Tried to take a quiz but there is a quiz pending answer", content = {@Content(mediaType = "application/json")})
    @GetMapping
    @CrossOrigin
    public QuizDTO getQuiz(HttpServletRequest request) {
        var token = extractTokenFromRequest(request);
        var matchId = quizValidator.validateMatchHasPendingQuiz(token);

        return quizDTOConverter.convertQuizBoToQuizDto(quizService.createNewQuiz(matchId));
    }

    @Operation(summary = "Reply the quiz and return if the user sent the right quiz answer and the amount of errors from user")
    @ApiResponse(responseCode = "200", description = "Quiz answer was received and processed with success.",
            content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MovieDTO.class)))})
    @ApiResponse(responseCode = "401", description = "Invalid access token.", content = {@Content(mediaType = "application/json")})
    @ApiResponse(responseCode = "400", description = "Invalid request. There are inconsistencies in your request.", content = {@Content(mediaType = "application/json")})
    @PostMapping
    @CrossOrigin
    public QuizAnswerDTO answerQuiz(@Valid @RequestBody QuizAnswerRequestDTO quizAnswerRequestDTO, HttpServletRequest request) {
        var token = extractTokenFromRequest(request);

        quizValidator.validateQuizAnswerInputs(quizAnswerRequestDTO.getMatchId(),
                quizAnswerRequestDTO.getQuizId(),
                quizAnswerRequestDTO.getSelectedMovieId(),
                token);

        var quizAnswerBO = quizService.processQuizAnswer(quizAnswerRequestDTO.getQuizId(),
                                                         quizAnswerRequestDTO.getSelectedMovieId(),
                                                         token);


        return QuizAnswerDTO.builder().quizId(quizAnswerRequestDTO.getQuizId())
                                      .matchId(quizAnswerRequestDTO.getMatchId())
                                      .amountErrors(quizAnswerBO.getAmountErrors())
                                      .hit(quizAnswerBO.isHit())
                                      .isMatchOver(quizAnswerBO.isMatchOver())
                                      .build();
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        var token = tokenService.extractJwtFromRequest(request);

        if(token == null){
            throw new TokenException("Invalid token. It wasn't possible to extract the token from request.");
        }
        return token;
    }
}