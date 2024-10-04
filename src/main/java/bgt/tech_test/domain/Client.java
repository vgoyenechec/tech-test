package bgt.tech_test.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "clients")
public class Client {
    @Id
    private String id;

    private String email;
    private String phone;
    private String name;
    private double balance;

    @Field("preferred_notification")
    private String preferredNotification; // sms or email

    private List<Subscription> subscriptions;

}
