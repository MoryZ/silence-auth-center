package com.old.silence.auth.center.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import com.old.silence.auth.center.domain.model.Role;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SilenceHallContextHolder {

    public static Optional<String> getAuthenticatedUserName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        } else {
            var principal = authentication.getPrincipal();
            return principal instanceof SilencePrincipal
                    ? Optional.of(((SilencePrincipal) principal).getUsername())
                    : Optional.of(principal.toString());
        }
    }

    public static Optional<BigInteger> getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        } else {
            var principal = authentication.getPrincipal();
            return principal instanceof SilencePrincipal
                    ? Optional.of(((SilencePrincipal) principal).getUserId())
                    : Optional.of(new BigInteger(principal.toString()));
        }
    }

    public static Optional<SilenceHallUser> getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            var principal = authentication.getPrincipal();
            if (principal instanceof SilencePrincipal) {
                var userId = ((SilencePrincipal) principal).getUserId();
                var um = ((SilencePrincipal) principal).getUsername();
                var cnName = ((SilencePrincipal) principal).getCnName();
                return Optional.of(new SilenceHallUser(userId, um, cnName));
            }
        }
        return Optional.empty();
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities();
    }

    public static boolean isRoot() {
        var authorities = getAuthorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        } else {
            return authorities.stream().anyMatch(
                    authority -> authority.getAuthority().equals(SecurityConstants.ROOT_ROLE_CODE)
            );
        }
    }

    public static boolean isMaster() {
        var authorities = getAuthorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        } else {
            return authorities.stream().anyMatch(
                    authority -> authority.getAuthority().equals(SecurityConstants.MASTER_ROLE_CODE)
            );
        }
    }

    public static Set<Role> getCurrentUserRoles() {
        var authorities = getAuthorities();
        return authorities.stream().map(authority -> {
            var silenceHallGrantedAuthority = (SilenceHallGrantedAuthority) authority;
            var silenceHallRole = new Role();
            silenceHallRole.setCode(silenceHallGrantedAuthority.getAuthority());
            silenceHallRole.setName(silenceHallGrantedAuthority.getRoleName());
            return silenceHallRole;
        }).collect(Collectors.toSet());
    }

}
