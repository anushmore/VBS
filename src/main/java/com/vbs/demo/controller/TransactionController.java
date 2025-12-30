// this manager will control user table and transaction table
package com.vbs.demo.controller;

import com.vbs.demo.dto.TransactionDto;
import com.vbs.demo.dto.TransferDto;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    TransactionRepo transactionRepo;

    //for deposit
    @PostMapping("/deposit")
    public String deposit(@RequestBody TransactionDto obj){
        User user =userRepo.findById(obj.getId())
                .orElseThrow(()->new RuntimeException("not found"));
        double newBalance=user.getBalance()+ obj.getAmount();
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t= new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs "+obj.getAmount()+" Deposited successfully");
        t.setUserId(obj.getId());
        transactionRepo.save(t);
        return "Deposit Successfully";

    }
    //for withdraw
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionDto obj){
        User user =userRepo.findById(obj.getId())
                .orElseThrow(()->new RuntimeException("not found"));
        double newBalance=user.getBalance()- obj.getAmount();
        if(newBalance<0){
            return "Balance Insufficient ";
        }
        user.setBalance(newBalance);
        userRepo.save(user);

        Transaction t= new Transaction();
        t.setAmount(obj.getAmount());
        t.setCurrBalance(newBalance);
        t.setDescription("Rs "+obj.getAmount()+ " Withdrawal successfully");
        t.setUserId(obj.getId());
        transactionRepo.save(t);
        return "Withdrawal Successfully";

    }

    //for transfer
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferDto obj){
        //for user table
        User sender = userRepo.findById(obj.getId())
                .orElseThrow(()->new RuntimeException("Not found"));
        User rec = userRepo.findByUsername(obj.getUsername());

        if (rec==null) return "Username not found";
        if(sender.getId()== rec.getId()) return "Self transaction Not allowed";
        if(obj.getAmount()<1) return "Invalid amount";
        double sbalance= sender.getBalance()- obj.getAmount();
        double rbalance= rec.getBalance()+ obj.getAmount();

        if(sbalance<0) return "Insufficient balance ";

        sender.setBalance(sbalance);
        rec.setBalance(rbalance);

        userRepo.save(sender);
        userRepo.save(rec);

        //for transaction table
        Transaction t1= new Transaction();
        Transaction t2=new Transaction();


        t1.setAmount(obj.getAmount());
        t1.setCurrBalance(sbalance);
        t1.setDescription("Rs "+obj.getAmount()+" sent to user "+rec.getUsername());
        t1.setUserId(sender.getId());

        t2.setAmount(obj.getAmount());
        t2.setCurrBalance(rbalance);
        t2.setDescription("Rs "+obj.getAmount()+" received from user "+sender.getUsername());
        t2.setUserId(rec.getId());


        transactionRepo.save(t1);
        transactionRepo.save(t2);
        return "Transfer done successfully";





    }

    //for passbook
    @GetMapping("/passbook/{id}")
    public List<Transaction> getPassbook(@PathVariable int id){
        return transactionRepo.findAllByUserId(id);
    }


}
