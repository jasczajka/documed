package com.documed.backend.users.services;

import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.users.UserDAO;
import com.documed.backend.users.exceptions.SpecializationToNonDoctorException;
import com.documed.backend.users.model.AccountStatus;
import com.documed.backend.users.model.Specialization;
import com.documed.backend.users.model.User;
import com.documed.backend.users.model.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserDAO userDAO;

  public UserService(UserDAO userDAO) {
    this.userDAO = userDAO;
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
    boolean isUserDoctor = isUserAssignedToRole(userId, UserRole.DOCTOR);
    if (!isUserDoctor) {
      throw new SpecializationToNonDoctorException(
          "Only users with role DOCTOR can have specializations.");
    }

    return userDAO.addSpecializationsToUser(userId, specializationIds);
  }

  public User updateUserSpecializations(int userId, List<Integer> updatedSpecializationIds) {

    boolean isUserDoctor = isUserAssignedToRole(userId, UserRole.DOCTOR);
    if (!isUserDoctor) {
      throw new SpecializationToNonDoctorException(
          "Only users with role DOCTOR can have specializations.");
    }

    return userDAO.updateUserSpecializations(userId, updatedSpecializationIds);
  }

  public List<Specialization> getUserSpecializationsById(int userId) {
    Optional<User> optionalUser = getById(userId);
    if (optionalUser.isEmpty()) {
      throw new UserNotFoundException("User not found.");
    }

    return userDAO.getUserSpecializationsById(userId);
  }

  public void toggleEmailNotificationsById(int userId) {
    userDAO.toggleEmailNotificationsById(userId);
  }

  public boolean areNotificationsOn(int userId) {
    Optional<User> user = userDAO.getById(userId);
    if (user.isEmpty()) {
      throw new UserNotFoundException("User not found");
    }
    return user.get().isEmailNotifications();
  }

  private boolean isUserAssignedToRole(int userId, UserRole role) {
    Optional<User> optionalUser = getById(userId);
    if (optionalUser.isEmpty()) {
      throw new UserNotFoundException("User not found.");
    }

    User user = optionalUser.get();
    return user.getRole() == role;
  }

  public void updateUserSubscription(int userId, int subscriptionId) {
    userDAO.updateUserSubscription(userId, subscriptionId);
  }

}
