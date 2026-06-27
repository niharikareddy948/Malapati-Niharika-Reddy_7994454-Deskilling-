package com.cognizant.ormlearn;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.cognizant.ormlearn.model.Country;
import com.cognizant.ormlearn.service.CountryService;
import com.cognizant.ormlearn.service.exception.CountryNotFoundException;

@SpringBootApplication
public class OrmLearnApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrmLearnApplication.class);
    private static CountryService countryService;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OrmLearnApplication.class, args);
        countryService = context.getBean(CountryService.class);
        LOGGER.info("Inside main");

        // Run all tests sequentially
        testGetAllCountries();
        testFindCountryByCode();
        testFindCountryByCodeNotFound();
        testAddCountry();
        testUpdateCountry();
        testDeleteCountry();
        testFindCountriesByNameContaining();
    }

    // Hands-on 1 & 5: Retrieve all countries
    private static void testGetAllCountries() {
        LOGGER.info("---- Start: testGetAllCountries ----");
        List<Country> countries = countryService.getAllCountries();
        LOGGER.debug("countries={}", countries);
        LOGGER.info("---- End: testGetAllCountries (Count: {}) ----", countries.size());
    }

    // Hands-on 6: Find a country based on country code
    private static void testFindCountryByCode() {
        LOGGER.info("---- Start: testFindCountryByCode (IN) ----");
        try {
            Country country = countryService.findCountryByCode("IN");
            LOGGER.debug("Country details: {}", country);
        } catch (CountryNotFoundException e) {
            LOGGER.error("Country not found!", e);
        }
        LOGGER.info("---- End: testFindCountryByCode ----");
    }

    // Hands-on 6: Attempt to find a country that does not exist to verify exception handling
    private static void testFindCountryByCodeNotFound() {
        LOGGER.info("---- Start: testFindCountryByCodeNotFound (XX) ----");
        try {
            Country country = countryService.findCountryByCode("XX");
            LOGGER.debug("Country details: {}", country);
        } catch (CountryNotFoundException e) {
            LOGGER.info("Expected Exception caught successfully: {}", e.getMessage());
        }
        LOGGER.info("---- End: testFindCountryByCodeNotFound ----");
    }

    // Hands-on 7: Add a new country
    private static void testAddCountry() {
        LOGGER.info("---- Start: testAddCountry ----");
        Country newCountry = new Country("ZZ", "TestCountry");
        countryService.addCountry(newCountry);
        LOGGER.info("New country ZZ added.");

        try {
            Country fetched = countryService.findCountryByCode("ZZ");
            LOGGER.debug("Verified new country in DB: {}", fetched);
        } catch (CountryNotFoundException e) {
            LOGGER.error("Verification failed! Country was not added.", e);
        }
        LOGGER.info("---- End: testAddCountry ----");
    }

    // Hands-on 8: Update a country based on code
    private static void testUpdateCountry() {
        LOGGER.info("---- Start: testUpdateCountry ----");
        try {
            LOGGER.info("Updating ZZ country name to 'UpdatedTestCountry'...");
            countryService.updateCountry("ZZ", "UpdatedTestCountry");
            Country updated = countryService.findCountryByCode("ZZ");
            LOGGER.debug("Verified updated country in DB: {}", updated);
        } catch (CountryNotFoundException e) {
            LOGGER.error("Country update failed!", e);
        }
        LOGGER.info("---- End: testUpdateCountry ----");
    }

    // Hands-on 9: Delete a country based on code
    private static void testDeleteCountry() {
        LOGGER.info("---- Start: testDeleteCountry ----");
        LOGGER.info("Deleting country ZZ...");
        countryService.deleteCountry("ZZ");
        
        try {
            Country fetched = countryService.findCountryByCode("ZZ");
            LOGGER.error("Delete verification failed! Country ZZ still exists: {}", fetched);
        } catch (CountryNotFoundException e) {
            LOGGER.info("Verified deletion: Country ZZ no longer exists (Exception caught: {})", e.getMessage());
        }
        LOGGER.info("---- End: testDeleteCountry ----");
    }

    // Hands-on 5 (extra service requirement): Find list of countries matching a partial country name
    private static void testFindCountriesByNameContaining() {
        LOGGER.info("---- Start: testFindCountriesByNameContaining ('ia') ----");
        List<Country> countries = countryService.findCountriesByNameContaining("ia");
        LOGGER.debug("Matching countries (containing 'ia'):");
        for (Country country : countries) {
            LOGGER.debug(" - {}", country);
        }
        LOGGER.info("---- End: testFindCountriesByNameContaining (Count: {}) ----", countries.size());
    }
}
