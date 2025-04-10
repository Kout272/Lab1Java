package com.example.mylab.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CountryCache {
    private final Map<String, String> countryToCodeCache = new ConcurrentHashMap<>();
    private final Map<String, String> codeToCountryCache = new ConcurrentHashMap<>();
    private final Map<Integer, String> idToCountryCache = new ConcurrentHashMap<>();
    private final Map<Integer, String> idToCodeCache = new ConcurrentHashMap<>();

    public void putCountryCode(String country, String code) {
        countryToCodeCache.put(country.toLowerCase(), code);
        codeToCountryCache.put(code, country);
    }

    public void putCountryWithId(Integer id, String country, String code) {
        putCountryCode(country, code);
        idToCountryCache.put(id, country);
        idToCodeCache.put(id, code);
    }

    public String getCodeByCountry(String country) {
        return countryToCodeCache.get(country.toLowerCase());
    }

    public String getCountryByCode(String code) {
        return codeToCountryCache.get(code);
    }

    public String getCountryById(Integer id) {
        return idToCountryCache.get(id);
    }

    public String getCodeById(Integer id) {
        return idToCodeCache.get(id);
    }

    public void removeCountry(String country) {
        String code = countryToCodeCache.remove(country.toLowerCase());
        if (code != null) {
            codeToCountryCache.remove(code);
        }
    }

    public void removeCode(String code) {
        String country = codeToCountryCache.remove(code);
        if (country != null) {
            countryToCodeCache.remove(country.toLowerCase());
        }
    }

    public void removeById(Integer id) {
        String country = idToCountryCache.remove(id);
        String code = idToCodeCache.remove(id);
        if (country != null && code != null) {
            removeCountry(country);
            removeCode(code);
        }
    }

    public void putAllCountries(Map<String, String> countryCodeMap) {
        countryCodeMap.forEach(this::putCountryCode);
    }

    public void putAllCountriesWithIds(Map<Integer, Map.Entry<String, String>> idCountryCodeMap) {
        idCountryCodeMap.forEach((id, entry) ->
                putCountryWithId(id, entry.getKey(), entry.getValue()));
    }

    public boolean containsCountry(String country) {
        return countryToCodeCache.containsKey(country.toLowerCase());
    }

    public boolean containsCode(String code) {
        return codeToCountryCache.containsKey(code);
    }

    public boolean containsId(Integer id) {
        return idToCountryCache.containsKey(id);
    }

    public void clearCache() {
        countryToCodeCache.clear();
        codeToCountryCache.clear();
        idToCountryCache.clear();
        idToCodeCache.clear();
    }

    public void clearCountryCache() {
        countryToCodeCache.clear();
    }

    public void clearCodeCache() {
        codeToCountryCache.clear();
    }

    public void clearIdCache() {
        idToCountryCache.clear();
        idToCodeCache.clear();
    }

    public int getCacheSize() {
        return countryToCodeCache.size();
    }

    public int getCodeCacheSize() {
        return codeToCountryCache.size();
    }

    public int getIdCacheSize() {
        return idToCountryCache.size();
    }

    public Map<String, String> getAllCountries() {
        return new HashMap<>(countryToCodeCache);
    }

    public Map<String, String> getAllCodes() {
        return new HashMap<>(codeToCountryCache);
    }

    public Map<Integer, String> getAllCountriesById() {
        return new HashMap<>(idToCountryCache);
    }

    public Map<Integer, String> getAllCodesById() {
        return new HashMap<>(idToCodeCache);
    }
}