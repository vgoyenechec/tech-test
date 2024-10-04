package bgt.tech_test.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@Document(collection = "funds")
public class Fund {
    @Id
    private String id;

    private String name;

    @Field("minimum_subscription_amount")
    private double minimumSubscriptionAmount;

    private String category;

}
