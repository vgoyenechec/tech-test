package bgt.tech_test.mappers;

import bgt.tech_test.domain.Transaction;
import bgt.tech_test.dto.TransactionDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction){
        if (transaction == null) {
            return null;
        }

        String amount =String.valueOf(transaction.getAmount());
        String balance = transaction.getType().equals("Apertura") ? "+ $".concat(amount) : "- $".concat(amount);
        return TransactionDTO.builder()
                .uid(transaction.getUid())
                .date(transaction.getTimestamp().toString())
                .clientId(transaction.getClientId())
                .fundId(transaction.getFundId())
                .fundName(transaction.getFundName())
                .type(transaction.getType())
                .balance(balance)
                .build();
    };

}
