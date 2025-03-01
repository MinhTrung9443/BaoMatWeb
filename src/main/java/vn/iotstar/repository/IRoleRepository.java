package vn.iotstar.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Role;
@Repository
public interface IRoleRepository extends JpaRepository<Role, Integer> {
	public Role findByRoleName(String roleName); // Truy vấn dựa trên tên vai trò
}
