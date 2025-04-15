package com.documed.backend.users;

import com.documed.backend.users.model.User;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserDAO userDAO;

  public UserService(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public Optional<User> getById(int id) {
    return userDAO.getById(id);
  }

  public boolean deactivateUser(int id) {
    int rowsAffected = userDAO.delete(id);
    return rowsAffected > 0;
  }
}
