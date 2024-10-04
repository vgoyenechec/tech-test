package bgt.tech_test;


import bgt.tech_test.controllers.AccountController;
import bgt.tech_test.dto.TransactionDTO;
import bgt.tech_test.dto.TransactionResponseDTO;
import bgt.tech_test.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;

    private TransactionResponseDTO successResponse;
    private TransactionResponseDTO errorResponse;
    private List<TransactionDTO> transactionDTOList;

    @BeforeEach
    void setUp() {
        successResponse = TransactionResponseDTO.builder()
                .code("00")
                .message("Transacción exitosa")
                .build();

        errorResponse = TransactionResponseDTO.builder()
                .code("99")
                .message("Error en la transacción")
                .build();

        TransactionDTO transaction1 = TransactionDTO.builder()
                .uid("tx1001")
                .fundId("fund123")
                .balance("1000.0")
                .type("OPEN")
                .build();

        TransactionDTO transaction2 = TransactionDTO.builder()
                .uid("tx1002")
                .fundId("fund123")
                .balance("500.0")
                .type("CLOSE")
                .build();

        transactionDTOList = Arrays.asList(transaction1, transaction2);
    }

    @Test
    @DisplayName("Testing: /openFund When: On:Success")
    void openFund_Success() throws Exception {
        String clientId = "client123";
        String fundId = "fundABC";
        Double amount = 1000.0;

        Mockito.when(accountService.createSubscription(eq(fundId), eq(clientId), eq(amount)))
                .thenReturn(successResponse);

        mockMvc.perform(post("/openFund")
                        .param("clientId", clientId)
                        .param("fundId", fundId)
                        .param("amount", amount.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Transacción exitosa"));
    }

    @Test
    @DisplayName("Testing: /openFund When: On:Fail")
    void openFund_onFail() throws Exception {
        String clientId = "client123";
        String fundId = "fundABC";
        Double amount = 1000.0;

        Mockito.when(accountService.createSubscription(eq(fundId), eq(clientId), eq(amount)))
                .thenReturn(errorResponse);

        mockMvc.perform(post("/openFund")
                        .param("clientId", clientId)
                        .param("fundId", fundId)
                        .param("amount", amount.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("99"))
                .andExpect(jsonPath("$.message").value("Error en la transacción"));
    }

    @Test
    @DisplayName("Testing: /closeFund When: On:Success")
    void closeFund_Success() throws Exception {
        String clientId = "client123";
        String fundId = "fundABC";

        Mockito.when(accountService.cancelSubscription(eq(clientId), eq(fundId)))
                .thenReturn(successResponse);

        mockMvc.perform(post("/closeFund")
                        .param("clientId", clientId)
                        .param("fundId", fundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"))
                .andExpect(jsonPath("$.message").value("Transacción exitosa"));
    }

    @Test
    @DisplayName("Testing: /closeFund When: On:Fail")
    void closeFund_onFail() throws Exception {
        String clientId = "client123";
        String fundId = "fundABC";

        Mockito.when(accountService.cancelSubscription(eq(clientId), eq(fundId)))
                .thenReturn(errorResponse);

        mockMvc.perform(post("/closeFund")
                        .param("clientId", clientId)
                        .param("fundId", fundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("99"))
                .andExpect(jsonPath("$.message").value("Error en la transacción"));
    }

    @Test
    @DisplayName("Testing: /transactions When: On:Success")
    void transactions_Success() throws Exception {
        String clientId = "client123";
        String fundId = "fundABC";

        Mockito.when(accountService.getLastTransactions(eq(clientId)))
                .thenReturn(transactionDTOList);

        mockMvc.perform(get("/transactions")
                        .param("clientId", clientId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].uid").value("tx1001"))
                .andExpect(jsonPath("$[0].fundId").value("fund123"))
                .andExpect(jsonPath("$[0].balance").value(1000.0))
                .andExpect(jsonPath("$[0].type").value("OPEN"))
                .andExpect(jsonPath("$[1].uid").value("tx1002"))
                .andExpect(jsonPath("$[1].fundId").value("fund123"))
                .andExpect(jsonPath("$[1].balance").value(500.0))
                .andExpect(jsonPath("$[1].type").value("CLOSE"));
    }

}
