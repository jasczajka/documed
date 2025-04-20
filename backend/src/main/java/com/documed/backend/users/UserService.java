package com.documed.backend.users;

import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import java.util.List;
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

  public Optional<User> addSpecializationsToUser(int userId, List<Integer> specializationIds) {
    Optional<User> optionalUser = getById(userId);
    if (optionalUser.isEmpty()) return Optional.empty();

    User user = optionalUser.get();
    if (user.getRole() != UserRole.DOCTOR) {
      throw new IllegalArgumentException("Only users with role DOCTOR can have specializations.");
    }

    return userDAO.addSpecializationsToUser(userId, specializationIds);
  }
}
