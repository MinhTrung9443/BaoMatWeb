package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.iotstar.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
	@Query("SELECT u FROM User u WHERE u.account.username = :username")
    User findByAccountUsername(@Param("username") String username);
		
	List<User> findByFullnameContaining(String fullname);
}
