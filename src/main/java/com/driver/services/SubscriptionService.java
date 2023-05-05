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
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        String s = subscriptionEntryDto.getSubscriptionType().toString();
        int amt = 0;
        if(s.equalsIgnoreCase("basic") || s.equalsIgnoreCase("pro") || s.equalsIgnoreCase("elite")){
            if(s.equalsIgnoreCase("basic")){
                subscription.setSubscriptionType(SubscriptionType.BASIC);
                amt = 500 + (200*subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setTotalAmountPaid(amt);
            }
            else if(s.equalsIgnoreCase("pro")){
                subscription.setSubscriptionType(SubscriptionType.PRO);
                amt = 800 + (250*subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setTotalAmountPaid(amt);
            }
            else if(s.equalsIgnoreCase("elite")){
                subscription.setSubscriptionType(SubscriptionType.ELITE);
                amt = 1000 + (350*subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setTotalAmountPaid(amt);
            }
        }

        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);

        user.setSubscription(subscription);
        userRepository.save(user);

        return amt;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        int amtDiffer = 0;

        if(subscription.getSubscriptionType()==SubscriptionType.ELITE)
            throw new Exception("Already the best Subscription");

        else if (subscription.getSubscriptionType()==SubscriptionType.PRO) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            amtDiffer = (1000 + (350*subscription.getNoOfScreensSubscribed()))-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(1000 + (350*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }
        else if (subscription.getSubscriptionType()==SubscriptionType.BASIC) {
            subscription.setSubscriptionType(SubscriptionType.PRO);
            amtDiffer = (800 + (250*subscription.getNoOfScreensSubscribed()))-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(800 + (250*subscription.getNoOfScreensSubscribed()));
            subscription.setStartSubscriptionDate(new Date());
        }

        subscriptionRepository.save(subscription);
        return amtDiffer;

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
