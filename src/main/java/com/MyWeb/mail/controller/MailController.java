package com.MyWeb.mail.controller;

import com.MyWeb.mail.service.MailService;
import org.springframework.stereotype.Controller;

@Controller
public class MailController {
    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }


}
