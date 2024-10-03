package bgt.tech_test.repositories;

import bgt.tech_test.domain.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
}
