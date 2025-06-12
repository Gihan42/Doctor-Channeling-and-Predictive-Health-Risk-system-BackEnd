package com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend;

import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.entity.UserRoles;
import com.Doctor.Channeling.and.Predictive.Health.Risk.System.backend.repo.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	@Autowired
	UserRolesRepo userRolesRepo;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		System.out.println("hello world");
	}

	@Override
	public void run(String... args) throws Exception {
		List<UserRoles> all = userRolesRepo.findAll();
		if (all.isEmpty()){
			UserRoles userRolesOne = new UserRoles();
			userRolesOne.setRole("Admin");
			userRolesOne.setStatus("1");
			userRolesRepo.save(userRolesOne);
			UserRoles userRolesTwo = new UserRoles();
			userRolesTwo.setRole("Patient");
			userRolesTwo.setStatus("1");
			userRolesRepo.save(userRolesTwo);
			UserRoles userRolesThree = new UserRoles();
			userRolesThree.setRole("Doctor");
			userRolesThree.setStatus("1");
			userRolesRepo.save(userRolesThree);
		}
	}
}
