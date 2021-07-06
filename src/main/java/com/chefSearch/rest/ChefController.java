package com.chefSearch.rest;

import com.chefSearch.model.Chef;
import com.chefSearch.service.ChefService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@CrossOrigin(origins = "http://localhost:8000")
@RequestMapping("/api/chef")
public class ChefController {

   public final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }


    @GetMapping("/{name}")
    public ResponseEntity<Chef> getChef(@PathVariable String name){
        Chef chef = chefService.getChef(name);
        return  ResponseEntity.ok(chef);
    }

}
