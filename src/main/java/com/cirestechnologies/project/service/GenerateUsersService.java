package com.cirestechnologies.project.service;

import com.cirestechnologies.project.model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Date;
import java.util.Random;

// this class is used to generate random users
public class GenerateUsersService {

    private String[] firstName = {
            "Hamza", "Bassma", "Ali", "Issam", "Maroua", "Mehdi", "Khalid", "Yaasir", "Yahya", "Aziz",
            "Adil", "Rajaa", "Fareed", "Amal", "Eman", "Hafsa", "Ikram", "Said"
    };
    private String[] lastName = {
            "Nait boubker",
            "Jabiri",
            "Hassani",
            "Lassiri",
            "Jaroudi",
            "Jziri",
            "Shariq",
            "Ganem",
            "Chentouf",
            "Hajji",
            "Slimani",
            "Ktibi",
            "Hemmadi",
            "Ahssain",
            "Ghadi",
            "Fenane"
    };
    private String[] company = {
            "Cires Technologies",
            "Inwi",
            "Marjane",
            "Maroc Telecom",
            "ONCF",
            "Royal Air Maroc",
            "GemoGraphy",
            "CGI"
    };
    private String[] cities = {"Tanger", "Rabat", "Casablanca", "Marrakech", "Agadir"};
    private String[] jobPosition = {
            "Software Engineer",
            "Sales Manager",
            "Marketing Manager",
            "Chief Executive Officer",
            "Graphic designer",
            "Machine learning Engineer"
    };
    private String[] role = {"admin", "user"};

    public User generateOneUser() {

        String firstName = getRandom(this.firstName);
        String lastName = getRandom(this.lastName);
        String company = getRandom(this.company);
        String username = firstName + lastName.subSequence(0, 3);
        String email = (firstName + "." + lastName + "@" + company + ".com").replace(" ", "");

        return new User(
                firstName,
                lastName,
                generateRandomDate(),
                getRandom(this.cities),
                "MA",
                "Avatar",
                company,
                getRandom(this.jobPosition),
                ("+212" + RandomStringUtils.randomNumeric(8)),
                username,
                email,
                RandomStringUtils.randomAlphanumeric(6, 11),
                getRandom(this.role));
    }

    int generateRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public Date generateRandomDate() {

        int year = generateRandomNumber(1980, 1998);
        int month = generateRandomNumber(1, 12);
        int day = generateRandomNumber(1, 28);

        return Date.valueOf(year + "-" + month + "-" + day);
    }

    public String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

}
