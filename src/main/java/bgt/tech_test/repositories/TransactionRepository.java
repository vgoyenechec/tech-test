package bgt.tech_test.repositories;

import bgt.tech_test.domain.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findAllByClientId(String clientId);
}
