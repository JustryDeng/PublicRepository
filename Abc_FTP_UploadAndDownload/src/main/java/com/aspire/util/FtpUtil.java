package com.aspire.util;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FTP上传、下载、删除 工具类
 * 约定:统一使用绝对路径
 *
 * @author JustryDeng
 * @date 2018年9月26日 上午10:38:46
 */
public class FtpUtil {

    private final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

    /** 路径分隔符 */
    private final String SEPARATOR_STR = "/";

    /** 点 */
    private final String DOT_STR = ".";

    /** ftp服务器地址 */
    private String hostname;

    /** 端口号 */
    private Integer port;

    /** ftp登录账号 */
    private String username;

    /** ftp登录密码 */
    private String password;

    /**
     * 命令 语句 编码(控制发出去的命令的编码)
     * 如:在删除时,发出去的指令由于此处的编码不对应的原因,乱码了;(找不到目标文件)导致删除失败
     * 如:在下载时,发出去的指令由于此处的编码不对应的原因,乱码了;(找不到目标文件)导致下载失败
     * 如:在上传时,发出去的指令由于此处的编码不对应的原因,乱码了;导致上传到FTP的文件的文件名乱码
     *
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     */
    private String sendCommandStringEncoding = "UTF-8";

    /**
     * 下载文件,文件名encode编码
     *
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     */
    private String downfileNameEncodingParam1 = "UTF-8";

    /**
     * 下载文件,文件名decode编码
     *
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     */
    private String downfileNameDecodingParam2 = "UTF-8";

    /**
     * 设置文件传输形式(使用FTP类静态常量赋值即可)
     *
     * 注:根据要下载上传的文件情况,这里灵活设置
     */
    private Integer transportFileType = FTP.BINARY_FILE_TYPE;

    /**
     * 用户FTP对应的根目录, 形如 /var/ftpusers/justry_deng_root
     * 注:初始化之后，其值不可能为 null
     *
     */
    private String userRootDir;

    /** FTP客户端 */
    private FTPClient ftpClient;

