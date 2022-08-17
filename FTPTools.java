package cn.qy.util;

import cn.qy.common.Constant;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * @注释 通过FTP上传文件
 */
public class FTPTools {

    /**
     * 用于打印日志
     */
    private static final Logger logger = LoggerFactory.getLogger(FTPTools.class);

    /**
     * 上传
     * @param inputStream 要上传文件的输入流
     * @param saveName    设置上传之后的文件名
     * @return
     */
    public static boolean upload(String hostname,int port,String username,String password,String workingPath,InputStream inputStream, String saveName) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        //1 测试连接
        if (connect(ftpClient, hostname, port, username, password)) {
            try {
                //切换到上传目录
                if (!ftpClient.changeWorkingDirectory(workingPath)) {
                    String urlPath = "/" +workingPath.substring(Constant.INT_ZERO,workingPath.length() - Constant.INT_ONE);
                    String[] dirs = urlPath.split("/");
                    String tempPath = "";
                    //如果目录不存在创建目录
                    for (String dir : dirs) {
                        if (null == dir || "".equals(dir)){
                            continue;
                        }
                        tempPath += "/" + dir;
                        if (!ftpClient.changeWorkingDirectory(tempPath)) {
                            System.out.println(tempPath);
                            if (!ftpClient.makeDirectory(tempPath)) {
                                throw new RuntimeException("6020");
                            } else {
                                //切换到上传目录
                                ftpClient.changeWorkingDirectory(tempPath);
                            }
                        }
                    }
                }
                // 2 检查是否上传成功
                if (storeFile(ftpClient, saveName, inputStream)) {
                    flag = true;
                    disconnect(ftpClient);
                }
            } catch (IOException e) {
                logger.error("工作目录不存在");
                e.printStackTrace();
                disconnect(ftpClient);
            }
        }
        return flag;
    }

    /**
     * 断开连接
     *
     * @param ftpClient
     * @throws Exception
     */
    public static void disconnect(FTPClient ftpClient) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
                logger.error("已关闭连接");
            } catch (IOException e) {
                logger.error("没有关闭连接");
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试是否能连接
     * @param ftpClient
     * @return 返回真则能连接
     */
    public static boolean connect(FTPClient ftpClient,String hostname,int port,String username,String password) {
        boolean flag = false;
        try {
            //ftp初始化的一些参数
            ftpClient.connect(hostname, port);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("UTF-8");
            if (ftpClient.login(username, password)) {
                logger.info("连接ftp成功");
                flag = true;
            } else {
                logger.error("连接ftp失败，可能用户名或密码错误");
                try {
                    disconnect(ftpClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            logger.error("连接失败，可能ip或端口错误");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 上传文件
     *
     * @param ftpClient
     * @param saveName        全路径。如/home/public/a.txt
     * @param fileInputStream 要上传的文件流
     * @return
     */
    public static boolean storeFile(FTPClient ftpClient, String saveName, InputStream fileInputStream) {
        boolean flag = false;
        try {
            if (ftpClient.storeFile(saveName, fileInputStream)) {
                flag = true;
                logger.error("上传成功");
                disconnect(ftpClient);
            }
        } catch (IOException e) {
            logger.error("上传失败");
            disconnect(ftpClient);
            e.printStackTrace();
        }
        return flag;
    }
}
