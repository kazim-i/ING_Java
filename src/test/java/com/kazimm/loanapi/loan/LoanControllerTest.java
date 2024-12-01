package com.kazimm.loanapi.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanService loanService; // Mocking LoanService

    @InjectMocks
    private LoanController loanController; // Injecting mocks into LoanController

    @BeforeEach
    void setUp() {
        // Initialize mocks and configure MockMvc with the controller
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
    }

    @Test
    void testPayLoan_Success() throws Exception {
        // Mock response from LoanService
        LoanPaymentResponseDTO mockResponse = new LoanPaymentResponseDTO();
        mockResponse.setInstallmentsPaid(3);
        mockResponse.setTotalAmountSpent(BigDecimal.valueOf(300));
        mockResponse.setLoanPaidCompletely(false);

        when(loanService.payLoan(anyLong(), any(BigDecimal.class)))
                .thenReturn(mockResponse);

        // Perform a POST request to the pay endpoint
        mockMvc.perform(post("/api/loans/1/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 300.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installmentsPaid").value(3))
                .andExpect(jsonPath("$.totalAmountSpent").value(300.00))
                .andExpect(jsonPath("$.loanPaidCompletely").value(false));
    }
}
