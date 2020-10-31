package com.labinf.libraryapi;

import com.labinf.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner runner(){

		return args -> {
			List<String> emails = Arrays.asList("c4f9bacab3-825f64@inbox.mailtrap.io");
			emailService.sendMails("Testando serviços de emails.", emails);
			System.out.println("Emails enviados");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
