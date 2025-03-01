package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.iotstar.entity.Account;

public interface IEmailRepository  extends JpaRepository<Account, Integer> {

}
