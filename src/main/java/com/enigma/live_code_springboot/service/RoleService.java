// src/main/java/com/enigma/live_code_springboot/service/RoleService.java
package com.enigma.live_code_springboot.service;

import com.enigma.live_code_springboot.constant.RoleType;
import com.enigma.live_code_springboot.entity.Role;

public interface RoleService {

    Role getOrCreate(RoleType roleType);
}