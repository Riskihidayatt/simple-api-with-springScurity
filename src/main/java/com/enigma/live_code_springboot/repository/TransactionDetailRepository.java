package com.enigma.live_code_springboot.repository;

import com.enigma.live_code_springboot.entity.Transaction;
import com.enigma.live_code_springboot.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionDetailRepository extends JpaRepository <TransactionDetail, UUID>{
    List<TransactionDetail>findByTransaction(Transaction transaction);

}
