package com.email.writer.Entity;


import lombok.Data;

@Data
public class EmailRequestEntity {
    private String emailContent;
    private String tone;
}
