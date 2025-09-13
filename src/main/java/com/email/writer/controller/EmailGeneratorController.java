package com.email.writer.controller;


import com.email.writer.Entity.EmailRequestEntity;
import com.email.writer.service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin
public class EmailGeneratorController {

    @Autowired
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> genrateEmail(@RequestBody EmailRequestEntity emailRequestEntity){
        String response = emailGeneratorService.generateEmailReply(emailRequestEntity);
        return ResponseEntity.ok(response);
    }
}
