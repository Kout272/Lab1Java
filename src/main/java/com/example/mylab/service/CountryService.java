package com.example.mylab.service;

import com.example.mylab.cache.CountryCache;
import com.example.mylab.model.Country;
import com.example.mylab.model.Person;
import com.example.mylab.repository.CountryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CountryService {
    @Autowired
    private CountryRepository repository;

    @Autowired
    private PersonService personService;

    @Autowired
    private CountryCache countryCache;

    private List<Country> countryList;

    @PostConstruct
    public void init() {
        countryList = new ArrayList<>();
        loadCountryCodes();
        loadDatabaseCountriesToCache();
    }

    private void loadDatabaseCountriesToCache() {
        repository.findAll().forEach(country -> {
            countryCache.putCountryWithId(country.getId(), country.getName(), country.getCode());
        });
    }

    private void loadCountryCodes() {
        String url = "https://www.ixbt.com/mobile/country_code.html";
        try {
            Document document = Jsoup.connect(url).get();
            Elements rows = document.select("table tbody tr");
            for (Element row : rows) {
                Elements columns = row.select("td");
                if (columns.size() >= 3) {
                    String countryName = columns.get(1).text();
                    String countryCode = columns.get(2).text();
                    Person person = null;
                    countryList.add(new Country(countryName, countryCode, person));
                    countryCache.putCountryCode(countryName, countryCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCodeByCountry(String countryName) {
        String cachedCode = countryCache.getCodeByCountry(countryName);
        if (cachedCode != null) {
            return cachedCode;
        }

        return countryList.stream()
                .filter(country -> country.getName().equalsIgnoreCase(countryName))
                .map(country -> {
                    String code = country.getCode();
                    countryCache.putCountryCode(countryName, code);
                    return code;
                })
                .findFirst()
                .orElseGet(() -> {
                    Optional<Country> dbCountry = repository.findByName(countryName);
                    dbCountry.ifPresent(country ->
                            countryCache.putCountryCode(countryName, country.getCode()));
                    return dbCountry.map(Country::getCode).orElse(null);
                });
    }

    public String getCountryByCode(String code) {
        String cachedCountry = countryCache.getCountryByCode(code);
        if (cachedCountry != null) {
            return cachedCountry;
        }

        return countryList.stream()
                .filter(country -> country.getCode().equals(code))
                .map(country -> {
                    String name = country.getName();
                    countryCache.putCountryCode(name, code);
                    return name;
                })
                .findFirst()
                .orElseGet(() -> {
                    Optional<Country> dbCountry = repository.findByCode(code);
                    dbCountry.ifPresent(country ->
                            countryCache.putCountryCode(country.getName(), code));
                    return dbCountry.map(Country::getName).orElse(null);
                });
    }

    public List<Country> getAllCountries() {
        List<Country> allCountries = new ArrayList<>(countryList);
        allCountries.addAll(repository.findAll());
        return allCountries;
    }

    public List<Country> findAll() {
        return repository.findAll();
    }

    public Optional<Country> findById(Integer id) {
        return repository.findById(id).map(country -> {
            countryCache.putCountryWithId(country.getId(), country.getName(), country.getCode());
            return country;
        });
    }

    public Country create(Country country, Integer personId) {
        Optional<Person> person = personService.findById(personId);
        if (person.isPresent()) {
            country.setPerson(person.get());
            Country savedCountry = repository.save(country);
            countryCache.putCountryWithId(savedCountry.getId(), savedCountry.getName(), savedCountry.getCode());
            return savedCountry;
        }
        return null;
    }

    public Country update(Integer id, Country countryDetails) {
        Optional<Country> optionalCountry = repository.findById(id);
        if (optionalCountry.isPresent()) {
            Country country = optionalCountry.get();
            countryCache.removeById(country.getId());

            country.setName(countryDetails.getName());
            country.setCode(countryDetails.getCode());

            Country updatedCountry = repository.save(country);
            countryCache.putCountryWithId(updatedCountry.getId(), updatedCountry.getName(), updatedCountry.getCode());
            return updatedCountry;
        }
        return null;
    }

    public void delete(Integer id) {
        repository.findById(id).ifPresent(country -> {
            countryCache.removeById(country.getId());
            repository.deleteById(id);
        });
    }
}