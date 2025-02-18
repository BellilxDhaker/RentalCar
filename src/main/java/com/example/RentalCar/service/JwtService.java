package com.example.RentalCar.service;

import com.example.RentalCar.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtService {
    private final String SECRET_KEY = "aadb720dad2b95b455d59a69c01660096f807dbdd79379a451b9a079023fe6e7";

    public  boolean isValid(String  token , UserDetails user){
        String  username =extractUserName(token);
        return  username.equals(user.getUsername()) && !isTokenExpired(token);
    }
    public boolean isTokenExpired(String token){
        return ExtractExpiration(token).before(new Date());
    }
    public Date ExtractExpiration(String token){
        return extractClaim(token ,Claims::getExpiration);
    }
    public String extractUserName(String token){
        return extractClaim(token ,Claims::getSubject);
    }


    public <T> T extractClaim(String token, Function<Claims,T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims)  ;  }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24*60*60*1000)) // 24 hour expiration
                .signWith(getSigninKey())
                .compact();
    }
    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
