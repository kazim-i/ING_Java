package com.kazimm.loanapi.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/{customerId}")
    public ResponseEntity<Loan> createLoan(@PathVariable Long customerId, @RequestBody LoanRequestDTO loanRequestDTO) {
        Loan createdLoan = loanService.createLoan(customerId, loanRequestDTO);
        return ResponseEntity.ok(createdLoan);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Loan>> getLoansByCustomerId(@PathVariable Long customerId) {
        List<Loan> loans = loanService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/{loanId}/pay")
    public ResponseEntity<LoanPaymentResponseDTO> payLoan(
            @PathVariable Long loanId,
            @RequestBody LoanPaymentRequestDTO paymentRequest) {
        LoanPaymentResponseDTO response = loanService.payLoan(loanId, paymentRequest.getAmount());
        return ResponseEntity.ok(response);
    }

}
