package com.urbaneats.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Integer codigoUsuario;
    private final String nombres;

    public CustomUserDetails(Integer codigoUsuario, String nombres, String correo,
                            String password, Collection<? extends GrantedAuthority> authorities) {
        super(correo, password, authorities);
        this.codigoUsuario = codigoUsuario;
        this.nombres = nombres;
    }
}