package com.example.mylab.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CountryCache
{
    private final Map<String, String> countryToCodeCache = new HashMap<>();
    private final Map<String, String> codeToCountryCache = new HashMap<>();

    public void putCountryCode(String country, String code)
    {
        countryToCodeCache.put(country.toLowerCase(), code);
        codeToCountryCache.put(code, country);
    }

    public String getCodeByCountry(String country)
    {
        return countryToCodeCache.get(country.toLowerCase());
    }

    public String getCountryByCode(String code)
    {
        return codeToCountryCache.get(code);
    }

    public void clearCache()
    {
        countryToCodeCache.clear();
        codeToCountryCache.clear();
    }

    public int getCacheSize()
    {
        return countryToCodeCache.size();
    }
}