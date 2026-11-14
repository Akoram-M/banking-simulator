package com.bank.simulator.accountservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bank.simulator.accountservice.domain.Account;
import com.bank.simulator.accountservice.dto.AccountResponse;
import com.bank.simulator.accountservice.dto.CreateAccountRequest;
import com.bank.simulator.accountservice.dto.TransferRequest;
import com.bank.simulator.accountservice.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    /**
     * -----------------------------------------------------------------------
     * PATTERN: RESTful Resource Creation
     * -----------------------------------------------------------------------
     * URL: POST /api/v1/accounts (No verbs needed)
     * Status: 201 CREATED
     * Security: @Valid triggers Jakarta validation before method execution.
     * -----------------------------------------------------------------------
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account savedAccount = accountService.createAccount(request.ownerName(), request.initialBalance());
        return AccountResponse.fromEntity(savedAccount);
    }
    
    /**
     * -----------------------------------------------------------------------
     * PATTERN: RESTful Sub-resource Actions
     * -----------------------------------------------------------------------
     * URL: POST /api/v1/accounts/transfer
     * Status: 204 NO CONTENT (Standard for operations returning no body)
     * -----------------------------------------------------------------------
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void processTransfer(@Valid @RequestBody TransferRequest request) {
        accountService.processTransfer(request.fromAccountNumber(), request.toAccountNumber(), request.amount());
    }
}