package org.eclipse.scout.springboot.demo.spring.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.scout.springboot.demo.model.Role;
import org.eclipse.scout.springboot.demo.model.User;
import org.eclipse.scout.springboot.demo.spring.repository.RoleRepository;
import org.eclipse.scout.springboot.demo.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @PostConstruct
  public void init() {
    Role roleUser = roleRepository.save(new Role(ROLE_ADMIN));
    Role roleAdmin = roleRepository.save(new Role(ROLE_USER));

    User alice = new User("alice", "Alice", "", "test");
    alice.getRoles().add(roleUser);
    alice.getRoles().add(roleAdmin);
    userRepository.save(alice);

    User bob = new User("bob", "Bob", "", "test");
    bob.getRoles().add(roleUser);
    userRepository.save(bob);

    User eclipse = new User("eclipse", "Eclipse", "", "scout");
    eclipse.getRoles().add(roleUser);
    userRepository.save(eclipse);
  }

  @Override
  public void addUser(User user) {
    if (user == null) {
      return;
    }

    // make sure user is not already in list
    if (userRepository.equals(user.getId())) {
      return;
    }

    userRepository.save(user);
  }

  @Override
  public User getUser(String userName) {
    return userRepository.findByName(userName);
  }

  @Override
  public List<User> getUsers() {
    return userRepository.findAll();
  }

  @Override
  public Role getRole(String roleName) {
    return roleRepository.findByName(roleName);
  }

}