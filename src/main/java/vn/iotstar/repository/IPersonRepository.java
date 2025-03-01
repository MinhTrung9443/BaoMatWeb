package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.iotstar.entity.Person;

@Repository
public interface IPersonRepository extends JpaRepository<Person, Integer>{
	@Query("SELECT p FROM Person p WHERE p.account.username = :username")
    Person findByAccountUsername(@Param("username") String username);
	Person findByEmail(String email);
	Person findByPhone(String phone);
}
