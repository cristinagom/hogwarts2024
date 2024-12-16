package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "person")
@NamedQuery(name="Person_MasPuntos",query="select p  from Person as p where p.id =" +
        "(select h2.receiver.id  from HousePoint h2 group by h2.receiver.id " +
        "having  sum(h2.points) >= all (select sum(h.points) from HousePoint h group by h.receiver.id))")
public class Person {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", house=" + house.toString() +
                '}';
    }
}