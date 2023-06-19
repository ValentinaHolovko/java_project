package db;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Person {
    @NotNull
    private Integer id;

    private String firstName;

    @Nullable
    private String lastName;

    public Person() {
        id = 0;
    }

    public Person(@NotNull Integer id, String firstName, @Nullable String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Person parsePerson(String[] strings) {
        if (strings.length < 3) {
            return new Person(Integer.valueOf(strings[0]), strings[1], null);
        }
        return new Person(Integer.valueOf(strings[0]), strings[1], strings[2]);

    }

    @NotNull
    public Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@Nullable String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "db.Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

}
