package juno.http.auth;

import juno.http.Authorization;

public class TokenAuthorization implements Authorization {
    
    private final TokenProvider tokenManager;
    private String headerPrefix;

    public TokenAuthorization(String headerPrefix, TokenProvider tokenManager) {
        this.tokenManager = tokenManager;
        this.headerPrefix = headerPrefix;
    }
    
    public TokenAuthorization(TokenProvider tokenManager) {
        this("Bearer ", tokenManager);
    }

    @Override
    public String generateAuthHeader() throws Exception {
        final Token token = tokenManager.retrieveOrRefreshToken();
        return headerPrefix + token.getToken();
    }

    public String getHeaderPrefix() {
        return headerPrefix;
    }

    public TokenAuthorization setHeaderPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
        return this;
    }
}
