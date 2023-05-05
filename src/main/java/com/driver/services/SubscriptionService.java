package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);

        String subtype = String.valueOf(subscriptionEntryDto.getSubscriptionType());
        int amount = 0;
        if(subtype.equals("PRO")){
            amount = 800 + (250 * subscriptionEntryDto.getNoOfScreensRequired());

        }else if(subtype.equals("BASIC")){
            amount = 500 * (200 * subscriptionEntryDto.getNoOfScreensRequired());

        }else if(subtype.equals("ELITE")){
            amount = 1000 + (350 * subscriptionEntryDto.getNoOfScreensRequired());
        }

        subscription.setTotalAmountPaid(amount);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        user.setSubscription(savedSubscription);
        userRepository.save(user);

        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();

        int balanceToBePaid = 0;
        Subscription subscription = user.getSubscription();
        if(String.valueOf(subscription.getSubscriptionType()).equals("ELITE")) throw new Exception("Already the best Subscription");

        if(String.valueOf(subscription.getSubscriptionType()).equals("BASIC")){

            int amountForPro = 800 + (250 * subscription.getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(amountForPro);

            balanceToBePaid = amountForPro - subscription.getTotalAmountPaid();

        }else if(String.valueOf(subscription.getSubscriptionType()).equals("PRO")){

            int amountForElite = 1000 + (350 * subscription.getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(amountForElite);

            balanceToBePaid = amountForElite - subscription.getTotalAmountPaid();

        }

        user.setSubscription(subscription);

        subscriptionRepository.save(subscription);

        return balanceToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int totalRev = 0;
        for(Subscription subscription: subscriptionList){

            totalRev += subscription.getTotalAmountPaid();

        }
        return totalRev;
    }

}
