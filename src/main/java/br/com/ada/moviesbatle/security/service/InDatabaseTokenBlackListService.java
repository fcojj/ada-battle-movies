package br.com.ada.moviesbatle.security.service;

import br.com.ada.moviesbatle.match.boundary.TokenException;
import br.com.ada.moviesbatle.security.control.AccessTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InDatabaseTokenBlackListService implements TokenBlackListService {

    private final AccessTokenRepository accessTokenRepository;

    public InDatabaseTokenBlackListService(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    @Transactional
    public void addToBlacklist(String token) {
        if(token == null){
            throw new TokenException("Invalid token. Token is empty.");
        }

        var tokenOptionalFullData = accessTokenRepository.findByToken(token);

        if(tokenOptionalFullData.isPresent()){
            var tokenFullData = tokenOptionalFullData.get();

            tokenFullData.setBlackList(true);
            accessTokenRepository.save(tokenFullData);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        var tokenSearched = accessTokenRepository.findByTokenAndIsBlackList(token, true);

        return tokenSearched.isPresent();
    }
}
