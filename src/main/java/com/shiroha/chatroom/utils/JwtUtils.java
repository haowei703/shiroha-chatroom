package com.shiroha.chatroom.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.shiroha.chatroom.types.TokenPair;
import org.springframework.lang.NonNull;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * jwt工具类
 */
public class JwtUtils {

    /**
     * 密钥
     */
    private final static String SECRET = "3F4428472B4B6250655368566D5971337336763979244226452948404D635166";

    /**
     * jwt主题
     */
    private final static String SUBJECT = "Peripherals";
    /**
     * jwt签发者
     */
    private final static String JWT_ISS = "Rem";

    /**
     * token过期时间
     */
    private final static Long TOKEN_EXPIRATION_TIME = 15 * 60 * 1000L; // 15分钟

    /**
     * refreshToken过期时间
     */
    private final static Long TOKEN_REFRESH_TIME = 30 * 60 * 1000L; // 30分钟

    /**
     * 签发JWT
     * @param username token负载：用户名
     * @return TokenPair
     * @throws JOSEException token解析失败
     */
    public static TokenPair generateTokenPair(@NonNull String username) throws JOSEException {
        String accessToken = generateToken(username, true);
        String refreshToken = generateToken(username, false);
        return TokenPair.builder()
                .accessToken(accessToken)
                .expiresIn(Instant.now().plusMillis(TOKEN_EXPIRATION_TIME))
                .refreshToken(refreshToken)
                .refreshExpiresIn(Instant.now().plusMillis(TOKEN_REFRESH_TIME))
                .build();
    }

    /**
     *
     * @param username token负载：用户名
     * @param isAccessToken true:AccessToken;false:RefreshToken
     * @return JWT令牌对
     * @throws JOSEException 密钥长度不够
     */
    private static String generateToken(@NonNull String username, @NonNull boolean isAccessToken) throws JOSEException {
        // 令牌id
        String uuid = UUID.randomUUID().toString();

        Date exp = isAccessToken ?
                new Date(new Date().getTime() + TOKEN_EXPIRATION_TIME) :
                new Date(new Date().getTime() + TOKEN_REFRESH_TIME);
        // 创建JWT Claims
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issuer(JWT_ISS)
                .claim("name", username)
                .claim("isAccessToken", String.valueOf(isAccessToken))
                .expirationTime(exp)
                .jwtID(uuid)
                .build();

        // 创建一个新的JWS对象
        SignedJWT signedJWTA = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet);

        // 创建HMAC签名器
        JWSSigner signer = new MACSigner(SECRET);

        // 签名
        signedJWTA.sign(signer);

        return signedJWTA.serialize();
    }

    /**
     * 验证JWT
     * @param token token字符串
     * @return 检验是否通过
     * @throws JOSEException 验证器创建失败或是签名验证失败时抛出
     * @throws ParseException token解析失败时抛出
     */
    public static boolean verifyToken(String token) throws JOSEException, ParseException {
        // 解析JWT
        SignedJWT signedJWT = SignedJWT.parse(token);

        // 创建HMAC验证器
        JWSVerifier verifier = new MACVerifier(SECRET);

        // 验证签名
        return signedJWT.verify(verifier);
    }

    /**
     * 解析JWT
     * @param token token字符串
     * @return ClaimsSet集合
     * @throws ParseException token解析失败时抛出
     */
    public static JWTClaimsSet parseToken(String token) throws ParseException {
        // 解析JWT
        SignedJWT signedJWT = SignedJWT.parse(token);

        // 获取JWT Claims
        return signedJWT.getJWTClaimsSet();
    }

    /**
     * 获取指定声明
     * @param token token字符串
     * @param claim 需要获取的claim
     * @return 获取结果
     * @throws ParseException token解析失败时抛出
     */
    public static String getClaim(String token, String claim) throws ParseException {
        JWTClaimsSet claimsSet = parseToken(token);
        return claimsSet.getStringClaim(claim);
    }

    // 检查JWT是否过期
    public static boolean isTokenExpired(String token) throws ParseException {
        JWTClaimsSet claimsSet = parseToken(token);
        Date expirationTime = claimsSet.getExpirationTime();
        return expirationTime.before(new Date());
    }

    // 刷新Token
    public static TokenPair refreshToken(String refreshToken) throws JOSEException, ParseException {
        if(verifyToken(refreshToken) && !isTokenExpired(refreshToken) && getClaim(refreshToken, "isAccessToken").equals("false")) {
            String username = getClaim(refreshToken, "name");

            return generateTokenPair(username);
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
