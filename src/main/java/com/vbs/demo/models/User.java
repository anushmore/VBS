//table of user
package com.vbs.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity //this will make columns in database
@Data //getter & setter
@AllArgsConstructor //constructors
@NoArgsConstructor  //constructors

public class User {
    @Id //this will make int id primary key
            @GeneratedValue(strategy = GenerationType.IDENTITY) // generates id uniquely
    int id;
    @Column(nullable = false,unique = true) // this will not make column null
    String username;
    @Column(nullable = false)
    String password;
    @Column(nullable = false,unique = true)
    String email;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String role;
    @Column(nullable = false)
    double balance;

}
