package com.app.banking.service.impl;

import com.app.banking.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
}
