package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //In case the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries savedWebSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(savedWebSeries != null) throw new Exception("Series is already present");

        WebSeries newWebSeries = new WebSeries();
        newWebSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        newWebSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        newWebSeries.setRating(webSeriesEntryDto.getRating());
        newWebSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        newWebSeries.setProductionHouse(productionHouse);
        productionHouse.getWebSeriesList().add(newWebSeries);

        //updating the rating of production house
        double ave = (productionHouse.getRatings() + webSeriesEntryDto.getRating())/2 ;
        productionHouse.getWebSeriesList().add(newWebSeries);
        productionHouse.setRatings(ave);

        newWebSeries.setProductionHouse(productionHouse);
        productionHouseRepository.save(productionHouse);
        savedWebSeries = webSeriesRepository.save(newWebSeries);


        return savedWebSeries.getId();
    }

}
