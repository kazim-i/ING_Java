package com.kazimm.loanapi.loaninstallment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
public class LoanInstallmentController {

    @Autowired
    private LoanInstallmentService loanInstallmentService;

    @PostMapping("/{loanId}")
    public ResponseEntity<LoanInstallment> createLoanInstallment(@PathVariable Long loanId, @RequestBody LoanInstallment installment) {
        LoanInstallment createdInstallment = loanInstallmentService.createLoanInstallment(loanId, installment);
        return ResponseEntity.ok(createdInstallment);
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanInstallment>> getInstallmentsByLoanId(@PathVariable Long loanId) {
        List<LoanInstallment> installments = loanInstallmentService.getInstallmentsByLoanId(loanId);
        return ResponseEntity.ok(installments);
    }
}
