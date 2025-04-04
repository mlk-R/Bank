package ru.malik.bank.StartBank;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.malik.bank.StartBank.dto.UserAccountDto;
import ru.malik.bank.StartBank.entity.UserAccountView;

@SpringBootApplication
@EnableScheduling
public class StartBankApplication {
	public static void main(String[] args) {
		SpringApplication.run(StartBankApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public ModelMapper modelMapperUser() {
		ModelMapper modelMapperUser = new ModelMapper();

		modelMapperUser.createTypeMap(UserAccountView.class, UserAccountDto.class)
				.addMappings(mapper -> {
					mapper.map(src -> src.getId().getUserId(), UserAccountDto::setUserId);
					mapper.map(src -> src.getId().getAccountId(), UserAccountDto::setAccountId);
				});
		return modelMapperUser;
	}
}
