package com.mymall.util;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by lamZe on 2017/11/22.<br>
 */

/**
 * 用于将文件上传到ftp服务器
 */
public class FTPUtil {
    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPwd = PropertiesUtil.getProperty("ftp.pwd");

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    public FTPUtil(String ip,int port,String user,String pwd) {
        this.ip = ip;
        this.port=port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> files) {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPwd);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img", files);

        logger.info("开始连接服务器，结束上传，上传结果{}",result);
        return result;
    }


    private boolean uploadFile(String remotePath,List<File> files) {
        Boolean uploaded = true;
        FileInputStream fis = null;

        //连接FTP服务器
        if (connectServer(this.getIp(), this.getPort(), this.user, this.pwd)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : files) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }

            } catch (IOException e) {
                logger.error("上传文件异常", e);
                uploaded = false;
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("文件传输通道关闭异常", e);
                }
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("ftpClient断连失败", e);
                }
                return false;
            }
        }
        return uploaded;
    }



    /**
     * 连接ftp服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    private boolean connectServer(String ip,int port,String user,String pwd) {
        boolean isSuccess = false;
         ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;

    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;


    public static void setFtpIp(String ftpIp) {
        FTPUtil.ftpIp = ftpIp;
    }


    public static void setFtpUser(String ftpUser) {
        FTPUtil.ftpUser = ftpUser;
    }



    public static void setFtpPwd(String ftpPwd) {
        FTPUtil.ftpPwd = ftpPwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
