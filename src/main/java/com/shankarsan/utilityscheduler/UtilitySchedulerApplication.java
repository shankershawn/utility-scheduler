package com.shankarsan.utilityscheduler;

import com.shankarsan.utilityscheduler.dto.SeatAvailabilityRequestDto;
import com.shankarsan.utilityscheduler.dto.SeatAvailabilityResponseDto;
import com.shankarsan.utilityscheduler.service.IrctcService;
import com.shankarsan.utilityscheduler.service.impl.IrctcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class UtilitySchedulerApplication {

    @Autowired
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(UtilitySchedulerApplication.class, args);
    }

}
