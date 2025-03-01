package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Vendor;

@Repository
public interface IAccountRepository  extends JpaRepository<Account, Integer> {

	Account findByUsername(String username);

    Account findByToken(String token);
	

}
