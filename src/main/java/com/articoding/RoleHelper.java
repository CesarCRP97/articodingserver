package com.articoding;

import com.articoding.model.Role;
import com.articoding.model.User;
import com.articoding.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleHelper {
    @Autowired
    RoleRepository roleRepository;

    public List<Role> getRole() {
        List<Role> roles = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<SimpleGrantedAuthority> privileges = (List<SimpleGrantedAuthority>) authentication.getAuthorities();
        List<String> actualUserRoles = privileges.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        if (actualUserRoles.contains("ROLE_ADMIN")) {
            roles.add(this.roleRepository.findByName("ROLE_ADMIN"));
            roles.add(this.roleRepository.findByName("ROLE_TEACHER"));
            roles.add(this.roleRepository.findByName("ROLE_USER"));
        }
        if (actualUserRoles.contains("ROLE_TEACHER")) {
            roles.add(this.roleRepository.findByName("ROLE_TEACHER"));
            roles.add(this.roleRepository.findByName("ROLE_USER"));
        }
        if (actualUserRoles.contains("ROLE_USER")){
            roles.add(this.roleRepository.findByName("ROLE_USER"));
        }
        return roles;
    }

    public boolean isAdmin(User user) {
        return user.getRole().getName().equals("ROLE_ADMIN");
    }
    public boolean can(Role roles, String necessaryRoot) {

        if (necessaryRoot.equals("ROLE_ADMIN")) {
            return roles.getName().equals("ROLE_ADMIN");
        }
        if (necessaryRoot.equals("ROLE_TEACHER")) {
            return roles.getName().equals("ROLE_ADMIN") ||
                    roles.getName().equals("ROLE_TEACHER");
        }
        if (necessaryRoot.equals("ROLE_USER")) {
            return roles.getName().equals("ROLE_ADMIN") ||
                    roles.getName().equals("ROLE_TEACHER") ||
                    roles.getName().equals("ROLE_USER") ;
        }
        return false;
    }

    public boolean isTeacher(User actualUser) {
        return actualUser.getRole().getName().equals("ROLE_TEACHER");
    }

    public boolean isUser(User actualUser) {
        return actualUser.getRole().getName().equals("ROLE_USER");
    }

    public Role getAdmin() {
        return roleRepository.findByName("ROLE_ADMIN");
    }
    public Role getTeacher() {
        return roleRepository.findByName("ROLE_TEACHER");
    }

    public Role getUser() {
        return roleRepository.findByName("ROLE_USER");
    }
}
