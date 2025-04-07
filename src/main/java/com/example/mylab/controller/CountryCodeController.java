package com.example.mylab.controller;

import com.example.mylab.model.Country;
import com.example.mylab.service.CountryService;
import com.example.mylab.cache.CountryCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для работы с странами и их кодами.
 */
@RestController
@RequestMapping("/api/countries")
<<<<<<< HEAD
@RequiredArgsConstructor
=======
>>>>>>> 72d08c2 (lab4)
@Tag(name = "Country API", description = "Управление странами и кодами стран")
public class CountryCodeController {
    private final CountryService countryService;
    private final CountryCache countryCache;

<<<<<<< HEAD
    @Operation(summary = "Получить код страны по названию",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Код страны найден"),
                    @ApiResponse(responseCode = "404", description = "Страна не найдена")
            })
    @GetMapping("/code/{countryName}")
    public ResponseEntity<String> getCountryCode(
            @Parameter(description = "Название страны", example = "Russia")
            @PathVariable String countryName) {
        String code = countryService.getCodeByCountry(countryName);
=======
    public CountryCodeController(CountryService countryService, CountryCache countryCache) {
        this.countryService = countryService;
        this.countryCache = countryCache;
    }

    @Operation(summary = "Получить код страны по названию")
    @GetMapping("/code/{countryName}")
    public ResponseEntity<String> getCountryCode(@PathVariable String countryName) {
        String cachedCode = countryCache.getCodeByCountry(countryName);
        if (cachedCode != null) {
            return ResponseEntity.ok(cachedCode);
        }

        String code = countryService.getCodeByCountry(countryName);
        if (code != null) {
            countryCache.putCountryCode(countryName, code);
        }

>>>>>>> 72d08c2 (lab4)
        return code != null
                ? ResponseEntity.ok(code)
                : ResponseEntity.notFound().build();
    }

<<<<<<< HEAD
    @Operation(summary = "Получить страну по коду",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страна найдена"),
                    @ApiResponse(responseCode = "404", description = "Код не найден")
            })
    @GetMapping("/country/{code}")
    public ResponseEntity<String> getCountryByCode(
            @Parameter(description = "Код страны", example = "+7")
            @PathVariable String code) {
        String country = countryService.getCountryByCode(code);
=======
    @Operation(summary = "Получить страну по коду")
    @GetMapping("/country/{code}")
    public ResponseEntity<String> getCountryByCode(@PathVariable String code) {
        String cachedCountry = countryCache.getCountryByCode(code);
        if (cachedCountry != null) {
            return ResponseEntity.ok(cachedCountry);
        }

        String country = countryService.getCountryByCode(code);
        if (country != null) {
            countryCache.putCountryCode(country, code);
        }

>>>>>>> 72d08c2 (lab4)
        return country != null
                ? ResponseEntity.ok(country)
                : ResponseEntity.notFound().build();
    }

<<<<<<< HEAD
    @Operation(summary = "Получить все страны")
    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @Operation(summary = "Получить страну по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страна найдена"),
                    @ApiResponse(responseCode = "404", description = "Страна не найдена")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(
            @Parameter(description = "ID страны", example = "1")
            @PathVariable Integer id) {
        return countryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новую страну",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страна создана"),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса")
            })
    @PostMapping("/create")
    public ResponseEntity<Country> createCountry(
            @Parameter(description = "Данные страны")
            @RequestBody Country country,
            @Parameter(description = "ID связанного человека")
            @RequestParam Integer personId) {
        Country createdCountry = countryService.create(country, personId);
=======
    @Operation(summary = "Создать новую страну")
    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody Country country,
                                                 @RequestParam(required = false) Integer code) {
        Country createdCountry = countryService.create(country, code);
        if (createdCountry != null && code != null) {
            countryCache.putCountryCode(createdCountry.getName(), code.toString());
        }
>>>>>>> 72d08c2 (lab4)
        return createdCountry != null
                ? ResponseEntity.ok(createdCountry)
                : ResponseEntity.badRequest().build();
    }

<<<<<<< HEAD
    @Operation(summary = "Обновить данные страны",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены"),
                    @ApiResponse(responseCode = "404", description = "Страна не найдена")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(
            @Parameter(description = "ID страны для обновления", example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Новые данные страны")
            @RequestBody Country countryDetails) {
=======
    @Operation(summary = "Обновить данные страны")
    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Integer id,
                                                 @RequestBody Country countryDetails) {
        countryService.findById(id).ifPresent(country -> {
            String oldCode = countryCache.getCodeByCountry(country.getName());
            if (oldCode != null) {
                countryCache.clearCache();
            }
        });
>>>>>>> 72d08c2 (lab4)
        Country updatedCountry = countryService.update(id, countryDetails);
        return updatedCountry != null
                ? ResponseEntity.ok(updatedCountry)
                : ResponseEntity.notFound().build();
    }

<<<<<<< HEAD
    @Operation(summary = "Удалить страну",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Страна удалена"),
                    @ApiResponse(responseCode = "404", description = "Страна не найдена")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(
            @Parameter(description = "ID страны для удаления", example = "1")
            @PathVariable Integer id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
=======
    @Operation(summary = "Удалить страну")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {
        return countryService.findById(id)
                .map(country -> {
                    String code = countryCache.getCodeByCountry(country.getName());
                    if (code != null) {
                        countryCache.clearCache();
                    }
                    countryService.delete(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
>>>>>>> 72d08c2 (lab4)
    }

    @Operation(summary = "Получить размер кэша стран")
    @GetMapping("/cache/size")
    public ResponseEntity<Integer> getCacheSize() {
        return ResponseEntity.ok(countryCache.getCacheSize());
    }

    @Operation(summary = "Очистить кэш стран")
    @PostMapping("/cache/clear")
    public ResponseEntity<Void> clearCache() {
        countryCache.clearCache();
        return ResponseEntity.ok().build();
    }
}