package bgt.tech_test.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@Data
public class Subscription {
    @Field("fund_id")
    private String fundId;

    @Field("balance")
    private double balance;

}
