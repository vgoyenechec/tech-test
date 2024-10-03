package bgt.tech_test.domain;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

@Getter
@Setter
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String uid;

    private Date timestamp;
    private String type;

    @Field("client_id")
    private String clientId;

    @Field("fund_id")
    private String fundId;

    private double amount;
    private String status;

    @Field("notification_sent")
    private boolean notificationSent;

}

