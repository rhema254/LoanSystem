package com.LoanManagementApp.LoansApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.LoanManagementApp.LoansApp.Models.Role;
import com.LoanManagementApp.LoansApp.Repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoansAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoansAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			if (!roleRepository.findByName("ROLE_SUPER_ADMIN").isPresent()) {
				Role superadminRole = new Role();
				superadminRole.setName("ROLE_SUPER_ADMIN");
				roleRepository.save(superadminRole);
			if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
				Role adminRole = new Role();
				adminRole.setName("ROLE_ADMIN");
				roleRepository.save(adminRole);

			if (!roleRepository.findByName("ROLE_LOAN_OFFICER").isPresent()) {
				Role officerRole = new Role();
				officerRole.setName("ROLE_LOAN_OFFICER");
				roleRepository.save(officerRole);
			}
			}
			}
		};
	}

}
