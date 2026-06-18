package com.talentconnect.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;
    public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration}") long expirationMs){
        this.key=Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs=expirationMs;
    }
    public String generateToken(String email){
        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+expirationMs)).signWith(key,SignatureAlgorithm.HS256).compact();
    }
    public String extractEmail(String token){return parseClaims(token).getSubject();}
    public boolean validateToken(String token){try{parseClaims(token);return true;}catch(JwtException|IllegalArgumentException e){return false;}}
    private Claims parseClaims(String token){return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();}
}
