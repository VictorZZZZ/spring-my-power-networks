package net.energo.grodno.pes.sms.utils.ftp;

import net.energo.grodno.pes.sms.exceptions.FtpClientException;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FtpClient implements Closeable {

    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;

    public FtpClient(String server, int port, String user, String password) throws IOException {
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.ftp = new FTPClient();
        this.ftp.setControlEncoding("Cp1251");
        open();
    }

    void open() throws IOException {
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);
    }

    public List<String> listFiles(String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }

    public void downloadFile(String source, String destination) throws IOException, FtpClientException {
        FileOutputStream out = new FileOutputStream(destination);
        if(!ftp.retrieveFile(source, out)){
            throw new FtpClientException("Не удалось скачать файл: " + source);
        };
    }

    public void close() throws IOException {
        ftp.disconnect();
    }
}
