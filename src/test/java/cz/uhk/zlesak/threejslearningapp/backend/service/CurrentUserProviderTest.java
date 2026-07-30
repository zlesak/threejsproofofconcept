package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.BackendException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void takesTheUserIdFromTheLoggedInOidcUser() {
        OidcUser oidcUser = Mockito.mock(OidcUser.class);
        Mockito.when(oidcUser.getSubject()).thenReturn("alice-sub");
        authenticate(oidcUser, "ROLE_TEACHER");

        assertThat(currentUserProvider.currentUserId()).isEqualTo("alice-sub");
        assertThat(currentUserProvider.requireUserId()).isEqualTo("alice-sub");
    }

    @Test
    void alsoAcceptsABearerTokenPrincipal() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("bart-sub")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        authenticate(jwt, "ROLE_STUDENT");

        assertThat(currentUserProvider.currentUserId()).isEqualTo("bart-sub");
    }

    @Test
    void treatsAnAnonymousVisitorAsNobody() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        assertThat(currentUserProvider.currentUserId()).isNull();
        assertThatThrownBy(currentUserProvider::requireUserId)
                .isInstanceOf(BackendException.Forbidden.class);
    }

    @Test
    void treatsAnEmptyContextAsNobody() {
        assertThat(currentUserProvider.currentUserId()).isNull();
        assertThat(currentUserProvider.hasRole("TEACHER")).isFalse();
    }

    @Test
    void recognisesTheRolesTheUserHolds() {
        OidcUser oidcUser = Mockito.mock(OidcUser.class);
        Mockito.when(oidcUser.getSubject()).thenReturn("alice-sub");
        authenticate(oidcUser, "ROLE_TEACHER", "ROLE_CREATE_QUIZ");

        assertThat(currentUserProvider.hasRole("TEACHER")).isTrue();
        assertThat(currentUserProvider.hasRole("CREATE_QUIZ")).isTrue();
        assertThat(currentUserProvider.hasRole("ADMIN")).isFalse();
    }

    private void authenticate(Object principal, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                principal, "n/a", List.of(authorities).stream().map(SimpleGrantedAuthority::new).toList()));
    }
}
