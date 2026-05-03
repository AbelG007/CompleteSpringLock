package com.authorizationServer.services;

import com.authorizationServer.dto.CustomerCreateRequest;
import com.authorizationServer.dto.CustomerResponse;
import com.authorizationServer.exception.CustomerAlreadyExistsException;
import com.authorizationServer.exception.CustomerNotFoundException;
import com.authorizationServer.exception.InvalidCustomerStateException;
import com.authorizationServer.model.Authority;
import com.authorizationServer.model.Customer;
import com.authorizationServer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CustomerManagementService {

    private static final String DEFAULT_AUTHORITY = "ROLE_USER";

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        validateCreateRequest(request);

        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already exists for email: " + request.email());
        }

        Customer customer = new Customer();
        customer.setName(request.name().trim());
        customer.setEmail(request.email().trim().toLowerCase());
        customer.setMobileNumber(trimToNull(request.mobileNumber()));
        customer.setPwd(passwordEncoder.encode(request.password()));
        customer.setRole(resolveRole(request));
        customer.setCreateDt(Date.valueOf(LocalDate.now()));
        customer.setAuthorities(buildAuthorities(customer, resolveAuthorities(request)));

        Customer savedCustomer = customerRepository.save(customer);
        return toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidCustomerStateException("Email must not be blank");
        }

        Customer customer = customerRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for email: " + email));
        return toResponse(customer);
    }

    private void validateCreateRequest(CustomerCreateRequest request) {
        if (request == null) {
            throw new InvalidCustomerStateException("Customer request must not be null");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new InvalidCustomerStateException("Customer name must not be blank");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new InvalidCustomerStateException("Customer email must not be blank");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new InvalidCustomerStateException("Customer password must not be blank");
        }
    }

    private String resolveRole(CustomerCreateRequest request) {
        if (request.role() != null && !request.role().isBlank()) {
            return request.role().trim();
        }
        return DEFAULT_AUTHORITY;
    }

    private List<String> resolveAuthorities(CustomerCreateRequest request) {
        if (request.authorities() != null && !request.authorities().isEmpty()) {
            return request.authorities().stream()
                    .filter(authority -> authority != null && !authority.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return List.of(resolveRole(request));
    }

    private Set<Authority> buildAuthorities(Customer customer, List<String> authorityNames) {
        Set<Authority> authorities = new LinkedHashSet<>();
        for (String authorityName : authorityNames) {
            Authority authority = new Authority();
            authority.setName(authorityName);
            authority.setCustomer(customer);
            authorities.add(authority);
        }
        return authorities;
    }

    private CustomerResponse toResponse(Customer customer) {
        List<String> authorityNames = customer.getAuthorities() == null ? List.of() : customer.getAuthorities().stream()
                .map(Authority::getName)
                .toList();

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getMobileNumber(),
                customer.getRole(),
                authorityNames
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
