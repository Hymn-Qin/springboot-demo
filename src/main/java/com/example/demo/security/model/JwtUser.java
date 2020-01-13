package com.example.demo.security.model;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class JwtUser implements UserDetails, Serializable {

    private static final long serialVersionUID = -547778910429597132L;
    private Integer id;
    private String username;
    private String displayName;
    private String name;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private Collection<? extends  GrantedAuthority> authorities;

    public JwtUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.displayName = user.getDisplayName();
        this.name = user.getName();
        this.authorities = getRoleList(user.getRoles());
    }

    public static List<SimpleGrantedAuthority> getRoleList(Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName())));
        return authorities;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthorities(Set<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //是否能登录，可以加上角色逻辑
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
