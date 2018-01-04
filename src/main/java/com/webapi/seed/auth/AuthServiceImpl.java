package com.webapi.seed.auth;

import com.webapi.seed.config.JwtConfig;
import com.webapi.seed.domain.JwtUser;
import com.webapi.seed.entity.Account;
import com.webapi.seed.entity.AccountRole;
import com.webapi.seed.service.impl.AccountRoleService;
import com.webapi.seed.service.impl.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private JwtConfig jwtConfig;

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtTokenUtil jwtTokenUtil;
    private AccountService accountService;
    private AccountRoleService accountRoleService;

    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtTokenUtil jwtTokenUtil,
            AccountService accountService,
            AccountRoleService accountRoleService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.accountService = accountService;
        this.accountRoleService = accountRoleService;
    }

    @Override
    public boolean register(Account account) {
        if (accountService.selectByUsername(account.username) != null) {
            return false;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        account.password = encoder.encode(account.password);
        account.lastPasswordResetDate = new Date();
        boolean accountResult = accountService.insert(account);

        AccountRole accountRole = new AccountRole(account.accountId, "ROLE_USER");
        boolean accountRoleResult = accountRoleService.insert(accountRole);

        return accountResult && accountRoleResult;
    }

    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication auth = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public String refresh(String oldToken) {
        final String token = oldToken.substring(jwtConfig.tokenHead.length());
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }

}
