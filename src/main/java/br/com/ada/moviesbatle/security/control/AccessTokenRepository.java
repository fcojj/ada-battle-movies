package br.com.ada.moviesbatle.security.control;

import br.com.ada.moviesbatle.security.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByToken(String token);
    Optional<AccessToken> findByTokenAndIsBlackList(String token, boolean isBlackList);
}
