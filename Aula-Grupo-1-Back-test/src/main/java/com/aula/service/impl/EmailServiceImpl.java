package com.aula.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl  implements MailSender {

		@Autowired
		private JavaMailSender emailSender;
		
		 public void sendSimpleMessage(String to, String subject, String text) {
			        SimpleMailMessage message = new SimpleMailMessage(); 
			        message.setFrom("virtualclassroomgrupoalejandro@gmail.com");
			        message.setTo(to); 
			        message.setSubject(subject); 
			        message.setText(text);
			        emailSender.send(message);
			    }

		@Override
		public void send(SimpleMailMessage... simpleMessages) throws MailException {
			// TODO document why this method is empty
		}
}
