package bgt.tech_test;

import bgt.tech_test.domain.Client;
import bgt.tech_test.domain.Fund;
import bgt.tech_test.domain.Subscription;
import bgt.tech_test.domain.Transaction;
import bgt.tech_test.dto.TransactionDTO;
import bgt.tech_test.dto.TransactionResponseDTO;
import bgt.tech_test.exceptions.BusinessException;
import bgt.tech_test.mappers.TransactionMapper;
import bgt.tech_test.repositories.ClientRepository;
import bgt.tech_test.repositories.FundRepository;
import bgt.tech_test.repositories.TransactionRepository;
import bgt.tech_test.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private FundRepository fundRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountService accountService;

    private Client client;
    private Fund fund;
    private Subscription subscription;
    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private TransactionResponseDTO successResponse;
    private TransactionResponseDTO errorResponse;
    private List<Transaction> transactionList;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id("client123")
                .email("cliente@ejemplo.com")
                .phone("123456789")
                .name("Juan Pérez")
                .balance(5000.0)
                .preferredNotification("EMAIL")
                .subscriptions(new ArrayList<>())
                .build();

        fund = Fund.builder()
                .id("fundABC")
                .name("Fondo ABC")
                .minimumSubscriptionAmount(1000.0)
                .build();

        subscription = Subscription.builder()
                .fundId("fundABC")
                .balance(1000.0)
                .build();

        transaction = Transaction.builder()
                .uid(UUID.randomUUID().toString())
                .timestamp(new Date())
                .clientId("client123")
                .amount(1000.0)
                .fundId("fundABC")
                .fundName("Fondo ABC")
                .type("Apertura")
                .notificationSent(true)
                .build();

        transactionDTO = TransactionDTO.builder()
                .uid(transaction.getUid())
                .fundId(transaction.getFundId())
                .fundName(transaction.getFundName())
                .balance(String.valueOf(transaction.getAmount()))
                .type(transaction.getType())
                .build();

        successResponse = TransactionResponseDTO.builder()
                .code("00")
                .message("Transacción exitosa")
                .build();

        errorResponse = TransactionResponseDTO.builder()
                .code("99")
                .message("Ocurrió un error")
                .build();

        transactionList = Collections.singletonList(transaction);
    }

    @Test
    @DisplayName("Testing: getLastTransactions On:Success")
    void getLastTransactions_TransactionsFound() {
        when(transactionRepository.findAllByClientId("client123")).thenReturn(transactionList);

        List<TransactionDTO> result = accountService.getLastTransactions("client123");

        verify(transactionRepository, times(1)).findAllByClientId("client123");
        verify(transactionMapper, times(1)).toDTO(transaction);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Testing: getLastTransactions When:no transactions found On: Success")
    void getLastTransactions_NoTransactionsFound() {
        when(transactionRepository.findAllByClientId("client123")).thenReturn(Collections.emptyList());

        List<TransactionDTO> result = accountService.getLastTransactions("client123");

        verify(transactionRepository, times(1)).findAllByClientId("client123");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    @DisplayName("Testing: getLastTransactions On:Failure")
    void getLastTransactions_ExceptionThrown() {
        when(transactionRepository.findAllByClientId("client123")).thenThrow(new RuntimeException("Error de base de datos"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.getLastTransactions("client123");
        });

        assertEquals("Ocurrió un error consultando las transacciones", exception.getMessage());
        verify(transactionRepository, times(1)).findAllByClientId("client123");
        verify(transactionMapper, times(0)).toDTO(any(Transaction.class));
    }


    @Test
    void createSubscription_Success() {

        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.of(fund));

        TransactionResponseDTO response = accountService.createSubscription("fundABC", "client123", 1000.0);

        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");
        verify(clientRepository, times(1)).save(client);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("La vinculación con el fondo Fondo ABC ha sido exitosa", response.getMessage());
        assertEquals(4000.0, client.getBalance()); // 5000 - 1000
        assertEquals(1, client.getSubscriptions().size());
    }

    @Test
    void createSubscription_ClientNotFound() {
        when(clientRepository.findById("client123")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.createSubscription("fundABC", "client123", 1000.0);
        });

        assertEquals("No existe este cliente", exception.getMessage());
        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(0)).findById(anyString());
        verify(clientRepository, times(0)).save(any(Client.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    void createSubscription_FundNotFound() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.createSubscription("fundABC", "client123", 1000.0);
        });

        assertEquals("No existe este fondo", exception.getMessage());
        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");
    }

    @Test
    void createSubscription_AlreadySubscribed() {
        Subscription subscription1 = Subscription.builder().fundId("fundABC").build();
        client.getSubscriptions().add(subscription1); //adds fund to validate it exists
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.of(fund));

        TransactionResponseDTO response = accountService.createSubscription("fundABC", "client123", 1000.0);

        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");

        assertNotNull(response);
        assertEquals("98", response.getCode());
        assertEquals("El cliente ya está suscrito al fondo: Fondo ABC", response.getMessage());
        assertEquals(5000.0, client.getBalance()); // Balance does not change
        assertEquals(1, client.getSubscriptions().size());
    }

    @Test
    void createSubscription_InsufficientBalance() {
        client.setBalance(800.0); // less than minimum deposit

        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.of(fund));

        TransactionResponseDTO response = accountService.createSubscription("fundABC", "client123", 800.0);

        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");

        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("No tiene saldo disponible para vincularse al fondo Fondo ABC", response.getMessage());
        assertEquals(800.0, client.getBalance()); // Balance does not change
        assertEquals(0, client.getSubscriptions().size());
    }

    @Test
    void createSubscription_ExceptionThrown() {
        when(clientRepository.findById("client123")).thenThrow(new RuntimeException());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.createSubscription("fundABC", "client123", 1000.0);
        });

        assertEquals("Ocurrió un error abriendo la suscripción!", exception.getMessage());
        verify(clientRepository, times(1)).findById("client123");
    }

    @Test
    void cancelSubscription_Success() {
        subscription.setBalance(1000.0);
        client.getSubscriptions().add(subscription);

        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.of(fund));

        TransactionResponseDTO response = accountService.cancelSubscription("client123", "fundABC");

        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");
        verify(clientRepository, times(1)).save(client);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Cancelación exitosa al fondo", response.getMessage());
        assertEquals(6000.0, client.getBalance()); // 5000 + 1000
        assertEquals(0, client.getSubscriptions().size());
    }

    @Test
    void cancelSubscription_ClientNotFound() {
        when(clientRepository.findById("client123")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.cancelSubscription("client123", "fundABC");
        });

        assertEquals("No existe el usuario", exception.getMessage());
        verify(clientRepository, times(1)).findById("client123");
    }

    @Test
    void cancelSubscription_FundNotFound() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.cancelSubscription("client123", "fundABC");
        });

        assertEquals("No existe este fondo", exception.getMessage());
        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");
    }

    @Test
    void cancelSubscription_NotSubscribed() {
        when(clientRepository.findById("client123")).thenReturn(Optional.of(client));
        when(fundRepository.findById("fundABC")).thenReturn(Optional.of(fund));

        TransactionResponseDTO response = accountService.cancelSubscription("client123", "fundABC");

        verify(clientRepository, times(1)).findById("client123");
        verify(fundRepository, times(1)).findById("fundABC");
        verify(clientRepository, times(0)).save(any(Client.class));
        verify(transactionRepository, times(0)).save(any(Transaction.class));

        assertNotNull(response);
        assertEquals("98", response.getCode());
        assertEquals("El cliente no está suscrito a ese fondo", response.getMessage());
        assertEquals(5000.0, client.getBalance()); // Balance no cambia
        assertEquals(0, client.getSubscriptions().size());
    }

    @Test
    void cancelSubscription_ExceptionThrown() {
        subscription.setBalance(1000.0);
        client.getSubscriptions().add(subscription);

        when(clientRepository.findById("client123")).thenThrow(new RuntimeException());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.cancelSubscription("client123", "fundABC");
        });

        assertEquals("Ocurrió un error cancelando la suscripción!", exception.getMessage());
        verify(clientRepository, times(1)).findById("client123");
    }






}
