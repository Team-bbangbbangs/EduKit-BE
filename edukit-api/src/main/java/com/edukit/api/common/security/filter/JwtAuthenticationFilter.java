package com.edukit.api.common.security.filter;

import com.edukit.api.common.security.authentication.MemberAuthentication;
import com.edukit.api.common.security.authentication.MemberDetailReader;
import com.edukit.api.common.security.config.SecurityWhitelist;
import com.edukit.core.auth.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final MemberDetailReader memberDetailReader;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String[] WHITELIST = SecurityWhitelist.getAllWhitelistPaths();

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }
        return Arrays.stream(WHITELIST)
                .anyMatch(whitelist -> pathMatcher.match(whitelist, path));
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        String memberUuid = getMemberUuidFromToken(request);
        doAuthentication(request, memberUuid);
        filterChain.doFilter(request, response);
    }

    private String getMemberUuidFromToken(final HttpServletRequest request) {
        String requestedToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        return jwtTokenService.parseMemberUuidFromAccessToken(requestedToken);
    }

    private void doAuthentication(final HttpServletRequest request, final String memberUuid) {
        UserDetails userDetails = memberDetailReader.loadUserByUsername(memberUuid);
        long memberId = Long.parseLong(userDetails.getUsername());
        MemberAuthentication authentication = MemberAuthentication.create(memberId, userDetails.getAuthorities());

        createAndSetWebAuthenticationDetails(request, authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void createAndSetWebAuthenticationDetails(final HttpServletRequest request,
                                                      final MemberAuthentication authentication) {
        WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();
        WebAuthenticationDetails webAuthenticationDetails = webAuthenticationDetailsSource.buildDetails(request);
        authentication.setDetails(webAuthenticationDetails);
    }
}
