package com.example.mylab.service;

import com.example.mylab.model.Person;
import com.example.mylab.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository repository;

    public List<Person> findAll() {
        return repository.findAll();
    }

    public Optional<Person> findById(Integer id) {
        return repository.findById(id);
    }

    public Person create(Person person) {
        return repository.save(person);
    }

    public Person update(Integer id, Person personDetails) {
        Optional<Person> optionalPerson = repository.findById(id);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            person.setName(personDetails.getName());
            person.setSurname(personDetails.getSurname());
            return repository.save(person);
        }
        return null;
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public List<Person> findByCountryName(String countryName) {
        return repository.findPersonsByCountryName(countryName);
    }
}