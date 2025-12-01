package com.laboratorio.services.impl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppServiceImpl {

    private final String ACCOUNT_SID = "ACa1b5df418d7f45020f912511d612e645";
    private final String AUTH_TOKEN = "359382055db3a891eb9d943c76542c87";
    private final String FROM_NUMBER = "+14155238886"; // Número de Twilio

    public WhatsAppServiceImpl() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

     public void enviarMensaje(String numeroDestino, String mensaje) {
        Message.creator(
                new PhoneNumber("whatsapp:" + numeroDestino), // DESTINO
                new PhoneNumber("whatsapp:" + FROM_NUMBER),    // ORIGEN
                mensaje
        ).create();
    }
}
