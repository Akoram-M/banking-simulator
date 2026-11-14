package com.bank.simulator.accountservice.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * ---------------------------------------------------------------------------
 * ANNOTATION DEEP DIVE: @Entity
 * ---------------------------------------------------------------------------
 * WHY NOT A JAVA 17 RECORD?: Java Records are immutable (their fields are final). 
 * JPA requires entities to be mutable because Hibernate uses the Default Constructor 
 * to instantiate the object, and then uses Setter injection (or direct field access) 
 * via Reflection to populate the data from the database. 
 * Furthermore, Hibernate creates a "Proxy" subclass of this entity at runtime to 
 * handle Lazy Loading and Dirty Checking. Proxies cannot extend Records.
 * ---------------------------------------------------------------------------
 */
@Entity
@Table(name = "accounts")
public class Account {

    /**
     * -----------------------------------------------------------------------
     * ANNOTATION: @Id & @GeneratedValue
     * -----------------------------------------------------------------------
     * We use UUID instead of Long for distributed systems. 
     * WHY: If we merge databases or migrate data, Long IDs (1, 2, 3) will collide. 
     * UUIDs are globally unique.
     * -----------------------------------------------------------------------
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String ownerName;

    /**
     * -----------------------------------------------------------------------
     * JAVA FEATURE: BigDecimal (Not Double/Float)
     * -----------------------------------------------------------------------
     * NEVER use double or float for currency. Floating point math is imprecise 
     * (e.g., 0.1 + 0.2 = 0.30000000000000004 in Java). BigDecimal provides 
     * exact precision for financial calculations.
     * -----------------------------------------------------------------------
     */
    @Column(nullable = false)
    private BigDecimal balance;

    /**
     * -----------------------------------------------------------------------
     * PATTERN: Optimistic Locking via @Version
     * -----------------------------------------------------------------------
     * THIS IS A CRITICAL INTERVIEW CONCEPT.
     * SCENARIO (Lost Update Problem):
     * 1. Thread A reads Account (Balance $100, Version 1).
     * 2. Thread B reads Account (Balance $100, Version 1).
     * 3. Thread A withdraws $50, saves Balance $50. Hibernate sets Version to 2.
     * 4. Thread B deposits $20, tries to save Balance $120. 
     * WHAT HAPPENS?: Hibernate sees Thread B is trying to update Version 1, but 
     * the DB is now at Version 2. Hibernate throws an ObjectOptimisticLockingFailureException.
     * Without @Version, Thread B would overwrite Thread A's withdrawal, destroying $50.
     * -----------------------------------------------------------------------
     */
    @Version
    private Long version;

    /**
     * -----------------------------------------------------------------------
     * JAVA 8+ FEATURE: java.time.Instant vs java.util.Date
     * -----------------------------------------------------------------------
     * java.util.Date is mutable and not thread-safe. 
     * Instant represents a specific moment on the timeline in UTC. It is immutable,
     * thread-safe, and the modern standard for auditing timestamps.
     * -----------------------------------------------------------------------
     */
    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    /**
     * -----------------------------------------------------------------------
     * ANNOTATION: @PrePersist / @PreUpdate (JPA Lifecycle Callbacks)
     * -----------------------------------------------------------------------
     * Automatically executes before Hibernate flushes the SQL INSERT or UPDATE 
     * to the database. Removes the need to manually set timestamps in your Service layer.
     * -----------------------------------------------------------------------
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Required by JPA
    protected Account() {}

    public Account(String accountNumber, String ownerName, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    // Standard Getters (Omitted for brevity - generate them in your IDE)
    public UUID getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    
    /**
     * -----------------------------------------------------------------------
     * PATTERN: Rich Domain Model (Behavior inside the Entity)
     * -----------------------------------------------------------------------
     * Anemic Domain Models (where entities only have getters/setters and services 
     * do all the math) are an anti-pattern in modern architecture. 
     * The Account entity should protect its own invariants (rules).
     * -----------------------------------------------------------------------
     */
    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }
}