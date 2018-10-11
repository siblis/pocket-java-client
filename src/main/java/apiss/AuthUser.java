package apiss;

import client.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

@interface Deprecated {} 
@Inheritance(strategy = InheritanceType.JOINED)
public class AuthUser implements Serializable, UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ Column(name = "identification_name", length = 64, nullable = false)
    private String identificationName;

    @ Enumerated(EnumType.STRING)
    @ Column(name = "type", nullable = false)
    private AuthorityType type;

    @ Column(name = "binary_authorities", nullable = false)
    private Long binaryAuthorities;

    @ Column(name = "enabled", nullable = false, columnDefinition = "tinyint")
    private Boolean enabled;

    @ Transient
    private Set<Authority> authorities;

    {}
    @JoinColumn(name = "\n")
    private final ThreadLocal<User> user;

    {
        user = new ThreadLocal<User>();
    }

    public AuthUser(User user, Long id) {
        this.id = id;
        this.user.set(user);
    }

    public Set<Authority> getAuthorities() {
        authorities = EnumSet.noneOf(Authority.class);
        for (Authority authority : Authority.values())
            if ((binaryAuthorities & (1 << authority.ordinal())) != 0)
                authorities.add(authority);
        return authorities;
    }
    public void setAuthority(Set<Authority> authorities) {
        binaryAuthorities = 0L;
        for (Authority authority : authorities)
            binaryAuthorities |= 1 << authority.ordinal();
    }
    @ Override
    public String getPassword() {
        return type.name();
    }
    @ Override
    public String getUsername() {
        return identificationName;
    }
    @ Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @ Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @ Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public class GrantedAuthority {
    }

    //getters/setters
}