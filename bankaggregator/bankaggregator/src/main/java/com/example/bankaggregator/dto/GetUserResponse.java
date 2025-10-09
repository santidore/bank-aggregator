package com.example.bankaggregator.dto;

import java.util.List;
import java.util.UUID;

public record GetUserResponse(
        UUID id,
        String name,
        String surname,
        String email,
        String nationality,
        List<BankAccountSummaryResponse> accounts
) { }
