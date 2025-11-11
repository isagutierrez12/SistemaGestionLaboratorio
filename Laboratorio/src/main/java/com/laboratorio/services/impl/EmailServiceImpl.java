/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 *
 * @author soportetecnico
 */
@Service
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoCita(String destinatario, String asunto, String contenido) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, true); // true para HTML

            mailSender.send(mensaje);

            System.out.println("Correo enviado a " + destinatario);
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }
}
