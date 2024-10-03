package bgt.tech_test.controllers;

import bgt.tech_test.domain.Fund;
import bgt.tech_test.repositories.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private FundRepository fundRepository;


    @GetMapping("/funds")
    private List<Fund> getFunds(){
        return fundRepository.findAll();
    }
}
