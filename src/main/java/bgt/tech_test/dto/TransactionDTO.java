package bgt.tech_test.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class TransactionDTO implements Serializable {
    private String uid;
    private String clientId;
    private String fundId;
    private String fundName;
    private String date;
    private String type;
    private String balance;

}
