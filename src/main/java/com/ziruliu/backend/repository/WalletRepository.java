package com.ziruliu.backend.repository;

import com.ziruliu.backend.entity.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {
}
