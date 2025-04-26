package com.documed.backend.users;

import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.model.AccountStatus;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserDAO userDAO;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
    this.userDAO = userDAO;
    this.passwordEncoder = passwordEncoder;
  }

  public Optional<User> getById(int id) {
    return userDAO.getById(id);
  }

  public Optional<User> getByEmail(String email) {
    return userDAO.getByEmail(email);
  }

  public Optional<User> getByPesel(String pesel) {
    return userDAO.getByPesel(pesel);
  }

  @Transactional
  public User createPendingUser(User user) {
    user.setAccountStatus(AccountStatus.PENDING_CONFIRMATION);
    return userDAO.createAndReturn(user);
  }

  @Transactional
  public User activateUser(String email) {
    User user = getByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

    user.setAccountStatus(AccountStatus.ACTIVE);
    return userDAO.update(user);
  }

  public boolean deactivateUser(int id) {
    int rowsAffected = userDAO.delete(id);
    return rowsAffected > 0;
  }

  public User addSpecializationsToUser(int userId, List<Integer> specializationIds) {
    Optional<User> optionalUser = getById(userId);
    if (optionalUser.isEmpty()) {
      throw new UserNotFoundException("User not found.");
    }

    User user = optionalUser.get();
    if (user.getRole() != UserRole.DOCTOR) {
      throw new IllegalArgumentException("Only users with role DOCTOR can have specializations.");
    }

    return userDAO.addSpecializationsToUser(userId, specializationIds);
  }

  public void toggleEmailNotificationsById(int userId) {
    userDAO.toggleEmailNotificationsById(userId);
  }
}
