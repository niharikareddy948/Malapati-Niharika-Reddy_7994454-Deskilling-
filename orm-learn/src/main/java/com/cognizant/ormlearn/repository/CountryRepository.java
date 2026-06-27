package com.cognizant.ormlearn.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cognizant.ormlearn.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    
    // Query method to find list of countries matching a partial country name
    List<Country> findByNameContaining(String name);
    
    // Optional: Query method to find list of countries matching a partial country name ordered alphabetically
    List<Country> findByNameContainingOrderByName(String name);
}
