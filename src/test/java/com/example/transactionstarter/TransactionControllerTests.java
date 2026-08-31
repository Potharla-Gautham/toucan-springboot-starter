package com.example.transactionstarter;

import com.example.transactionstarter.transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void clearTransactions() {
        transactionRepository.deleteAll();
    }

    @Test
    void createsValidTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson("tx-1", "customer-1", "PENDING")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("tx-1"))
                .andExpect(jsonPath("$.amount").value(125.50))
                .andExpect(jsonPath("$.transactionStatus").value("PENDING"));
    }

    @Test
    void rejectsInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactionId": "tx-1",
                                  "customerId": "customer-1",
                                  "amount": 0,
                                  "currency": "USD",
                                  "transactionType": "PURCHASE",
                                  "transactionStatus": "PENDING"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDuplicateTransactionId() throws Exception {
        createTransaction("tx-1", "customer-1");

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson("tx-1", "customer-2", "PENDING")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Transaction ID already exists: tx-1"));
    }

    @Test
    void getsExistingTransaction() throws Exception {
        createTransaction("tx-1", "customer-1");

        mockMvc.perform(get("/api/transactions/tx-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("customer-1"));
    }

    @Test
    void returnsNotFoundForMissingTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction not found: missing"));
    }

    @Test
    void getsTransactionsForCustomer() throws Exception {
        createTransaction("tx-1", "customer-1");
        createTransaction("tx-2", "customer-1");
        createTransaction("tx-3", "customer-2");

        mockMvc.perform(get("/api/customers/customer-1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("customer-1"))
                .andExpect(jsonPath("$[1].customerId").value("customer-1"));
    }

    @Test
    void updatesPendingTransactionToCompleted() throws Exception {
        createTransaction("tx-1", "customer-1");

        mockMvc.perform(patch("/api/transactions/tx-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionStatus").value("COMPLETED"));
    }

    @Test
    void rejectsTransitionFromTerminalStatus() throws Exception {
        createTransaction("tx-1", "customer-1");
        updateStatus("tx-1", "FAILED");

        mockMvc.perform(patch("/api/transactions/tx-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"PENDING\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid status transition from FAILED to PENDING"));
    }

    @Test
    void rejectsUnsupportedStatus() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson("tx-1", "customer-1", "PROCESSING")))
                .andExpect(status().isBadRequest());
    }

    private void createTransaction(String transactionId, String customerId) throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson(transactionId, customerId, "PENDING")))
                .andExpect(status().isCreated());
    }

    private void updateStatus(String transactionId, String status) throws Exception {
        mockMvc.perform(patch("/api/transactions/" + transactionId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionStatus\":\"" + status + "\"}"))
                .andExpect(status().isOk());
    }

    private String transactionJson(String transactionId, String customerId, String status) {
        return """
                {
                  "transactionId": "%s",
                  "customerId": "%s",
                  "amount": 125.50,
                  "currency": "USD",
                  "transactionType": "PURCHASE",
                  "transactionStatus": "%s"
                }
                """.formatted(transactionId, customerId, status);
    }
}