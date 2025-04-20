package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Enums.PaymentFrequency;
import com.LoanManagementApp.LoansApp.Enums.RepaymentScheduleStatus;
import com.LoanManagementApp.LoansApp.Models.*;
import com.LoanManagementApp.LoansApp.Repositories.*;
import com.LoanManagementApp.LoansApp.Requests.CreateLoanRequestDTO;
import com.LoanManagementApp.LoansApp.Responses.CustomerDTO;
import com.LoanManagementApp.LoansApp.Responses.LoanDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    @Autowired
    private final LoanRepository loanRepository;
    private final LoanProductRepository loanProductRepository;
    private final CustomerRepository customerRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final UserRepository userRepository;



    @PreAuthorize("hasRole('LOAN_OFFICER')")
    @Transactional
    public Loan createLoan(CreateLoanRequestDTO loanRequestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.isCanApproveLoans()) {
            throw new IllegalArgumentException("User cannot approve loans");
        }

        Long customerId = loanRequestDTO.getCustomerId();
        Long productId = loanRequestDTO.getProductId();

        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Loan product not found"));

        PaymentFrequency paymentFrequency;
        try {
            paymentFrequency = loanRequestDTO.getFrequency();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment frequency: " + loanRequestDTO.getFrequency());
        }

        if (!product.getAllowedFrequencies().contains(paymentFrequency)) {
            throw new IllegalArgumentException("Frequency not allowed for this product");
        }

        if (loanRequestDTO.getPrincipalAmount() <= 0) {
            throw new IllegalArgumentException("Principal amount must be positive");
        }
        if (loanRequestDTO.getInterestRate() != null && loanRequestDTO.getInterestRate() < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (loanRequestDTO.getRepaymentPeriod() < 1) {
            throw new IllegalArgumentException("Repayment period must be at least 1");
        }

        Loan loan = new Loan();
        loan.setStartDate(loanRequestDTO.getStartDate() != null ? loanRequestDTO.getStartDate() : LocalDate.now());
        loan.setPrincipalAmount(loanRequestDTO.getPrincipalAmount());
        loan.setInterestRate(loanRequestDTO.getInterestRate() != null ? loanRequestDTO.getInterestRate() : product.getDefaultInterestRate());
        loan.setRepaymentPeriod(loanRequestDTO.getRepaymentPeriod());
        loan.setFrequency(loanRequestDTO.getFrequency());
        loan.setOutstandingBalance(loanRequestDTO.getPrincipalAmount());
        loan.setTotalPaid(0.0);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setCustomer(customer);
        loan.setProduct(product);
        loan.setLoanOfficer(user);

        // Corrected line: Save the Loan entity
        Loan savedLoan = loanRepository.save(loan);
        generateRepaymentSchedule(savedLoan);
        return savedLoan;
    }

    @Transactional
    public LoanProduct createLoanProduct(String name, Double defaultInterestRate, Set<PaymentFrequency> allowedFrequencies) {
        if (loanProductRepository.existsByName(name)) {
            throw new IllegalArgumentException("Loan product name already exists");
        }
        LoanProduct product = new LoanProduct();
        product.setName(name);
        product.setDefaultInterestRate(defaultInterestRate);
        product.setAllowedFrequencies(allowedFrequencies);
        return loanProductRepository.save(product);
    }

    public List<LoanProduct> getAllLoanProducts() {
        return loanProductRepository.findAll();
    }

    public List<LoanDTO> getAllLoans(){
        return loanRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<Loan> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<RepaymentSchedule> getRepaymentSchedule(Long loanId) {
        return repaymentScheduleRepository.findByLoanId(loanId);
    }

    private void generateRepaymentSchedule(Loan loan) {
        BigDecimal principal = BigDecimal.valueOf(loan.getPrincipalAmount());
        BigDecimal annualRate = BigDecimal.valueOf(loan.getInterestRate()).divide(BigDecimal.valueOf(100));
        int periods = loan.getRepaymentPeriod();
        PaymentFrequency frequency = loan.getFrequency();
        LocalDate currentDate = loan.getStartDate();

        int periodsPerYear;
        switch (frequency) {
            case WEEKLY:
                periodsPerYear = 52;
                break;
            case MONTHLY:
                periodsPerYear = 12;
                break;
            case YEARLY:
                periodsPerYear = 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid frequency");
        }

        BigDecimal periodRate = annualRate.divide(BigDecimal.valueOf(periodsPerYear), 10, RoundingMode.HALF_UP);
        BigDecimal paymentAmount = calculateAmortizedPayment(principal, periodRate, periods);

        BigDecimal remainingBalance = principal;
        for (int i = 1; i <= periods; i++) {
            BigDecimal interestAmount = remainingBalance.multiply(periodRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalAmount = paymentAmount.subtract(interestAmount).setScale(2, RoundingMode.HALF_UP);
            remainingBalance = remainingBalance.subtract(principalAmount).setScale(2, RoundingMode.HALF_UP);

            if (i == periods) {
                principalAmount = principalAmount.add(remainingBalance).setScale(2, RoundingMode.HALF_UP);
                paymentAmount = principalAmount.add(interestAmount).setScale(2, RoundingMode.HALF_UP);
                remainingBalance = BigDecimal.ZERO;
            }

            RepaymentScheduleStatus initialPayment  = RepaymentScheduleStatus.WAITING;

            RepaymentSchedule schedule = new RepaymentSchedule();
            schedule.setLoan(loan);
            schedule.setPaymentNumber(i);
            schedule.setPaymentDate(currentDate);
            schedule.setPaymentAmount(paymentAmount.doubleValue());
            schedule.setPrincipalAmount(principalAmount.doubleValue());
            schedule.setInterestAmount(interestAmount.doubleValue());
            schedule.setRemainingBalance(remainingBalance.doubleValue());
            schedule.setStatus(initialPayment);

            repaymentScheduleRepository.save(schedule);

            currentDate = switch (frequency) {
                case WEEKLY -> currentDate.plusWeeks(1);
                case MONTHLY -> currentDate.plusMonths(1);
                case YEARLY -> currentDate.plusYears(1);
            };
        }
    }

    private BigDecimal calculateAmortizedPayment(BigDecimal principal, BigDecimal periodRate, int periods) {
        if (periodRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusRate = BigDecimal.ONE.add(periodRate);
        BigDecimal ratePowerN = onePlusRate.pow(periods);
        BigDecimal numerator = principal.multiply(periodRate).multiply(ratePowerN);
        BigDecimal denominator = ratePowerN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private CustomerDTO convertToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setFirstName(customer.getFirstName());
        customerDTO.setLastName(customer.getLastName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setIdNumber(customer.getIdNumber());
        return customerDTO;
    }
    public LoanDTO convertToDTO(Loan loan) {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(loan.getId());
        loanDTO.setPrincipalAmount(loan.getPrincipalAmount());
        loanDTO.setInterestRate(loan.getInterestRate());
        loanDTO.setRepaymentPeriod(loan.getRepaymentPeriod());
        loanDTO.setFrequency(loan.getFrequency().name());
        loanDTO.setStartDate(loan.getStartDate());
        loanDTO.setCustomer(convertToCustomerDTO(loan.getCustomer()));
        loanDTO.setProductId(loan.getProduct().getId()); // Adjust based on LoanProduct
        return loanDTO;
    }
}