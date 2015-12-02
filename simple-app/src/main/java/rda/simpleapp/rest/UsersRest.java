package rda.simpleapp.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rda.simpleapp.UserDetailsServiceHz;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UsersRest {

    @Autowired
    UserDetailsServiceHz userDetailsService;

    @RequestMapping("/")
    public String home() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user.getUsername();
        return name;
    }

    @RequestMapping("/usernames")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Set<String> all() {
        return userDetailsService.getAllNames();
    }
}
