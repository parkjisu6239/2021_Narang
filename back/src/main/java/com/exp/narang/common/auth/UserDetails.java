package com.exp.narang.common.auth;

import com.exp.narang.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 현재 액세스 토큰으로 부터 인증된 유저의 부가 상세정보(활성화 여부, 만료, 롤 등) 정의.
 */
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
	@Autowired
	User user;
	boolean accountNonExpired;
    boolean accountNonLocked;
    boolean credentialNonExpired;
    boolean enabled = false;
    List<GrantedAuthority> roles = new ArrayList<>();
    
    public UserDetails(User user) {
    		super();
    		this.user = user;
    }
    
    public User getUser() {
    		return this.user;
    }
	@Override
	public String getPassword() {
		return this.user.getPassword();
	}
	@Override
	public String getUsername() {
		return this.user.getEmail();
	}
	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}
	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialNonExpired;
	}
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles;
	}
	public void setAuthorities(List<GrantedAuthority> roles) {
		this.roles = roles;
	}
}