    private FtpUtil(String hostname, Integer port, String username, String password) {
        super();
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    /**
     * 设置下载时,文件名的编码
     * 即:new String(file.getName().getBytes(param1), param2) 中的param1
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     *
     * @date 2018年9月26日 下午7:34:26
     */
    public void setDownfileNameEncodingParam1(String downfileNameEncodingParam1) {
        this.downfileNameEncodingParam1 = downfileNameEncodingParam1;
    }

    /**
     * 设置下载时,文件名的编码
     * 即:new String(file.getName().getBytes(param1), param2) 中的param2
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     *
     * @date 2018年9月26日 下午7:34:26
     */
    public void setDownfileNameDecodingParam2(String downfileNameDecodingParam2) {
        this.downfileNameDecodingParam2 = downfileNameDecodingParam2;
    }


    /**
     * 设置文件传输形式 -> 二进制
     * 根据自己的时机情况,选择FTP.BINARY_FILE_TYPE或FTP.ASCII_FILE_TYPE等即可
     * 注:根据不同的文件情况,这里灵活设置
     *
     * @date 2018年9月27日 上午9:48:51
     */
    public void setTransportFileType(Integer transportFileType) {
        if( transportFileType != null) {
            this.transportFileType = transportFileType;
        }
    }

    /**
     * FTP的上传、下载、删除,底层还是 发送得命令语句; 这里就设置发送的命令语句的编码
     * 如:在删除时,发出去的指令由于此处的编码不对应的原因,乱码了;(找不到目标文件)导致删除失败
     * 如:在下载时,发出去的指令由于此处的编码不对应的原因,乱码了;(找不到目标文件)导致下载失败
     * 如:在上传时,发出去的指令由于此处的编码不对应的原因,乱码了;导致上传到FTP的文件的文件名乱码
     *
     *  Saves the character encoding to be used by the FTP control connection.
     *  Some FTP servers require that commands be issued in a non-ASCII
     *  encoding like UTF-8 so that filenames with multi-byte character
     *  representations (e.g, Big 8) can be specified.
     */
    public void setSendCommandStringEncoding(String sendCommandStringEncoding) {
        this.sendCommandStringEncoding = sendCommandStringEncoding;
    }

    /**
     * @param hostname
     *            FTPServer ip
     * @param port
     *            FTPServer 端口
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return FtpUtil实例
     * @date 2018年9月26日 下午4:39:02
     */
    public static FtpUtil getFtpUtilInstance(String hostname, Integer port, String username, String password) {
        return new FtpUtil(hostname, port, username, password);
    }

    /**
     * 初始化FTP服务器
     *
     * @throws IOException IO异常
     * @date 2018年9月26日 下午1:37:14
     */
    private void initFtpClient() throws IOException {
        if(ftpClient == null){
            ftpClient = new FTPClient();
        }
        // 设置编码(其是影响是否乱码的主要因素，在ftpClient.connect(hostname, port)连接前就要设置，否者不一定生效)
        ftpClient.setControlEncoding(sendCommandStringEncoding);
        // Returns the integer value of the reply code of the last FTP reply.
        int replyCode = ftpClient.getReplyCode();
        // 221表示 退出了网络，那么需要重新连接
        int ftpDisconnectionStatusCode = 221;
        // Determine if a reply code is a positive completion response.
        // FTPReply.isPositiveCompletion(replyCode)可用于验证是否连接FTP
        if (FTPReply.isPositiveCompletion(replyCode) && replyCode != ftpDisconnectionStatusCode) {
            LOGGER.info(" FtpUtil -> alreadly connected FTPServer !");
            return;
        } else {
            LOGGER.info(" FtpUtil -> connecting FTPServer -> {} : {}", this.hostname, this.port);
            // 连接ftp服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
            ftpClient.login(username, password);
            LOGGER.info(" FtpUtil -> connect FTPServer success!");
            // 初始化ftp用户根目录， 形如 /var/ftpusers/justry_deng_root
            userRootDir = ftpClient.printWorkingDirectory();
            if (userRootDir == null || "".equals(userRootDir.trim()) || SEPARATOR_STR.equals(userRootDir)) {
                userRootDir = "";
            }
        }
        // 设置文件传输形式
        ftpClient.setFileType(transportFileType);
        // 设置文件传输形式
        ftpClient.setFileType(transportFileType);
        // 设置FTP客户端(即本地)模式为被动模式
        ftpClient.enterLocalPassiveMode();

        /* TODO 此配置主要解决： 操作Linux下的FTP，.listFiles(xxx)方法不能获取到指定文件夹下 的 文件(夹)问题
         *      引入此配置后，反而可能导致  操作Windows下的FTP时，.listFiles(xxx)方法不能获取到指定文件夹下 的 文件(夹)，
         *      所以，如果是操作Windows下的FTP,操作失败时，可考虑将 此配置注释掉
         */
        // 由于apache不支持中文语言环境，通过定制类解析中文日期类型
        ftpClient.configure(new FTPClientConfig("com.aspire.util.UnixFTPEntryParser"));
    }

    /**
     * 上传文件至FTP
     * 注:若有同名文件,那么原文件会被覆盖
     *
     * @param remoteDir
     *            上传到指定目录(FTP用户pwd时的绝对路径)
     * @param remoteFileName
     *            上传到FTP,该文件的文件名
     * @param file
     *            要上传的本地文件
     *
     * @return 上传结果
     * @throws IOException IO异常
     * @date 2018年9月26日 下午1:35:27
     */
    @SuppressWarnings("unused")
    public boolean uploadFile(String remoteDir, String remoteFileName, File file) throws IOException{
        boolean result;
        remoteDir = handleRemoteDir(remoteDir);
        try(InputStream inputStream = new FileInputStream(file)){
            // 初始化
            initFtpClient();
            createDirecroty(remoteDir);
            ftpClient.changeWorkingDirectory(remoteDir);
            result = ftpClient.storeFile(remoteFileName, inputStream);
        }
        LOGGER.info(" FtpUtil -> uploadFile boolean result is -> {}", result);
        return result;
    }

    /**
     * 从FTP下载文件
     * 注:如果remoteDirOrRemoteFile不存在,则不会下载下来任何东西
     * 注:如果remoteDirOrRemoteFile不存在,localDir也不存在;再不会下载下来任何东西,
     *    也不会在本地创建localDir目录
     *
     * @param remoteDirOrRemoteFile
     *            FTP中的某一个目录(此时下载该目录下的所有文件,该目录下的文件夹不会被下载);
     *            或  FTP中的某一个文件全路径名(此时下载该文件)
     * @param localDir
     *            本地用于保存下载下来的文件的文件夹
     *
     * @return 下载了的文件个数
     * @throws IOException IO异常
     * @date 2018年9月26日 下午7:24:11
     */
    public int downloadFile(String remoteDirOrRemoteFile, String localDir) throws IOException{
        remoteDirOrRemoteFile = handleRemoteDir(remoteDirOrRemoteFile);
        int successSum = 0;
        int failSum = 0;
        initFtpClient();
        // 根据remoteDirOrRemoteFile是文件还是目录,来切换changeWorkingDirectory
        if (!remoteDirOrRemoteFile.contains(DOT_STR)) {
            // 切换至要下载的文件所在的目录,否者下载下来的文件大小为0
            boolean flag = ftpClient.changeWorkingDirectory(remoteDirOrRemoteFile);
            // 不排除那些 没有后缀名的文件 存在的可能;
            // 如果切换至该目录失败,那么其可能是没有后缀名的文件,那么尝试着下载该文件
            if (!flag) {
                return downloadNonsuffixFile(remoteDirOrRemoteFile, localDir);
            }
        } else {
            String tempWorkingDirectory;
            int index = remoteDirOrRemoteFile.lastIndexOf(SEPARATOR_STR);
            if (index > 0) {
                tempWorkingDirectory = remoteDirOrRemoteFile.substring(0, index);
            } else {
                tempWorkingDirectory = SEPARATOR_STR;
            }
            // 切换至要下载的文件所在的目录,否者下载下来的文件大小为0
            ftpClient.changeWorkingDirectory(tempWorkingDirectory);
        }
        File localFileDir = new File(localDir);
        // 获取remoteDirOrRemoteFile目录下所有 文件以及文件夹   或  获取指定的文件
        FTPFile[] ftpFiles = ftpClient.listFiles(remoteDirOrRemoteFile);
        for (FTPFile file : ftpFiles) {
            // 如果是文件夹,那么不下载 (因为:直接下载文件夹的话,是无效文件)
            if (file.isDirectory()) {
                continue;
            }
            //如果文件夹不存在则创建    
            if (!localFileDir.exists()) {
                boolean result = localFileDir.mkdirs();
                LOGGER.info(" {} is not exist, create this Dir! create result -> {}!",
                        localFileDir, result);
            }
            String name = new String(file.getName().getBytes(this.downfileNameEncodingParam1),
                    this.downfileNameDecodingParam2);
            String tempLocalFile = localDir.endsWith(SEPARATOR_STR) ?
                    localDir + name :
                    localDir + SEPARATOR_STR + name;
            File localFile = new File(tempLocalFile);
            try (OutputStream os = new FileOutputStream(localFile)) {
                boolean result = ftpClient.retrieveFile(file.getName(), os);
                if (result) {
                    successSum++;
                    LOGGER.info(" already download normal file -> {}", name);
                } else {
                    failSum++;
                }
            }
        }
        LOGGER.info(" FtpUtil -> downloadFile success download file total -> {}", successSum);
        LOGGER.info(" FtpUtil -> downloadFile fail download file total -> {}", failSum);
        return successSum;
    }

    /**
     * downloadFile的升级版 -> 其功能如下:
     *     1.remoteDirOrRemoteFile可为FTP上某一个文件的全路径名（绝对路径）
     *       -> 下载该文件,此处与downloadFile功能一致
     *
     *     2.remoteDirOrRemoteFile可为FTP上某一个文件目录名
     *       -> 下载该目录下的所有文件、文件夹(包括该文件夹中的所有文件文件夹并以此类推)
     *           注:对比downloadFile方法可知,downloadFile只能下载该目录下的所有文件,不能递归下载
     *
     * @date 2018年9月26日 下午7:26:22
     */
    public int recursiveDownloadFile(String remoteDirOrRemoteFile, String localDir) throws IOException {
        remoteDirOrRemoteFile = handleRemoteDir(remoteDirOrRemoteFile);
        int successSum = 0;
        // remoteDirOrRemoteFile是一个明确的文件  还是  一个目录
        if (remoteDirOrRemoteFile.contains(DOT_STR)) {
            successSum = downloadFile(remoteDirOrRemoteFile, localDir);
        } else {
            /// 初步组装数据,调用递归方法;查询给定FTP目录以及其所有子孙目录,进而得到FTP目录与本地目录的对应关系Map
            // 有序存放FTP remote文件夹路径
            // 其实逻辑是:先往alreadyQueriedDirList里面存,再进行的查询。此处可以这么处理。
            List<String> alreadyQueryDirList = new ArrayList<>(16);
            alreadyQueryDirList.add(remoteDirOrRemoteFile);
            // 有序存放FTP remote文件夹路径
            List<String> requiredQueryDirList = new ArrayList<>(16);
            requiredQueryDirList.add(remoteDirOrRemoteFile);
            // 记录FTP目录与 本地目录对应关系
            Map<String, String> storeDataMap = new HashMap<>(16);
            storeDataMap.put(remoteDirOrRemoteFile, localDir);
            queryFTPAllChildrenDirectory(storeDataMap, alreadyQueryDirList, requiredQueryDirList);
            String tempPath;
            // 循环调用downloadFile()方法,进行嵌套下载
            for(String str : alreadyQueryDirList) {
                // 将FTP用户的pwd的绝对路径转换为，用户输入的路径(因为 downloadFile方法会将用户输入的路径转化为pwd路径)
                // 提示:用户刚登陆进FTP时，输入pwd,得到的不一定是"/"，有可能时FTP对应的Linux上的文件夹路径，
                //     这与FTP的设置有关，可详见《程序员成长笔记(四)》搭建FTP服务器相关章节
                tempPath = str.length() > userRootDir.length() ?
                        str.substring(userRootDir.length()) :
                        SEPARATOR_STR;
                int thiscount = downloadFile(tempPath, storeDataMap.get(str));
                successSum += thiscount;
            }
        }
        System.out.println(" FtpUtil -> recursiveDownloadFile(excluded created directories) "
                + " success download file total -> " + successSum);
        return successSum;
    }

    /**
     * 删除文件 或 删除空的文件夹
     * 注:删除不存在的目录或文件  会导致删除失败
     * 注: 出于保护措施，输入的“/”时，不可删除，直接返回false
     *
     * @param deletedBlankDirOrFile
     *            要删除的文件的全路径名  或  要删除的空文件夹全路径名
     *        统一:路径分割符 用“/”,而不用“\”;
     *
     * @return 删除成功与否
     * @throws IOException IO异常
     * @date 2018年9月26日 下午9:12:07
     */
    public boolean deleteBlankDirOrFile(String deletedBlankDirOrFile) throws IOException{
        if(deletedBlankDirOrFile == null || SEPARATOR_STR.equals(deletedBlankDirOrFile)) {
            return false;
        }
        deletedBlankDirOrFile = handleRemoteDir(deletedBlankDirOrFile);
        boolean flag;
        initFtpClient();
        // 根据remoteDirOrRemoteFile是文件还是目录,来切换changeWorkingDirectory
        if (deletedBlankDirOrFile.lastIndexOf(DOT_STR) < 0) {
            // 出于保护机制:如果当前文件夹中是空的,那么才能删除成功
            flag = ftpClient.removeDirectory(deletedBlankDirOrFile);
            // 不排除那些 没有后缀名的文件 存在的可能;
            // 如果删除空文件夹失败,那么其可能是没有后缀名的文件,那么尝试着删除文件
            if (!flag) {
                flag = ftpClient.deleteFile(deletedBlankDirOrFile);
            }
            // 如果是文件,那么直接删除该文件
        } else {
            String tempWorkingDirectory;
            int index = deletedBlankDirOrFile.lastIndexOf(SEPARATOR_STR);
            if (index > 0) {
                tempWorkingDirectory = deletedBlankDirOrFile.substring(0, index);
            } else {
                tempWorkingDirectory = SEPARATOR_STR;
            }
            // 切换至要下载的文件所在的目录,否者下载下来的文件大小为0
            ftpClient.changeWorkingDirectory(tempWorkingDirectory);
            flag = ftpClient.deleteFile(deletedBlankDirOrFile.substring(index + 1));
        }
        LOGGER.info(" FtpUtil -> deleteBlankDirOrFile [{}] boolean result is -> {}",
                deletedBlankDirOrFile, flag);
        return flag;
    }


    /**
     * deleteBlankDirOrFile的加强版 -> 可删除文件、空文件夹、非空文件夹
     * 注: 出于保护措施，输入的“/”时，不可删除，直接返回false
     *
     * @param deletedBlankDirOrFile
     *            要删除的文件路径或文件夹
     *
     * @return 删除成功与否
     * @throws IOException IO异常
     * @date 2018年9月27日 上午1:25:16
     */
    public boolean recursiveDeleteBlankDirOrFile(String deletedBlankDirOrFile) throws IOException{
        if(deletedBlankDirOrFile == null || SEPARATOR_STR.equals(deletedBlankDirOrFile)) {
            return false;
        }
        String realDeletedBlankDirOrFile = handleRemoteDir(deletedBlankDirOrFile);
        boolean result = true;
        initFtpClient();
        if(!destDirExist(realDeletedBlankDirOrFile)) {
            LOGGER.info(" {} maybe is a  non-suffix file!, try delete!", realDeletedBlankDirOrFile);
            boolean flag = deleteBlankDirOrFile(deletedBlankDirOrFile);
            String flagIsTrue = " FtpUtil -> recursiveDeleteBlankDirOrFile "
                    + realDeletedBlankDirOrFile + " -> success!";
            String flagIsFalse = " FtpUtil -> recursiveDeleteBlankDirOrFile "
                    + realDeletedBlankDirOrFile + " -> target file is not exist!";
            LOGGER.info(flag ? flagIsTrue : flagIsFalse);
            return true;
        }
        // remoteDirOrRemoteFile是一个明确的文件  还是  一个目录
        if (realDeletedBlankDirOrFile.contains(DOT_STR) || !ftputilsChangeWorkingDirectory(realDeletedBlankDirOrFile)) {
            result = deleteBlankDirOrFile(deletedBlankDirOrFile);
        } else {
            /// 初步组装数据,调用递归方法;查询给定FTP目录以及其所有子孙目录、子孙文件        (含其自身)
            // 存放  文件夹路径
            // 其实逻辑是:先往alreadyQueriedDirList里面存,再进行的查询。此处可以这么处理。
            List<String> alreadyQueriedDirList = new ArrayList<>(16);
            alreadyQueriedDirList.add(realDeletedBlankDirOrFile);
            // 存放  文件路径
            List<String> alreadyQueriedFileList = new ArrayList<>(16);
            // 存放 文件夹路径
            List<String> requiredQueryDirList = new ArrayList<>(16);
            requiredQueryDirList.add(realDeletedBlankDirOrFile);
            queryAllChildrenDirAndChildrenFile(alreadyQueriedDirList,
                    alreadyQueriedFileList,
                    requiredQueryDirList);
            String tempPath;
            // 循环调用deleteBlankDirOrFile()方法,删除文件
            for (String filePath : alreadyQueriedFileList) {
                tempPath = filePath.length() > userRootDir.length() ?
                           filePath.substring(userRootDir.length()) :
                           SEPARATOR_STR;
                deleteBlankDirOrFile(tempPath);
            }
            // 对alreadyQueriedDirList进行排序,以保证等下删除时,先删除的空文件夹是 最下面的
            String[] alreadyQueriedDirArray = new String[alreadyQueriedDirList.size()];
            alreadyQueriedDirArray = alreadyQueriedDirList.toArray(alreadyQueriedDirArray);
            sortArray(alreadyQueriedDirArray);
            // 循环调用deleteBlankDirOrFile()方法,删除空的文件夹
            for (String str : alreadyQueriedDirArray) {
                tempPath = str.length() > userRootDir.length() ?
                           str.substring(userRootDir.length()) :
                           SEPARATOR_STR;
                boolean isSuccess = deleteBlankDirOrFile(tempPath);
                if (!isSuccess) {
                    result = false;
                }
            }
        }
        LOGGER.info(" FtpUtil -> recursiveDeleteBlankDirOrFile {} boolean result is -> {}",
                realDeletedBlankDirOrFile, result);
        return result;
    }

    /**
     * 根据数组元素的长度,来进行排序(字符串长的,排在前面)
     * 数组元素不能为null
     *
     * @date 2018年9月27日 上午12:54:03
     */
    private void sortArray(String[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for(int j = 0; j < array.length - 1 - i; j++) {
                if (array[j].length() - array[j+1].length() < 0) {
                    String flag=array[j];
                    array[j] = array[j+1];
                    array[j+1] = flag;
                }
            }
        }
    }

    /**
     *
     * 根据给出的FTP目录、对应本地目录; 查询该FTP目录的所有子目录 , 以及获得与每一个子
     * 目录对应的本地目录(含其自身以及与其自身对应的本地目录)
     *
     * @param storeDataMap
     *            存储FTP目录与本地目录的对应关系;key -> FTP目录, value -> 与key对应的本地目录
     * @param alreadyQueriedDirList
     *            所有已经查询过了的FTP目录,即:key集合
     * @param requiredQueryDirList
     *            还需要查询的FTP目录
     *
     * @throws IOException IO异常
     * @date 2018年9月26日 下午7:17:52
     */
    private void queryFTPAllChildrenDirectory(Map<String, String> storeDataMap,
                                              List<String> alreadyQueriedDirList,
                                              List<String> requiredQueryDirList) throws IOException {
        List<String> newRequiredQueryDirList = new ArrayList<>(16);
        if(requiredQueryDirList.size() == 0) {
            return;
        }
        for (String str : requiredQueryDirList) {
            String rootLocalDir = storeDataMap.get(str);
            // 获取rootRemoteDir目录下所有 文件以及文件夹(或  获取指定的文件)
            FTPFile[] ftpFiles = ftpClient.listFiles(str);
            for(FTPFile file : ftpFiles){
                if (file.isDirectory()) {
                    String tempName = file.getName();
                    String ftpChildrenDir = str.endsWith(SEPARATOR_STR) ?
                            str + tempName :
                            str + SEPARATOR_STR + tempName;
                    String localChildrenDir = rootLocalDir.endsWith(SEPARATOR_STR) ?
                            rootLocalDir + tempName :
                            rootLocalDir + SEPARATOR_STR + tempName;
                    alreadyQueriedDirList.add(ftpChildrenDir);
                    newRequiredQueryDirList.add(ftpChildrenDir);
                    storeDataMap.put(ftpChildrenDir, localChildrenDir);
                }
            }
        }
        this.queryFTPAllChildrenDirectory(storeDataMap, alreadyQueriedDirList, newRequiredQueryDirList);
    }

    /**
     * 根据给出的FTP目录,查询其所有子目录以及子文件(含其自身)
     *
     * @param alreadyQueriedDirList
     *            所有已经查询出来了的目录
     * @param alreadyQueriedFileList
     *            所有已经查询出来了的文件
     * @param requiredQueryDirList
     *            还需要查询的FTP目录
     * @throws IOException IO异常
     * @date 2018年9月27日 上午12:12:53
     */
    private void queryAllChildrenDirAndChildrenFile(List<String> alreadyQueriedDirList,
                                                    List<String> alreadyQueriedFileList,
                                                    List<String> requiredQueryDirList) throws IOException {
        List<String> newRequiredQueryDirList = new ArrayList<>(16);
        if (requiredQueryDirList.size() == 0) {
            return;
        }
        initFtpClient();
        for (String dirPath : requiredQueryDirList) {
            // 获取dirPath目录下所有 文件以及文件夹(或  获取指定的文件)
            FTPFile[] ftpFiles = ftpClient.listFiles(dirPath);
            for (FTPFile file : ftpFiles) {
                String tempName = file.getName();
                String ftpChildrenName = dirPath.endsWith(SEPARATOR_STR) ?
                        dirPath + tempName :
                        dirPath + SEPARATOR_STR + tempName;
                if (file.isDirectory()) {
                    alreadyQueriedDirList.add(ftpChildrenName);
                    newRequiredQueryDirList.add(ftpChildrenName);
                } else {
                    alreadyQueriedFileList.add(ftpChildrenName);
                }
            }

        }
        this.queryAllChildrenDirAndChildrenFile(alreadyQueriedDirList, alreadyQueriedFileList, newRequiredQueryDirList);
    }


    /**
     * 创建指定目录(注:如果要创建的目录已经存在,那么返回false)
     *
     * @param dir
     *            目录路径,绝对路径,如: /abc 或  /abc/ 可以
     *                   相对路径,如:  sss 或    sss/ 也可以
     *                  注:相对路径创建的文件夹所在位置时,相对于当前session所处目录位置。
     *                  提示: .changeWorkingDirectory() 可切换当前session所处目录位置
     * @return 创建成功与否
     * @throws IOException IO异常
     * @date 2018年9月26日 下午3:42:20
     */
    private boolean makeDirectory(String dir) throws IOException {
        boolean flag;
        flag = ftpClient.makeDirectory(dir);
        if (flag) {
            LOGGER.info(" FtpUtil -> makeDirectory -> create Dir [{}] success!", dir);
        } else {
            LOGGER.info(" FtpUtil -> makeDirectory -> create Dir [{}] fail!", dir);
        }
        return flag;
    }

    /**
     * 在FTP服务器上创建remoteDir目录(不存在,则创建;存在,则不创建)
     *
     * @param directory
     *            要创建的目录
     *            注:此目录指的是FTP用户pwd时获取到的目录（这也与FTP服务器设置是否允许用户切出上级目录有关）
     *            注:pwdRemoteDir不能为null或“”，pwdRemoteDir必须是绝对路径
     *
     * @throws IOException IO异常
     * @date 2018年9月26日 下午2:19:37
     */
    private void createDirecroty(String directory) throws IOException {
        if (!directory.equals(userRootDir) && !ftpClient.changeWorkingDirectory(directory)) {
            if (!directory.endsWith(SEPARATOR_STR)) {
                directory = directory  + SEPARATOR_STR;
            }
            // 获得每一个节点目录的起始位置
            int start = userRootDir.length() + 1;
            int end = directory.indexOf(SEPARATOR_STR, start);
            // 循环创建目录
            String dirPath = userRootDir;
            String subDirectory;
            boolean result;
            while (end >= 0) {
                subDirectory = directory.substring(start, end);
                dirPath = dirPath + SEPARATOR_STR + subDirectory;
                if (!ftpClient.changeWorkingDirectory(dirPath)) {
                    result = makeDirectory(dirPath);
                    LOGGER.info(" FtpUtil -> createDirecroty -> invoke makeDirectory got retrun -> {}!", result);
                }
                start = end + 1;
                end = directory.indexOf(SEPARATOR_STR, start);
            }
        }
    }


    /**
     * 避免在代码中频繁 initFtpClient、logout、disconnect;
     * 这里包装一下FTPClient的.changeWorkingDirectory(String pathname)方法
     *
     * @param pathname
     *            要切换(session)到FTP的哪一个目录下
     * @date 2018年9月27日 上午11:24:25
     */
    private boolean ftputilsChangeWorkingDirectory(String pathname) throws IOException{
        boolean result;
        initFtpClient();
        result = ftpClient.changeWorkingDirectory(pathname);
        return result;
    }

    /**
     * 判断FTP上某目录是否存在
     *
     * @param pathname
     *            要判断的路径(文件名全路径、文件夹全路径都可以)
     *            注:此路径应从根目录开始
     * @date 2018年9月27日 上午11:24:25
     */
    private boolean destDirExist(String pathname) throws IOException{
        boolean result;
        if (pathname.contains(DOT_STR)) {
            int index = pathname.lastIndexOf(SEPARATOR_STR);
            if (index != 0) {
                pathname = pathname.substring(0, index);
            } else {
                return true;
            }
        }
        result = ftpClient.changeWorkingDirectory(pathname);
        return result;
    }

    /**
     * 处理用户输入的 FTP路径
     * 注:这主要是为了 兼容  FTP(对是否允许用户切换到上级目录)的设置
     *
     * @param remoteDirOrFile
     *            用户输入的FTP路径
     * @return  处理后的路径
     * @date 2019/2/1 14:00
     */
    private String handleRemoteDir(String remoteDirOrFile) throws IOException {
        initFtpClient();
        if(remoteDirOrFile == null
                || "".equals(remoteDirOrFile.trim())
                || SEPARATOR_STR.equals(remoteDirOrFile)) {
            remoteDirOrFile = userRootDir + SEPARATOR_STR;
        } else if(remoteDirOrFile.startsWith(SEPARATOR_STR)) {
            remoteDirOrFile = userRootDir + remoteDirOrFile;
        } else {
            remoteDirOrFile = userRootDir + SEPARATOR_STR + remoteDirOrFile;
        }
        return remoteDirOrFile;
    }

    /**
     * 下载 无后缀名的文件
     *
     * @param remoteDirOrFile
     *            经过handleRemoteDir()方法处理后的   FTP绝对路径
     *
     * @return  成功条数
     * @throws IOException IO异常
     * @date 2019/2/1 14:23
     */
    private int downloadNonsuffixFile(String remoteDirOrFile, String localDir) throws IOException {
        int successSum = 0;
        int failSum = 0;
        File localFileDir = new File(localDir);
        String tempWorkingDirectory;
        String tempTargetFileName;
        int index = remoteDirOrFile.lastIndexOf(SEPARATOR_STR);
        tempTargetFileName = remoteDirOrFile.substring(index + 1);
        if(tempTargetFileName.length() > 0) {
            if (index > 0) {
                tempWorkingDirectory = remoteDirOrFile.substring(0, index);
            }else {
                tempWorkingDirectory = SEPARATOR_STR;
            }
            ftpClient.changeWorkingDirectory(tempWorkingDirectory);
            // 获取tempWorkingDirectory目录下所有 文件以及文件夹   或  获取指定的文件
            FTPFile[] ftpFiles = ftpClient.listFiles(tempWorkingDirectory);
            for(FTPFile file : ftpFiles){
                String name = new String(file.getName().getBytes(this.downfileNameEncodingParam1),
                        this.downfileNameDecodingParam2);
                // 如果不是目标文件,那么不下载
                if(!tempTargetFileName.equals(name)) {
                    continue;
                }
                //如果文件夹不存在则创建    
                if (!localFileDir.exists()) {
                    boolean result = localFileDir.mkdirs();
                    LOGGER.info(" {} is not exist, create this Dir! create result -> {}!",
                            localFileDir, result);
                }
                String tempLocalFile = localDir.endsWith(SEPARATOR_STR) ?
                        localDir + name :
                        localDir + SEPARATOR_STR + name;
                File localFile = new File(tempLocalFile);
                try (OutputStream os = new FileOutputStream(localFile)) {
                    boolean result = ftpClient.retrieveFile(file.getName(), os);
                    if (result) {
                        successSum++;
                        LOGGER.info(" already download nonsuffixname file -> {}", name);
                    } else {
                        failSum++;
                    }
                }
                LOGGER.info(" FtpUtil -> downloadFile success download item count -> {}", successSum);
                LOGGER.info(" FtpUtil -> downloadFile fail download item count -> {}", failSum);
            }
        }
        return successSum;
    }

    /**
     * 释放资源
     *
     * 注:考虑到 递归下载、递归删除，如果将释放资源写在下载、删除逻辑里面，那么
     *    当文件较多时，就不频繁的连接FTP、断开FTP；那么久非常影响效率；
     *    所以干脆提供一个方法，在使用FTP结束后，需要主动调用此方法 释放资源
     *
     *
     */
    public void releaseResource() throws IOException {
        if (ftpClient == null) {
            return;
        }
        try {
            ftpClient.logout();
        } catch (IOException e) {
            // 连接未打开的话
            // 忽略 java.io.IOException: Connection is not open
        }
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
        ftpClient = null;
    }
}