package com.chefSearch.service.implementation;

import com.chefSearch.model.Chef;
import com.chefSearch.service.TvShowService;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TvShowServiceImpl implements TvShowService {
    @Override
    public void createTvShows(Chef chefModel, ResIterator tvShowIterator, List<String> tvShows, Property propertyStarring) {
        while (tvShowIterator.hasNext()) {
            String tvShowWithUnderScore = tvShowIterator.nextResource().getProperty(propertyStarring).getSubject().getLocalName();
            String tvShow = tvShowWithUnderScore.replace("_", " ");
            tvShow = tvShow.replaceFirst("s", "");
            tvShows.add(tvShow);
        }
        chefModel.setTvShows(tvShows);

    }
}