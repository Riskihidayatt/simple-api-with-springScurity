package com.belajar.springboot.repository;

import com.belajar.springboot.entity.Transaction;
import com.belajar.springboot.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionDetailRepository extends JpaRepository <TransactionDetail, UUID>{
    List<TransactionDetail>findByTransaction(Transaction transaction);

}
