//manager of user table
package com.vbs.demo.controller;


import com.vbs.demo.dto.DisplayDto;
import com.vbs.demo.dto.LoginDto;
import com.vbs.demo.dto.UpdateDto;
import com.vbs.demo.models.History;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.HistoryRepo;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//to declare that it is controller
@CrossOrigin(origins="*") //to communicate between both ports

public class UserController {
    @Autowired
    UserRepo userRepo;

    @Autowired
    HistoryRepo historyRepo;

    @Autowired
    TransactionRepo transactionRepo;
    // for register
    @PostMapping("/register") //it will trigger the below function when it is post and register (it is a condition)
    public String register(@RequestBody User user) // if condition pass then it will take data from request body
    {
        userRepo.save(user);
        return "Signup Successful";

    }
    //for login
    @PostMapping("/login")
    public String login (@RequestBody LoginDto u){
        User user= userRepo.findByUsername(u.getUsername());
        if(user==null){
            return "User not found";
        }
        if (!u.getPassword().equals(user.getPassword())){
            return"Password Incorrect";

        }
        if(!u.getRole().equals(user.getRole())){
            return "Role Incorrect";
        }
        return String.valueOf(user.getId());
    }
    // to give accurate balance and change username after refresh
    @GetMapping("/get-details/{id}")
    public DisplayDto display(@PathVariable int id)
    {
        User user = userRepo.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
        DisplayDto displayDto= new DisplayDto();
        displayDto.setUsername(user.getUsername());
        displayDto.setBalance(user.getBalance());
        return displayDto;
    }

    //for profile update
    @PostMapping("/update")
    public String update (@RequestBody UpdateDto obj){
        User user=userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Not found"));
        if(obj.getKey().equalsIgnoreCase("name")){
            if(obj.getValue().equals(user.getName())) return "Cannot be same";
            user.setName(obj.getValue());
        }
        else if(obj.getKey().equalsIgnoreCase("password")){
            if(obj.getValue().equals(user.getPassword())) return "Cannot be same";
            user.setPassword(obj.getValue());
        }
        else if(obj.getKey().equalsIgnoreCase("email")){
            if(obj.getValue().equals(user.getEmail())) return "Cannot be same";
            User user2=userRepo.findByEmail(obj.getValue());
            if (user2!=null) return "Email Already exists";
            user.setEmail(obj.getValue());
        }
        else {
            return "Invalid key";
        }

        userRepo.save(user);
        return "Updated successfully";

    }

    //in admin panel add user feature
    @PostMapping("/add/{adminId}")
    public String add(@RequestBody User user , @PathVariable int adminId){
        History h1= new History();
        h1.setDescription("Admin "+adminId+" Created user "+user.getUsername());
        historyRepo.save(h1);
        userRepo.save(user);

        if (user.getBalance()>0){
            Transaction t = new Transaction();
            t.setAmount(user.getBalance());
            t.setCurrBalance(user.getBalance());
            t.setDescription("Rs "+user.getBalance()+" Deposit Successfully");
            t.setUserId(user.getId());
            transactionRepo.save(t);
        }


        return "Added Successfully";
    }


    //to fetch users who has customer role


    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam String sortBy,@RequestParam String order){

        Sort sort;
        if(order.equalsIgnoreCase("desc")){
            sort =Sort.by(sortBy).descending();

        }
        else{
            sort=Sort.by(sortBy).ascending();
        }
        return userRepo.findAllByRole("customer",sort);
    }

    //find by using username in search bar
    @GetMapping("/users/{keyword}")
    public List<User> getUsers(@PathVariable String keyword ){

        return userRepo.findByUsernameContainingIgnoreCaseAndRole(keyword,"customer");
    }

    @DeleteMapping("delete-user/{userId}/admin/{adminId}")
    public String delete(@PathVariable int userId,@PathVariable int adminId){
        User user =userRepo.findById(userId)
                .orElseThrow(()->new RuntimeException("not Found"));
        if(user.getBalance()>0){
            return "Balance Should be zero";
        }
        userRepo.delete(user);
        History h1= new History();
        h1.setDescription("Admin "+adminId+" Deleted user "+user.getUsername());
        historyRepo.save(h1);
        return " User deleted successfully";
    }

}


























