package com.enigma.live_code_springboot.repository;

import com.enigma.live_code_springboot.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxRepository extends JpaRepository <Tax, UUID> {

}
