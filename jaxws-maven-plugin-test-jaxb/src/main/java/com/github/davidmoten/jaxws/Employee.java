package com.github.davidmoten.jaxws;
 
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
 
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "employee")
public class Employee {
    @XmlAttribute
    private int id;
     
    private String name;
    private double salary;
    private String designation;
     
    private Address address;
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public double getSalary() {
        return salary;
    }
 
    public void setSalary(double salary) {
        this.salary = salary;
    }
 
    public String getDesignation() {
        return designation;
    }
 
    public void setDesignation(String designation) {
        this.designation = designation;
    }
 
    public Address getAddress() {
        return address;
    }
 
    public void setAddress(Address address) {
        this.address = address;
    }
}