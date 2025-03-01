package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Role;
import vn.iotstar.repository.IRoleRepository;
import vn.iotstar.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByRoleName(name); // Tìm Role theo tên
    }
}

