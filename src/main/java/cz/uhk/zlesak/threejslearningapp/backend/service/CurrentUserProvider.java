package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.BackendException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Resolves the authenticated user for the embedded backend.
 * The UI authenticates through the OIDC login flow, so the principal is normally an {@link OidcUser};
 * a {@link Jwt} principal is also accepted so the same code works behind a bearer-token filter chain.
 */
@Component
public class CurrentUserProvider {

    /**
     * @return stable id of the current user, taken from the {@code sub} claim.
     * @throws BackendException.Forbidden when nobody is authenticated.
     */
    public String requireUserId() {
        String userId = currentUserId();
        if (userId == null) {
            throw new BackendException.Forbidden("Pro tuto akci je nutné být přihlášen.");
        }
        return userId;
    }

    /**
     * @return id of the current user, or {@code null} when the request is anonymous.
     */
    public String currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getSubject();
        }
        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        if ("anonymousUser".equals(principal)) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * @param role role name without the {@code ROLE_} prefix.
     * @return whether the current user holds the role.
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String authority = "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }
}
