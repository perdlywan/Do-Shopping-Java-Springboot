package com.example.do_shopping.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimezoneConfig {

    @PostConstruct
    public void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
    }
}
