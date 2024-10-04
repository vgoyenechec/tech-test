package bgt.tech_test.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class TransactionResponseDTO implements Serializable {
    private String code;
    private String message;

}
