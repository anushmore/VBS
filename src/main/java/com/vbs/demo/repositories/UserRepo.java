//  a worker which will communicate with table of user

package com.vbs.demo.repositories;

import com.vbs.demo.models.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository; // Userrepo needs help of this to make table by using user and integer
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository // to declare it is repository
public interface UserRepo extends JpaRepository <User,Integer> {

    User findByUsername(String username);

    User findByEmail(String value);

    List<User> findAllByRole(String customer, Sort sort);

    List<User> findByUsernameContainingIgnoreCaseAndRole(String keyword, String customer);
}
