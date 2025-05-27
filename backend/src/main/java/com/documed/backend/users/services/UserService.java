package com.documed.backend.users.services;

import com.documed.backend.attachments.S3Service;
import com.documed.backend.attachments.model.Attachment;
import com.documed.backend.auth.exceptions.UserNotFoundException;
import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.users.UserDAO;
import com.documed.backend.users.exceptions.SpecializationToNonDoctorException;
import com.documed.backend.users.model.*;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserService {

  private final UserDAO userDAO;
  private final S3Service s3Service;

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
    User user = getByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

    user.setAccountStatus(AccountStatus.ACTIVE);
    return userDAO.update(user);
  }

  @Transactional
  public boolean deactivateUser(int id) {
    List<Attachment> userAttachments = this.s3Service.getAttachmentsForPatient(id);
    userAttachments.forEach(attachment -> this.s3Service.deleteFile(attachment.getId()));
    int rowsAffected = userDAO.deletePatientPersonalData(id);
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
      throw new NotFoundException("User not found.");
    }

    return userDAO.getUserSpecializationsById(userId);
  }

  public void toggleEmailNotificationsById(int userId) {
    userDAO.toggleEmailNotificationsById(userId);
  }

  public boolean areNotificationsOn(int userId) {
    Optional<User> user = userDAO.getById(userId);
    if (user.isEmpty()) {
      throw new NotFoundException("User not found");
    }
    return user.get().isEmailNotifications();
  }

  public List<User> getAllByRole(UserRole role) {
    return userDAO.getAllByRole(role);
  }

  private boolean isUserAssignedToRole(int userId, UserRole role) {
    User user = getById(userId).orElseThrow(() -> new NotFoundException("User not found"));

    return user.getRole() == role;
  }

  public void updateUserSubscription(int userId, int subscriptionId) {
    userDAO.updateUserSubscription(userId, subscriptionId);
  }

  public int getSubscriptionIdForPatient(int userId) {
    User patient =
        userDAO.getById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

    return patient.getSubscriptionId();
  }
}
