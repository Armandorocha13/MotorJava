package com.motorjava.service.ihs;

import com.motorjava.config.Config;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

public class OutlookService {

    private Consumer<String> logger;

    public OutlookService(Consumer<String> logger) {
        this.logger = logger;
    }

    public OutlookService() {
        this(System.out::println);
    }

    private void log(String msg) {
        if (logger != null)
            logger.accept(msg);
    }

    /**
     * Conecta ao Outlook via IMAP e baixa anexos de e-mails não lidos.
     * 
     * @param subjectFilter Filtro opcional por assunto (null para ignorar)
     * @return Quantidade de arquivos baixados
     */
    public int baixarAnexosOutlook(String subjectFilter) throws Exception {
        log("Conectando ao servidor IMAP: " + Config.MAIL_HOST);
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", Config.MAIL_HOST);
        properties.put("mail.imaps.port", Config.MAIL_PORT);
        properties.put("mail.imaps.ssl.enable", "true");

        Session emailSession = Session.getInstance(properties);
        Store store = emailSession.getStore("imaps");

        // Em produção, deve-se usar OAuth2 para Office 365, mas para este esqueleto
        // usamos login/senha (que pode requerer 'App Password')
        store.connect(Config.MAIL_USER, Config.MAIL_PASS);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        // Busca e-mails não lidos
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        int arquivosBaixados = 0;

        for (Message message : messages) {
            String subject = message.getSubject();

            if (subjectFilter != null && !subject.toLowerCase().contains(subjectFilter.toLowerCase())) {
                continue;
            }

            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                        String fileName = bodyPart.getFileName();
                        salvarAnexo(bodyPart, fileName);
                        arquivosBaixados++;
                    }
                }
            }

            // Marcar como lido após processar
            message.setFlag(Flags.Flag.SEEN, true);
        }

        inbox.close(false);
        store.close();

        return arquivosBaixados;
    }

    private void salvarAnexo(BodyPart bodyPart, String fileName) throws MessagingException, IOException {
        File folder = new File(Config.PATH_OUTLOOK);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String nomePadronizado = padronizarNome(fileName);
        File file = new File(folder, nomePadronizado);
        ((MimeBodyPart) bodyPart).saveFile(file);
        log("Anexo salvo e padronizado: " + fileName + " -> " + nomePadronizado);
    }

    private String padronizarNome(String nomeOriginal) {
        if (nomeOriginal == null)
            return "arquivo_sem_nome";

        int lastDot = nomeOriginal.lastIndexOf(".");
        String nome = (lastDot != -1) ? nomeOriginal.substring(0, lastDot) : nomeOriginal;
        String extensao = (lastDot != -1) ? nomeOriginal.substring(lastDot) : "";

        nome = nome.toLowerCase()
                .replaceAll("\\d", "")
                .replaceAll("[\\(\\)\\[\\]\\-_]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return nome + extensao.toLowerCase();
    }
}
