package bgt.tech_test.repositories;

import bgt.tech_test.domain.Fund;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FundRepository extends MongoRepository<Fund,String> {
}
