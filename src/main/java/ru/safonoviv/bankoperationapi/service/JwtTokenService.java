package ru.safonoviv.bankoperationapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.safonoviv.bankoperationapi.entity.User;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Duration expiration;

    @Autowired
    @Lazy
    private UserService userService;

    public String generateToken(UserDetails userDetails) {
        User user = userService.getUserByLogin(userDetails.getUsername());
        if (user!=null) {
                Map<String, List<String>> claims = new HashMap<>();
                List<String> rolesList = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                claims.put("roles", rolesList);
                claims.put("user_login", Collections.singletonList(String.valueOf(user.getLogin())));

                Date issuedDate = new Date();
                Date expiredDate = new Date(issuedDate.getTime() + expiration.toMillis());
                return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(issuedDate)
                        .setExpiration(expiredDate)
                        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                        .compact();
            }
        return "error";

    }

    @SneakyThrows
    public String getUserLogin(String token)  {
        return (String) getAllClaimsFromToken(token).get("user_login", List.class).stream().toList().stream().findFirst().orElseThrow(()-> new RuntimeException("Wrong token"));
    }


    public Collection<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", Collection.class);
    }

    private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
