package com.pws.admin.security.oauth2.config;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pws.admin.utility.JwtUtil;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        String token = null;
        String userName = null;
        
//        if (httpServletRequest.getServletPath().equals("/authenticate") || httpServletRequest.getServletPath().equals("/authenticate/refresh")) {
//		 filterChain.doFilter(httpServletRequest, httpServletResponse);
//        }else

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            userName = jwtUtil.extractUsername(token);
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userName);

            if (jwtUtil.validateToken(token, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
//@Slf4j
//public class CustomAuthorizationFilter extends OncePerRequestFilter {
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//
//		// if user is using this end point we don't do anything
//		if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
//			filterChain.doFilter(request, response);
//		} else {
//			String authorizationHeader = request.getHeader(AUTHORIZATION);
//			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//
//				try {
//
//					String token = authorizationHeader.substring("Bearer ".length());
//					Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//					JWTVerifier verifier = JWT.require(algorithm).build();
//					DecodedJWT decodedJWT = verifier.verify(token);
//					String username = decodedJWT.getSubject();
//					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
//					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//					stream(roles).forEach(role ->{
//						authorities.add(new SimpleGrantedAuthority(role));
//					});
//					UsernamePasswordAuthenticationToken authenticationToken = 
//							new UsernamePasswordAuthenticationToken(username, null, authorities);
//					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//					filterChain.doFilter(request, response);
//					
//				} catch (Exception e) {
//					
//					log.error("Error logging in: {}", e.getMessage());
//					response.setHeader("error", e.getMessage());
//					response.setStatus(FORBIDDEN.value());
//					//response.sendError(FORBIDDEN.value());
//					Map<String, String> error = new HashMap<>();
//					error.put("error_message", e.getMessage());
//					response.setContentType(MediaType.APPLICATION_JSON_VALUE);	//To set content type
//					new ObjectMapper().writeValue(response.getOutputStream(), error);
//
//				} 
//			}else {
//				filterChain.doFilter(request, response);
//			}
//		}
//
//	}
//
//}