package com.lsm.aomp.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SshUtil {

    public boolean testConnection(String host, int port, String user, String password, String key, String keyPassphrase, int timeout) {
        Session session = null;
        try {
            JSch jsch = new JSch();
            if (key != null && !key.isEmpty()) {
                if (keyPassphrase != null && !keyPassphrase.isEmpty()) {
                    jsch.addIdentity("key", key.getBytes(), null, keyPassphrase.getBytes());
                } else {
                    jsch.addIdentity("key", key.getBytes(), null, null);
                }
            }
            session = jsch.getSession(user, host, port);
            if (password != null && !password.isEmpty()) {
                session.setPassword(password);
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(timeout);
            session.connect(timeout);
            return true;
        } catch (Exception e) {
            log.warn("SSH connection test failed for {}:{} - {}", host, port, e.getMessage());
            return false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public String executeCommand(String host, int port, String user, String password, String key, String keyPassphrase, String command, int timeout) {
        Session session = null;
        ChannelExec channel = null;
        try {
            JSch jsch = new JSch();
            if (key != null && !key.isEmpty()) {
                if (keyPassphrase != null && !keyPassphrase.isEmpty()) {
                    jsch.addIdentity("key", key.getBytes(), null, keyPassphrase.getBytes());
                } else {
                    jsch.addIdentity("key", key.getBytes(), null, null);
                }
            }
            session = jsch.getSession(user, host, port);
            if (password != null && !password.isEmpty()) {
                session.setPassword(password);
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(timeout);
            session.connect(timeout);

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect(timeout);

            java.io.InputStream in = channel.getInputStream();
            java.io.InputStream err = channel.getErrStream();
            String stdout = new String(in.readAllBytes());
            String stderr = new String(err.readAllBytes());

            channel.disconnect();
            session.disconnect();

            if (!stderr.isEmpty() && stdout.isEmpty()) {
                return "ERROR: " + stderr;
            }
            return stdout;
        } catch (Exception e) {
            log.error("SSH command execution failed for {}:{} - {}", host, port, e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            if (channel != null && channel.isConnected()) channel.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }
}
