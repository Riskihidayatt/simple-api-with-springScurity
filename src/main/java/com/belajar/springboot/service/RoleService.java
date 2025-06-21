// src/main/java/com/enigma/live_code_springboot/service/RoleService.java
package com.belajar.springboot.service;

import com.belajar.springboot.constant.RoleType;
import com.belajar.springboot.entity.Role;

public interface RoleService {

    Role getOrCreate(RoleType roleType);
}