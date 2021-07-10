package com.chefSearch.service;

import com.chefSearch.model.Chef;

import java.io.IOException;

public interface ChefService {
    Chef getChef(String chef) throws IOException;
}