// src/main/java/com/enigma/live_code_springboot/service/impl/RoleServiceImpl.java
package com.enigma.live_code_springboot.service.impl;

import com.enigma.live_code_springboot.constant.RoleType;
import com.enigma.live_code_springboot.entity.Role;
import com.enigma.live_code_springboot.repository.RoleRepository;
import com.enigma.live_code_springboot.service.RoleService;
import lombok.RequiredArgsConstructor; // <-- PASTIKAN IMPORT INI ADA
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role getOrCreate(RoleType roleType) {
        // Baris 16 (tempat error terjadi menurut stack trace)
        return roleRepository.findByName(roleType).orElseGet(() -> { // Error karena roleRepository null
            Role newRole = Role.builder()
                    .name(roleType)
                    .build();
            return roleRepository.save(newRole);
        });
    }
}