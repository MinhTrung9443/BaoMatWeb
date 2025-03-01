package vn.iotstar.service;

import org.springframework.stereotype.Service;

import vn.iotstar.entity.Role;
@Service
public interface IRoleService {

	Role findByName(String name);

}
