package bgt.tech_test.controllers;

import bgt.tech_test.dto.TransactionDTO;
import bgt.tech_test.dto.TransactionResponseDTO;
import bgt.tech_test.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;


    @PostMapping("/openFund")
    public ResponseEntity<TransactionResponseDTO> openFund(@RequestParam String clientId,
                                                           @RequestParam String fundId,
                                                           @RequestParam Double amount) {
        TransactionResponseDTO responseDTO = accountService.createSubscription(fundId, clientId, amount);

        if (responseDTO.getCode().equals("00")) {
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/closeFund")
    public ResponseEntity<TransactionResponseDTO> closeFund(@RequestParam String clientId,
                                                            @RequestParam String fundId) {
        TransactionResponseDTO responseDTO = accountService.cancelSubscription(clientId, fundId);

        if (responseDTO.getCode().equals("00")) {
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@RequestParam String clientId) {
        return ResponseEntity.ok(accountService.getLastTransactions(clientId));
    }
}
