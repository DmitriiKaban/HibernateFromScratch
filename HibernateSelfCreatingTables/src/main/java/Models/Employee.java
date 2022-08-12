package Models;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
// make second level cache enable
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee {
    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private String name;
    private int age;
    @OneToMany(mappedBy = "owner")
    private List<Laptop> devices = new ArrayList<>();

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Laptop> getDevices() {
        return devices;
    }

    public void setDevices(List<Laptop> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "Name is " + name +
                ", age is " + age;
    }
}
