package com.cafateria.mailer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cafateria.auth.AuthHelper;
import com.cafateria.auth.Interfaces;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

import java.io.Writer;


public class Async extends AsyncTask<Void, Integer, Boolean> {

    private static final String TAG = "Async";
    String from, password, to, subj, msg;
    Context c;
    private Interfaces.callback1 callback1;

    public Async(Context c, String from, String password, String to, String subject, String text, Interfaces.callback1 callback1) {
        this.from = from;
        this.password = password;
        this.to = to;
        this.subj = subject;
        this.msg = text;
        this.c = c;
        this.callback1 = callback1;
    }

    @Override
    protected void onPreExecute() {
        ((Activity) c).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AuthHelper.startDialog(c);
            }
        });
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return send(from, password, to, subj, msg);
    }

    boolean send(String from, String password, String to, String subject, String text) {
        try {
            sendEmail(from, password, to, subject, text);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        AuthHelper.stopDialog();
        callback1.onComplete(aBoolean);
        super.onPostExecute(aBoolean);
    }

    public void sendEmail(String from, String password, String to, String subject, String text) throws Exception {
        Log.e(TAG,"sendEmail:"+to);
        String hostname = "smtp.gmail.com";
        int port = 587;

        AuthenticatingSMTPClient client = new AuthenticatingSMTPClient();
        try {
            // optionally set a timeout to have a faster feedback on errors
            client.setDefaultTimeout(60 * 1000);
            //client.setSoTimeout(60 * 1000);
            // you connect to the SMTP server
            client.connect(hostname, port);
            // you say ehlo  and you specify the host you are connecting from, could be anything
            client.ehlo("localhost");
            // if your host accepts STARTTLS, we're good everything will be encrypted, otherwise we're done here
            if (client.execTLS()) {

                client.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, from, password);
                checkReply(client);

                client.setSender(from);
                checkReply(client);

                client.addRecipient(to);
                checkReply(client);

                Writer writer = client.sendMessageData();

                if (writer != null) {
                    SimpleSMTPHeader header = new SimpleSMTPHeader(from, to, subject);
                    writer.write(header.toString());
                    writer.write(text);
                    writer.close();
                    if (!client.completePendingCommand()) {// failure
                        throw new Exception("Failure to send the email " + client.getReply() + client.getReplyString());
                    }
                } else {
                    throw new Exception("Failure to send the email " + client.getReply() + client.getReplyString());
                }
            } else {
                throw new Exception("STARTTLS was not accepted " + client.getReply() + client.getReplyString());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            client.logout();
            client.disconnect();
        }
    }

    private static void checkReply(SMTPClient sc) throws Exception {
        if (SMTPReply.isNegativeTransient(sc.getReplyCode())) {
            throw new Exception("Transient SMTP error " + sc.getReply() + sc.getReplyString());
        } else if (SMTPReply.isNegativePermanent(sc.getReplyCode())) {
            throw new Exception("Permanent SMTP error " + sc.getReply() + sc.getReplyString());
        }
    }
}
