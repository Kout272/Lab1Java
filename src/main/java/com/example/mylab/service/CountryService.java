package com.example.mylab.service;

import com.example.mylab.model.Country;
import com.example.mylab.model.Person;
import com.example.mylab.repository.CountryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private List<Country> countryList;

    public CountryService() {
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCodeByCountry(String countryName) {
        return countryList.stream()
                .filter(country -> country.getName().equalsIgnoreCase(countryName))
                .map(Country::getCode)
                .findFirst()
                .orElse(null);
    }

    public String getCountryByCode(String code) {
        return countryList.stream()
                .filter(country -> country.getCode().equals(code))
                .map(Country::getName)
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
        return null; // Или выбросьте исключение, если персона не найдена
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