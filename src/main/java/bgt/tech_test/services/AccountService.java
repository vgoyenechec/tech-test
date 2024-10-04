package bgt.tech_test.services;

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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class AccountService {
    private final String OPENING = "Apertura";
    private final String CANCELLATION = "Cancelación";
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private FundRepository fundRepository;
    @Autowired
    private TransactionMapper transactionMapper;

    public List<TransactionDTO> getLastTransactions(String clientId) {
        try{
        return transactionRepository.findAllByClientId(clientId)
                .stream().map(transactionMapper::toDTO)
                .toList();
        }
        catch (Exception e){
            throw new BusinessException("Ocurrió un error consultando las transacciones");
        }
    }

    public TransactionResponseDTO createSubscription(String fundId, String clientId, double depositAmount) {
        try {
            Client client = clientRepository.findById(clientId).orElseThrow(() -> new BusinessException("No existe este cliente"));
            Fund fund = fundRepository.findById(fundId).orElseThrow(() -> new BusinessException("No existe este fondo"));
            if (client.getBalance() >= fund.getMinimumSubscriptionAmount() && depositAmount >= fund.getMinimumSubscriptionAmount()) {
                if (!subscriptionExists(client, fundId)) {
                    client.setBalance(client.getBalance() - depositAmount);

                    Subscription subscription = Subscription.builder().fundId(fundId).balance(depositAmount).build();

                    client.getSubscriptions().add(subscription);
                    clientRepository.save(client);

                    createTransaction(fund, depositAmount, OPENING, clientId);
                    log.info("Apertura Exitosa");
                    return TransactionResponseDTO.builder().code("00").message(String.format("La vinculación con el fondo %s ha sido exitosa", fund.getName())).build();
                } else {
                    return TransactionResponseDTO.builder().code("98").message(String.format("El cliente ya está suscrito al fondo: %s", fund.getName())).build();
                }
            } else {
                return TransactionResponseDTO.builder().code("99").message(String.format("No tiene saldo disponible para vincularse al fondo %s", fund.getName())).build();
            }
        } catch (BusinessException e){
            throw e;
        }
        catch (Exception e) {
            throw new BusinessException("Ocurrió un error abriendo la suscripción!");
        }
    }

    public TransactionResponseDTO cancelSubscription(String clientId, String fundId) {
        try {
            Client client = clientRepository.findById(clientId).orElseThrow(() -> new BusinessException("No existe el usuario"));
            Fund fund = fundRepository.findById(fundId).orElseThrow(() -> new BusinessException("No existe este fondo"));

            List<Subscription> subscriptions = client.getSubscriptions();

            Optional<Subscription> subscriptionOpt = subscriptions.stream().filter(subscription -> subscription.getFundId().equals(fundId)).findFirst();

            if (subscriptionOpt.isEmpty()) {
                return TransactionResponseDTO.builder().code("98").message(("El cliente no está suscrito a ese fondo")).build();
            }
            Subscription subscriptionToRemove = subscriptionOpt.get();
            subscriptions.remove(subscriptionToRemove);
            double amount = subscriptionToRemove.getBalance();

            client.setBalance(client.getBalance() + amount);
            clientRepository.save(client);
            createTransaction(fund, amount, CANCELLATION, clientId);
            log.info("Cancelacion exitosa");

            return TransactionResponseDTO.builder().code("00").message(("Cancelación exitosa al fondo")).build();
            } catch (BusinessException e){
                throw e;
            }catch (Exception e) {
            throw new BusinessException("Ocurrió un error cancelando la suscripción!");
        }
    }

    private boolean subscriptionExists(Client client, String fundId) {
        List<Subscription> subscriptions = client.getSubscriptions();
        return subscriptions.stream().anyMatch(subs -> subs.getFundId().equals(fundId));
    }

    private void createTransaction(Fund fund, double amount, String type, String clientId) {
        try {
            Transaction transaction = Transaction.builder().uid(UUID.randomUUID().toString())
                    .timestamp(new Date())
                    .clientId(clientId)
                    .amount(amount)
                    .fundId(fund.getId())
                    .fundName(fund.getName())
                    .type(type)
                    .notificationSent(true) // TODO
                    .build();
            transactionRepository.save(transaction);
            log.info("Transacción Exitosa");
        } catch (Exception e) {
            throw new BusinessException("Ocurrió un error creando la transacción");
        }
    }
}
