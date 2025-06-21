package com.belajar.springboot.repository;

import com.belajar.springboot.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxRepository extends JpaRepository <Tax, UUID> {

}
