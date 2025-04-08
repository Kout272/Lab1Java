// CountryService.java
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
                .orElse(null);
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
                .orElse(null);
    }

    public List<Country> findAll() {
        List<Country> countries = repository.findAll();
        // Обновляем кэш для всех загруженных стран
        countries.forEach(country -> {
            if (country.getName() != null && country.getCode() != null) {
                countryCache.putCountryCode(country.getName(), country.getCode());
            }
        });
        return countries;
    }

    public Optional<Country> findById(Integer id) {
        Optional<Country> country = repository.findById(id);
        country.ifPresent(c -> {
            if (c.getName() != null && c.getCode() != null) {
                countryCache.putCountryCode(c.getName(), c.getCode());
            }
        });
        return country;
    }

    public Country create(Country country, Integer personId) {
        Optional<Person> person = personService.findById(personId);
        if (person.isPresent()) {
            country.setPerson(person.get());
            Country created = repository.save(country);
            if (created.getName() != null && created.getCode() != null) {
                countryCache.putCountryCode(created.getName(), created.getCode());
            }
            return created;
        }
        return null;
    }

    public Country update(Integer id, Country countryDetails) {
        return repository.findById(id)
                .map(existingCountry -> {
                    // Удаляем старые значения из кэша
                    if (existingCountry.getName() != null) {
                        countryCache.removeByCountry(existingCountry.getName());
                    }
                    if (existingCountry.getCode() != null) {
                        countryCache.removeByCode(existingCountry.getCode());
                    }

                    // Обновляем страну
                    existingCountry.setName(countryDetails.getName());
                    existingCountry.setCode(countryDetails.getCode());

                    Country updated = repository.save(existingCountry);

                    // Добавляем новые значения в кэш
                    if (updated.getName() != null && updated.getCode() != null) {
                        countryCache.putCountryCode(updated.getName(), updated.getCode());
                    }
                    return updated;
                })
                .orElse(null);
    }

    public void delete(Integer id) {
        repository.findById(id).ifPresent(country -> {
            // Удаляем из кэша перед удалением из БД
            if (country.getName() != null) {
                countryCache.removeByCountry(country.getName());
            }
            if (country.getCode() != null) {
                countryCache.removeByCode(country.getCode());
            }
            repository.deleteById(id);
        });
    }
}