package com.ms.email.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ms.email.enums.StatusEmail;
import com.ms.email.models.EmailModel;
import com.ms.email.repositories.EmailRepository;

import jakarta.transaction.Transactional;

@Service
public class EmailService {

    final EmailRepository emailRepository;
    final JavaMailSender javaMailSender;

    public EmailService(EmailRepository emailRepository, JavaMailSender javaMailSender) {
        this.emailRepository = emailRepository;
        this.javaMailSender = javaMailSender;
    }


    @Value("${spring.mail.username}")
    private String emailFrom;

    @Transactional
    public EmailModel sendEmail(EmailModel emailModel) {
        try {
            emailModel.setSendDate(LocalDateTime.now());
            emailModel.setEmailFrom(emailFrom);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(emailModel.getEmailTo());
            mailMessage.setSubject(emailModel.getSubject());
            mailMessage.setText(emailModel.getText());
            

            javaMailSender.send(mailMessage);

            emailModel.setStatus(StatusEmail.SENT);

            return emailModel;
        } catch (MailException e) {
            emailModel.setStatus(StatusEmail.ERROR);
        }
        finally {
            return emailRepository.save(emailModel);
        }
    }

}
