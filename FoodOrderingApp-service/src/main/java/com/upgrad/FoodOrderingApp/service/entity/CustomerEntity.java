package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "customer", schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "customerByContact_number", query = "select u from CustomerEntity u where u.contact_number = :contact_number"),
                @NamedQuery(name = "customerById", query = "select u from CustomerEntity u where u.id = :id"),
                @NamedQuery(name = "customerByUuid", query = "select u from CustomerEntity u where u.uuid = :uuid")
        }
)

public class CustomerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "FIRSTNAME")
    @NotNull
    @Size(max = 30)
    private String firstName;

    @Column(name = "LASTNAME")
    @Size(max = 30)
    private String lastName;

    @Column(name = "EMAIL",unique = true)
    @NotNull
    @Size(max = 50)
    private String email;

    @Column(name = "CONTACT_NUMBER")
    @Size(max = 30)
    private String contact_number;

    @ToStringExclude
    @Column(name = "PASSWORD")
    @NotNull
    @Size(max = 255)
    private String password;

    @ToStringExclude
    @Column(name = "SALT")
    @NotNull
    @Size(max = 255)
    private String salt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
