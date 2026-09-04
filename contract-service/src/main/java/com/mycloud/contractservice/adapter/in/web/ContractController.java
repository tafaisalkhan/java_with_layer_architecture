package com.mycloud.contractservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.contractservice.application.port.in.AddCustomerCreditCommand;
import com.mycloud.contractservice.application.port.in.ContractResult;
import com.mycloud.contractservice.application.port.in.CreateContractCommand;
import com.mycloud.contractservice.application.port.in.CustomerCreditBalanceResult;
import com.mycloud.contractservice.application.port.in.CustomerCreditResult;
import com.mycloud.contractservice.application.port.in.QuotaCommand;
import com.mycloud.contractservice.application.port.in.usecase.AddCustomerCreditUseCase;
import com.mycloud.contractservice.application.port.in.usecase.CommitQuotaUseCase;
import com.mycloud.contractservice.application.port.in.usecase.CreateContractUseCase;
import com.mycloud.contractservice.application.port.in.usecase.GetContractUseCase;
import com.mycloud.contractservice.application.port.in.usecase.GetActiveCustomerContractUseCase;
import com.mycloud.contractservice.application.port.in.usecase.GetCustomerCreditBalanceUseCase;
import com.mycloud.contractservice.application.port.in.usecase.ReleaseQuotaUseCase;
import com.mycloud.contractservice.application.port.in.usecase.ReserveQuotaUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
public class ContractController {
    private final CreateContractUseCase createContractUseCase;
    private final GetContractUseCase getContractUseCase;
    private final GetActiveCustomerContractUseCase getActiveCustomerContractUseCase;
    private final ReserveQuotaUseCase reserveQuotaUseCase;
    private final CommitQuotaUseCase commitQuotaUseCase;
    private final ReleaseQuotaUseCase releaseQuotaUseCase;
    private final AddCustomerCreditUseCase addCustomerCreditUseCase;
    private final GetCustomerCreditBalanceUseCase getCustomerCreditBalanceUseCase;

    public ContractController(
        CreateContractUseCase createContractUseCase,
        GetContractUseCase getContractUseCase,
        GetActiveCustomerContractUseCase getActiveCustomerContractUseCase,
        ReserveQuotaUseCase reserveQuotaUseCase,
        CommitQuotaUseCase commitQuotaUseCase,
        ReleaseQuotaUseCase releaseQuotaUseCase,
        AddCustomerCreditUseCase addCustomerCreditUseCase,
        GetCustomerCreditBalanceUseCase getCustomerCreditBalanceUseCase
    ) {
        this.createContractUseCase = createContractUseCase;
        this.getContractUseCase = getContractUseCase;
        this.getActiveCustomerContractUseCase = getActiveCustomerContractUseCase;
        this.reserveQuotaUseCase = reserveQuotaUseCase;
        this.commitQuotaUseCase = commitQuotaUseCase;
        this.releaseQuotaUseCase = releaseQuotaUseCase;
        this.addCustomerCreditUseCase = addCustomerCreditUseCase;
        this.getCustomerCreditBalanceUseCase = getCustomerCreditBalanceUseCase;
    }

    @PostMapping
    public ResponseEntity<ContractResult> createContract(@Valid @RequestBody CreateContractCommand command) {
        ContractResult result = createContractUseCase.createContract(command);
        return ResponseEntity.created(URI.create("/contracts/" + result.contractId())).body(result);
    }

    @GetMapping("/{contractId}")
    public ContractResult getContract(@PathVariable("contractId") UUID contractId) {
        return getContractUseCase.getContract(new GetByIdQuery(contractId));
    }

    @GetMapping("/customers/{customerId}/active")
    public ContractResult getActiveContract(@PathVariable("customerId") UUID customerId) {
        return getActiveCustomerContractUseCase.getActiveContract(customerId);
    }

    @PostMapping("/quota/reserve")
    public ContractResult reserveQuota(@Valid @RequestBody QuotaCommand command) {
        return reserveQuotaUseCase.reserveQuota(command);
    }

    @PostMapping("/quota/commit")
    public ContractResult commitQuota(@Valid @RequestBody QuotaCommand command) {
        return commitQuotaUseCase.commitQuota(command);
    }

    @PostMapping("/quota/release")
    public ContractResult releaseQuota(@Valid @RequestBody QuotaCommand command) {
        return releaseQuotaUseCase.releaseQuota(command);
    }

    @PostMapping("/credits")
    public CustomerCreditResult addCredit(@Valid @RequestBody AddCustomerCreditCommand command) {
        return addCustomerCreditUseCase.addCredit(command);
    }

    @GetMapping("/customers/{customerId}/credits")
    public CustomerCreditBalanceResult getCreditBalance(@PathVariable("customerId") UUID customerId) {
        return getCustomerCreditBalanceUseCase.getCreditBalance(customerId);
    }
}
