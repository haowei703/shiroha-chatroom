package com.shiroha.chatroom.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nimbusds.jose.JOSEException;
import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.dto.UserDTO;
import com.shiroha.chatroom.helper.RedisTokenManager;
import com.shiroha.chatroom.mapper.UserMapper;
import com.shiroha.chatroom.service.UserService;
import com.shiroha.chatroom.types.LoginStatus;
import com.shiroha.chatroom.types.TokenPair;
import com.shiroha.chatroom.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserDetailsService, UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final RedisTokenManager tokenManager;

    private final UserService self;  // AOP代理对象

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           RedisTokenManager tokenManager,
                           @Lazy PasswordEncoder passwordEncoder,
                           @Lazy AuthenticationManager authenticationManager,
                           @Lazy UserService self) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.self = self;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, username);
        try {
            return userMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserDTO login(String username, String password) throws RuntimeException {
        try {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDO user = (UserDO) authentication.getPrincipal();

            user.setLastLoginTime(LocalDateTime.now().withNano(0));
            user.setStatus(LoginStatus.ONLINE);
            userMapper.updateById(user);
            TokenPair token = JwtUtils.generateTokenPair(username);
            tokenManager.saveToken(user.getId().toString(), token);
            return UserDTO.builder()
                    .id(user.getId())
                    .username(username)
                    .tokenPair(token)
                    .build();
        } catch (JOSEException e) {
            log.error("jwt parse error", e);
            throw new RuntimeException("jwt parse error");
        } catch (MyBatisSystemException | PersistenceException | BadSqlGrammarException e) {
            log.error("database update error", e);
            throw new RuntimeException("mybatis system parse error");
        }catch (BadCredentialsException e) {
            log.error("password is wrong", e);
            throw new RuntimeException("password is wrong");
        } catch (Exception e) {
            throw new UsernameNotFoundException(username);
        }
    }


    @Override
    @Transactional
    public UserDTO register(UserDTO userDTO) {
        try {
            LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserDO::getUsername, userDTO.getUsername());
            if (userMapper.selectList(wrapper).isEmpty()) {
                UserDO user = new UserDO()
                        .setUsername(userDTO.getUsername())
                        .setPassword(passwordEncoder.encode(userDTO.getPassword()))
                        .setCreatedAt(LocalDate.now());
                userMapper.insert(user);
                UUID uid = self.getUserByUsername(user.getUsername()).getId();
                return UserDTO.builder()
                        .id(uid)
                        .username(user.getUsername())
                        .build();
            } else {
                throw new IllegalArgumentException("User already exists");
            }
        } catch (Exception e) {
            log.error("Error occurred during user registration: ", e);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public UserDO getUserByUserId(String userId) {
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getId, userId);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void logout(UUID userId) {
        UserDO user = userMapper.selectById(userId);
        user.setStatus(LoginStatus.OFFLINE);
        userMapper.updateById(user);
    }

    @Override
    @Cacheable(value = "userInfos", key = "#username")
    public UserDTO getUserInfo(String username) {
        UserDO user = self.getUserByUsername(username);
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .build();
    }
}
