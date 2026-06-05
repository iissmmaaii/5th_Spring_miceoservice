package com.cyphervault.api_gateway.common.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CypherVaultJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        String role = jwt.getClaimAsString("role");

        if (StringUtils.hasText(role)) {
            String normalizedRole = role.startsWith("ROLE_")
                    ? role
                    : "ROLE_" + role;

            authorities.add(new SimpleGrantedAuthority(normalizedRole));
        }

        List<String> permissions = jwt.getClaimAsStringList("permissions");

        if (permissions != null) {
            for (String permission : permissions) {
                if (StringUtils.hasText(permission)) {
                    authorities.add(
                            new SimpleGrantedAuthority("PERMISSION_" + permission)
                    );
                }
            }
        }

        return authorities;
    }
}