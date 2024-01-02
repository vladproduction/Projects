package com.vladproduction.bankTransactions.caseD_usingInterface;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;



public class JsonParserImpl implements Parser {

    private ObjectMapper mapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Override
    public BankTransaction lineFromFile(String line) {
        String[] columns = line.split(",");

        LocalDate date = LocalDate.parse(columns[0], DATE_PATTERN);
        double amount = Double.parseDouble(columns[1]);
        String description = columns[2];

        return new BankTransaction(date, amount, description);
    }

    @Override
    public List<BankTransaction> loadFromFile(File file) {
        try {

            List<BankTransaction> bankTransactions =
                    Collections.singletonList(mapper.readValue(file, BankTransaction.class));
            return bankTransactions;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
