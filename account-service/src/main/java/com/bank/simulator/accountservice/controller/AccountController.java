package com.bank.simulator.accountservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.simulator.accountservice.domain.Account;
import com.bank.simulator.accountservice.dto.CreateAccountRequest;
import com.bank.simulator.accountservice.dto.TransferRequest;
import com.bank.simulator.accountservice.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService acService){
        accountService = acService;
    }

    @PostMapping("/createAccount")
    public Account createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request.ownerName(), request.initialBalance());
    }
    
    @PostMapping("/proccessTransfer")
    public void processTransfer(@RequestBody TransferRequest request) {
        accountService.processTransfer(request.fromAccountNumber(), request.toAccountNumber(), request.amount());
    }
    
}
