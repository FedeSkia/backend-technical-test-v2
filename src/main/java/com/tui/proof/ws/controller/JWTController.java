package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.AuthenticationRequest;
import com.tui.proof.dto.response.AutenticationResponse;
import com.tui.proof.service.JwtToken;
import com.tui.proof.service.JwtUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JWTController {

    private final JwtToken jwtToken;
    private final JwtUserDetailsService userDetailsService;

    public JWTController(JwtToken jwtToken,
            JwtUserDetailsService userDetailsService) {
        this.jwtToken = jwtToken;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {


        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtToken.generateToken(userDetails);

        return ResponseEntity.ok(new AutenticationResponse(token));
    }

}