package com.documed.backend.users.services;

import com.documed.backend.exceptions.NotFoundException;
import com.documed.backend.users.SubscriptionDAO;
import com.documed.backend.users.model.Subscription;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SubscriptionService {

  private final SubscriptionDAO subscriptionDAO;

  public Subscription getById(int id) {
    return subscriptionDAO
        .getById(id)
        .orElseThrow(() -> new NotFoundException("Subscription not found"));
  }

  public List<Subscription> getAll() {
    return subscriptionDAO.getAll();
  }
}
