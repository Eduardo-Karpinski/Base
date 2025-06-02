package br.com.base.services;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Async
	public void sendEmail(String to, String subject, Map<String, Object> variables) {
	    try {
	        Context context = new Context();
	        context.setVariables(variables);
	        String body = templateEngine.process("email", context);

	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(body, true);

	        mailSender.send(message);
	        log.info("Email successfully sent to {}", to);
	    } catch (Exception ex) {
	        log.error("Failed to send email to {}: {}", to, ex.getMessage(), ex);
	        throw new RuntimeException("Failed to send email", ex);
	    }
	}

}