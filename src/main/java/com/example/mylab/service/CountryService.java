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
                    // Заполняем кэш при загрузке
                    countryCache.putCountryCode(countryName, countryCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCodeByCountry(String countryName) {
        // Сначала проверяем кэш
        String cachedCode = countryCache.getCodeByCountry(countryName);
        if (cachedCode != null) {
            return cachedCode;
        }

        // Если нет в кэше, ищем в списке и обновляем кэш
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
        // Сначала проверяем кэш
        String cachedCountry = countryCache.getCountryByCode(code);
        if (cachedCountry != null) {
            return cachedCountry;
        }

        // Если нет в кэше, ищем в списке и обновляем кэш
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

    public List<Country> getAllCountries() {
        return countryList;
    }

    public List<Country> findAll() {
        return repository.findAll();
    }

    public Optional<Country> findById(Integer id) {
        return repository.findById(id);
    }

    public Country create(Country country, Integer personId) {
        Optional<Person> person = personService.findById(personId);
        if (person.isPresent()) {
            country.setPerson(person.get());
            return repository.save(country);
        }
        return null;
    }

    public Country update(Integer id, Country countryDetails) {
        Optional<Country> optionalCountry = repository.findById(id);
        if (optionalCountry.isPresent()) {
            Country country = optionalCountry.get();
            country.setName(countryDetails.getName());
            country.setCode(countryDetails.getCode());
            return repository.save(country);
        }
        return null;
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}