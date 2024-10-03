package bgt.tech_test.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

@Getter
@Setter
@Document(collection = "subscriptions")
public class Subscription {
    @Id
    private String id;

    @Field("client_id")
    private String clientId;

    @Field("fund_id")
    private String fundId;

    @Field("subscription_date")
    private Date subscriptionDate;

    @Field("current_balance")
    private double currentBalance;

    private String status;  // active or cancelled

}
