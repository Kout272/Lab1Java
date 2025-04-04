package com.example.mylab.controller;

import com.example.mylab.model.Country;
import com.example.mylab.service.CountryService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mylab.cache.CountryCache;

import java.util.List;
import java.util.Optional;

@Data
@RestController
@RequestMapping("/api/countries")
public class CountryCodeController {
    @Autowired
    private CountryService countryService;

    @Autowired
    private CountryCache countryCache;

    @GetMapping("/code/{countryName}")
    public String getCode(@PathVariable String countryName) {
        return countryService.getCodeByCountry(countryName);
    }

    @GetMapping("/country/{code}")
    public String getCountry(@PathVariable String code) {
        return countryService.getCountryByCode(code);
    }

    @GetMapping
    public List<Country> getAllCountries() {
        return countryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Integer id) {
        Optional<Country> country = countryService.findById(id);
        return country.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Country> createCountry(@RequestBody Country country, @RequestParam Integer personId) {
        Country createdCountry = countryService.create(country, personId);
        return createdCountry != null ? ResponseEntity.ok(createdCountry) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Integer id, @RequestBody Country countryDetails) {
        Country updatedCountry = countryService.update(id, countryDetails);
        return updatedCountry != null ? ResponseEntity.ok(updatedCountry) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cache/size")
    public int getCacheSize() {
        return countryCache.getCacheSize();
    }

    @PostMapping("/cache/clear")
    public ResponseEntity<Void> clearCache() {
        countryCache.clearCache();
        return ResponseEntity.ok().build();
    }
}