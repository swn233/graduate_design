package com.example.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
//读取配置文件中的属性
//@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
//Jwt秘钥
    String key;
    @Value("${spring.security.jwt.expire}")
    int   expire;
    @Resource
    StringRedisTemplate template;
    //利用redis建立黑名单机制,用户退出后无法再使用这个token，在token过期后删除
    public boolean invalidateJwt(String headerToken){
        String token=this.convertToken(headerToken);
        if(token==null){
            return false;
        }
        Algorithm algorithm=Algorithm.HMAC256(key);
        //通过指定的算法构造一个jwt解析器
        JWTVerifier jwtVerifier=JWT.require(algorithm).build();
        try{
            DecodedJWT jwt=jwtVerifier.verify(token);
            //这里获取的是jwtid，使用uuid进行唯一标识，而不是负载中的id字段
            String id =jwt.getId();
            //将对应id的jwt拉黑
            return deleteToken(id,jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    private boolean deleteToken(String uuid,Date time){
        if (isInvalidToken(uuid)){
            return false;
        }
        Date now=new Date();;
        long expire=Math.max(time.getTime()-now.getTime(),0);
        template.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MILLISECONDS);
        return true; }

    private boolean isInvalidToken(String uuid  ){
        return  Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST+uuid));
    }

    //由于USERDetails中只有username和password，所以id和昵称等信息需要额外传入
    public  String createJwt(UserDetails details, int id, String username){
        //加密
        Algorithm algorithm=Algorithm.HMAC256(key);

        return JWT.create()
                .withJWTId(java.util.UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authority",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(GetExpireTime())
                .withIssuedAt(new Date())
                .sign(algorithm);
    }
    //计算过期时间
    public Date GetExpireTime(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);
        return calendar.getTime();
    }

    private String convertToken(String headerToken){
        if(headerToken==null||!headerToken.startsWith("Bearer ")){
            return null;
        }
        return headerToken.substring(7);
    }

    public DecodedJWT resolveJwt(String headerToken) {
        String token=this.convertToken(headerToken);
        if (token==null){
            return null;
        }
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify=jwtVerifier.verify(token);
            if (this.isInvalidToken(verify.getId()))return null;//判断是否在黑名单中
            Date expiresAt=verify.getExpiresAt();
            return new Date().after(expiresAt) ? null:verify;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return null;
        }
    }


    //将解码后的jwt转换成userdetails对象，从而用于构建验证
    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims=jwt.getClaims();
        return User.withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authority").asArray(String.class))
                .build();
    }

    public Object toId(DecodedJWT jwt) {
        Map<String, Claim> claims=jwt.getClaims();
        return claims.get("id").asInt();
    }
}
