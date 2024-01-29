package br.com.ada.moviesbatle.security.service;

public interface TokenBlackListService {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
