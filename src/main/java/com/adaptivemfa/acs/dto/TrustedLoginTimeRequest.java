package com.adaptivemfa.acs.dto;

public record TrustedLoginTimeRequest (
        String username,
        int startHour,
        int endHour
){
}
