//package com.documed.backend.users.services;
//
//import com.documed.backend.services.ServiceService;
//import com.documed.backend.users.SubscriptionToServiceDAO;
//import com.documed.backend.users.model.Subscription;
//import com.documed.backend.users.model.SubscriptionToService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class SubscriptionToServiceService {
//
//    private final SubscriptionToServiceDAO subscriptionToServiceDAO;
//    private final SubscriptionService subscriptionService;
//    private final ServiceService serviceService;
//
//    public List<SubscriptionToService> getAllSubscriptionToServiceForSubscription(int subscriptionId) {
//        return subscriptionToServiceDAO.getForSubscription(subscriptionId);
//    }
//
//    public int getDiscountForService(int serviceId, int subscriptionId) {
//        return subscriptionToServiceDAO.getDiscountForService(serviceId, subscriptionId);
//    }
//
//    public void createSubscriptionToService(SubscriptionToService subscriptionToService) {
//        subscriptionToServiceDAO.create(subscriptionToService);
//    }
//
//    public void updateSubscriptionToService(SubscriptionToService subscriptionToService) {
//        subscriptionToServiceDAO.update(subscriptionToService);
//    }
//
//    public void createSubscriptionToServiceForNewService(int serviceId){
//        List<Subscription> subscriptions = subscriptionService.getAll();
//        subscriptions.forEach(subscription ->
//                createSubscriptionToService(new SubscriptionToService(serviceId, subscription.getId(), 0)));
//    }
//
//    void createSubscriptionToServiceForNewSubscription(int subscriptionId) {
//        List<com.documed.backend.services.model.Service> services = serviceService.getAll();
//
//        services.forEach(service ->
//                createSubscriptionToService(new SubscriptionToService(service.getId(), subscriptionId, 0)));
//
//    }
//
//}
