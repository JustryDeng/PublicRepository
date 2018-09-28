package com.aspire.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP上传、下载、删除 工具类
 * 约定:统一使用绝对路径
 *
 * @author JustryDeng
 * @DATE 2018年9月26日 上午10:38:46
 */
public class FTPUtils {
	
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
    private String sendCommandStringEncoding = "ISO-8859-1";
    
    /**
     * 下载文件,文件名encode编码
     * 
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     */
    private String downfileNameEncodingParam1 = "ISO-8859-1";
    
    /**
     * 下载文件,文件名decode编码
     * 
     * 注:根据不同的(Server/Client)情况,这里灵活设置
     */
    private String downfileNameDecodingParam2 = "GBK";
    
    /**
     * 设置文件传输形式(使用FTP类静态常量赋值即可)
     * 
     * 注:根据要下载上传的文件情况,这里灵活设置
     */
    private Integer transportFileType = FTP.BINARY_FILE_TYPE;
    
    /** FTP客户端引用 */
    private FTPClient ftpClient = null;
    
    public FTPClient getFtpClient() {
		return ftpClient;
	}

	private FTPUtils(String hostname, Integer port, String username, String password) {
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
	 * @DATE 2018年9月26日 下午7:34:26
	 */
    public void setDownfileNameEncodingParam1(String downfileNameEncodingParam1) {
		this.downfileNameEncodingParam1 = downfileNameEncodingParam1;
	}

	/**
	 * 设置下载时,文件名的编码
	 * 即:new String(file.getName().getBytes(param1), param2) 中的param2
	 * 注:根据不同的(Server/Client)情况,这里灵活设置
     *
	 * @DATE 2018年9月26日 下午7:34:26
	 */
	public void setDownfileNameDecodingParam2(String downfileNameDecodingParam2) {
		this.downfileNameDecodingParam2 = downfileNameDecodingParam2;
	}
	

	/**
	 * 设置文件传输形式 -> 二进制
	 * 根据自己的时机情况,选择FTP.BINARY_FILE_TYPE或FTP.ASCII_FILE_TYPE等即可
	 * 注:根据不同的文件情况,这里灵活设置
	 *
	 * @DATE 2018年9月27日 上午9:48:51
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
     * @return FTPUtils实例
     * @DATE 2018年9月26日 下午4:35:40
     */
    public static FTPUtils getFTPUtilsInstance(String hostname, Integer port) {
    	return getFTPUtilsInstance(hostname, port, null, null);
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
     * @return FTPUtils实例
     * @DATE 2018年9月26日 下午4:39:02
     */
    public static FTPUtils getFTPUtilsInstance(String hostname, Integer port, String username, String password) {
    	return new FTPUtils(hostname, port, username, password);
    }

    /**
     * 初始化FTP服务器
     * 注:连接FTP服务器后,当前目录(即:session)默认处于根目录“/”下;
     *    所以如果一开始就是相对路径的话,那么是相对根目录的
     *
     * @throws IOException
     * @DATE 2018年9月26日 下午1:37:14
     */
    private void initFtpClient() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding(sendCommandStringEncoding);
        System.out.println(" FTPUtils -> connecting FTPServer -> " + this.hostname + ":" + this.port); 
        // 连接ftp服务器
        ftpClient.connect(hostname, port);
        if(username != null && password != null) {
        	//登录ftp服务器
            ftpClient.login(username, password); 
        }
        // 设置文件传输形式
        ftpClient.setFileType(transportFileType);
        // Returns the integer value of the reply code of the last FTP reply.
        int replyCode = ftpClient.getReplyCode(); 
        // Determine if a reply code is a positive completion response.
        if(FTPReply.isPositiveCompletion(replyCode)){
        	System.out.println(" FTPUtils -> connect FTPServer success!"); 
        } else {
        	System.err.println(" FTPUtils -> connect FTPServer fail!"); 
        }
    }

    /**
     * 上传文件至FTP
     * 注:若有同名文件,那么原文件会被覆盖
     *
     * @param remoteDir
     *            上传到指定目录(绝对路径)  如果写的相对路径,那么会默认在其前面加一个"/"
     *         统一:路径分割符 用“/”,而不用“\”;
     *         
     * @param remoteFileName
     *            上传到FTP,该文件的文件名
     * @param file
     *            要上传的本地文件
     * @return 上传结果
     * @throws IOException
     * @DATE 2018年9月26日 下午1:35:27
     */
    public boolean uploadFile(String remoteDir, String remoteFileName, File file) throws IOException{
        boolean result = false;
        InputStream inputStream = null;
        try{
            inputStream = new FileInputStream(file);
            // 初始化
            initFtpClient();
            CreateDirecroty(remoteDir);
            ftpClient.makeDirectory(remoteDir);
            ftpClient.changeWorkingDirectory(remoteDir);
            result = ftpClient.storeFile(remoteFileName, inputStream);
        }finally{
        	// 先登出、再关闭连接
        	ftpClient.logout();
            if(ftpClient.isConnected()){ 
                ftpClient.disconnect();
            } 
            if(null != inputStream){
                inputStream.close();
            } 
        }
        System.out.println(" FTPUtils -> uploadFile boolean result is ---> " + result);
        return result;
    }

    
    /**
     * 从FTP下载文件
     * 注:如果remoteDirOrRemoteFile不存在,再不会下载下来任何东西
     * 注:如果remoteDirOrRemoteFile不存在,localDir也不存在;再不会下载下来任何东西,
     *    也不会在本地创建localDir目录
     * 提示:此处代码由于把无后缀名文件也考虑在内了,所以代码略显冗余;后期有时间我会进行优化
     *
     * @param remoteDirOrRemoteFile
     *            FTP中的某一个目录(此时下载该目录下的所有文件,该目录下的文件夹不会被下载); 
     *            或  FTP中的某一个文件全路径名(此时下载该文件)
     *        统一:路径分割符 用“/”,而不用“\”;
     *        
     * @param localDir
     *            本地用于保存下载下来的文件的文件夹
     *        统一:路径分割符 用“/”,而不用“\”;
     * @return 下载了的文件个数
     * @throws IOException
     * @DATE 2018年9月26日 下午7:24:11
     */
    public int downloadFile(String remoteDirOrRemoteFile, String localDir) throws IOException{ 
        int successSum = 0; 
        int failSum = 0; 
        OutputStream os=null;
        // 这里先根据本地目录创建一个File实例,下面再根据是否有下载的文件,来判断(如果此目录不存在的话则创建此目录)
        File localFileDir = new File(localDir);
        try { 
            initFtpClient();
            // 根据remoteDirOrRemoteFile是文件还是目录,来切换changeWorkingDirectory
            if(remoteDirOrRemoteFile.lastIndexOf(".") < 0) {
	            // 切换至要下载的文件所在的目录,否者下载下来的文件大小为0
	            boolean flag = ftpClient.changeWorkingDirectory(remoteDirOrRemoteFile);
            	// 不排除那些 没有后缀名的文件 存在的可能;
            	// 如果切换至该目录失败,那么其可能是没有后缀名的文件,那么尝试着下载该文件
            	if (!flag) {
            		String tempWorkingDirectory = "";
            		String tempTargetFileName = "";
            		int index = remoteDirOrRemoteFile.lastIndexOf("/");
            		tempTargetFileName = remoteDirOrRemoteFile.substring(index + 1);
            		if(tempTargetFileName.length() > 0) {
	            		if (index > 0) {
	                		tempWorkingDirectory = remoteDirOrRemoteFile.substring(0, index);
	                	}else {
	                		tempWorkingDirectory = "/";
	                	}
	            		ftpClient.changeWorkingDirectory(tempWorkingDirectory);
	            		// 获取tempWorkingDirectory目录下所有 文件以及文件夹   或  获取指定的文件
	                    FTPFile[] ftpFiles = ftpClient.listFiles(tempWorkingDirectory);
	                    for(FTPFile file : ftpFiles){ 
	                    	// 如果是文件夹,那么不下载 (因为:直接下载文件夹的话,是无效文件)
	                    	if(!tempTargetFileName.equals(file.getName())) {
	                    		continue;
	                    	}
	                    	String name = new String(file.getName().getBytes(this.downfileNameEncodingParam1), 
	                    			                 this.downfileNameDecodingParam2);
	                    	//如果文件夹不存在则创建    
	                    	if (!localFileDir.exists()) {
	                    		System.out.println(" " + localFileDir + " is not exist, create this Dir!");
	                    		localFileDir.mkdir();
	                    	}
	                        File localFile = new File(localDir + "/" + name); 
	                        os = new FileOutputStream(localFile); 
	                        boolean result = ftpClient.retrieveFile(file.getName(), os); 
	                        if (result) {
	                            successSum++;
	                        } else {
	                        	failSum++;
	                        }
	                        System.out.println(" already success download item count ---> " + successSum);
	                    } 
            		}
            	}
            }else {
            	String tempWorkingDirectory = "";
            	int index = remoteDirOrRemoteFile.lastIndexOf("/");
            	if (index > 0) {
            		tempWorkingDirectory = remoteDirOrRemoteFile.substring(0, index);
            	}else {
            		tempWorkingDirectory = "/";
            	}
            	// 切换至要下载的文件所在的目录,否者下载下来的文件大小为0
	            ftpClient.changeWorkingDirectory(tempWorkingDirectory);
            }
            // 获取remoteDirOrRemoteFile目录下所有 文件以及文件夹   或  获取指定的文件
            FTPFile[] ftpFiles = ftpClient.listFiles(remoteDirOrRemoteFile);
            for(FTPFile file : ftpFiles){ 
            	// 如果是文件夹,那么不下载 (因为:直接下载文件夹的话,是无效文件)
            	if(file.isDirectory()) {
            		continue;
            	}
            	String name = new String(file.getName().getBytes(this.downfileNameEncodingParam1), 
            			                 this.downfileNameDecodingParam2);
            	//如果文件夹不存在则创建    
            	if (!localFileDir.exists()) {
            		System.out.println(" " + localFileDir + " is not exist, create this Dir!");
            		localFileDir.mkdir();
            	}
                File localFile = new File(localDir + "/" + name); 
                os = new FileOutputStream(localFile); 
                boolean result = ftpClient.retrieveFile(file.getName(), os); 
                if (result) {
                    successSum++;
                } else {
                	failSum++;
                }
                System.out.println(" already success download item count ---> " + successSum);
            } 
        } finally{ 
        	// 先登出、再关闭连接
        	ftpClient.logout(); 
            if(ftpClient.isConnected()){ 
                ftpClient.disconnect();
            } 
            if(os != null){
                os.close();
            } 
        } 
        System.out.println(" FTPUtils -> downloadFile success download file total ---> " + successSum);
        System.out.println(" FTPUtils -> downloadFile fail download file total ---> " + failSum);
        return successSum; 
    }

    /**
     * downloadFile的升级版 -> 其功能如下:
     *     1.remoteDirOrRemoteFile可为FTP上某一个文件的全路径名
     *       ---> 下载该文件,此处与downloadFile功能一致
     *     
     *     2.remoteDirOrRemoteFile可为FTP上某一个文件目录名
     *       ---> 下载该目录下的所有文件、文件夹(包括该文件夹中的所有文件文件夹并以此类推) 
     *           注:对比downloadFile方法可知,downloadFile只能下载该目录下的所有文件,不能递归下载
     *
     * @DATE 2018年9月26日 下午7:26:22
     */
    public int recursiveDownloadFile(String remoteDirOrRemoteFile, String localDir) throws IOException{ 
    	int successSum = 0; 
    	// remoteDirOrRemoteFile是一个明确的文件  还是  一个目录
    	if(remoteDirOrRemoteFile.indexOf(".") >= 0) {
    		successSum = downloadFile(remoteDirOrRemoteFile, localDir);
    	}else {
	        /// 初步组装数据,调用递归方法;查询给定FTP目录以及其所有子孙目录,进而得到FTP目录与本地目录的对应关系Map
	        // 有序存放FTP remote文件夹路径
    		// 其实逻辑是:先往alreadyQueriedDirList里面存,再进行的查询。此处可以这么处理。
	        List<String> alreadyQueryDirList = new ArrayList<>(16); 
	        alreadyQueryDirList.add(remoteDirOrRemoteFile);
	        // 有序存放FTP remote文件夹路径
	        List<String> requiredQueryDirList = new ArrayList<>(16); 
	        requiredQueryDirList.add(remoteDirOrRemoteFile);
	        // 记录FTP目录与 本地目录对应关系
	        Map<String, String> storeDataMap = new HashMap<>();
	        storeDataMap.put(remoteDirOrRemoteFile, localDir);
	        queryFTPAllChildrenDirectory(storeDataMap, alreadyQueryDirList, requiredQueryDirList);
	        
	        // 循环调用downloadFile()方法,进行嵌套下载
	        for (int i = 0; i < alreadyQueryDirList.size(); i++) {
	        	int thiscount = downloadFile(alreadyQueryDirList.get(i), 
	        			storeDataMap.get(alreadyQueryDirList.get(i)));
	        	successSum += thiscount;
			}
    	}
        System.out.println(" FTPUtils -> recursiveDownloadFile(excluded created directories) "
        		               + " success download file total ---> " + successSum);
        return successSum; 
    }
 

    /**
     * 删除文件 或 删除空的文件夹
     * 注:删除不存在的目录或文件  会导致删除失败
     *
     * @param deletedDirOrFile
     *            要删除的文件的全路径名  或  要删除的空文件夹全路径名
     *        统一:路径分割符 用“/”,而不用“\”;
     *        
     * @return 删除成功与否
     * @throws IOException
     * @DATE 2018年9月26日 下午9:12:07
     */
    public boolean deleteBlankDirOrFile(String deletedBlankDirOrFile) throws IOException{ 
        boolean flag = false; 
        try { 
            initFtpClient();
            // 根据remoteDirOrRemoteFile是文件还是目录,来切换changeWorkingDirectory
            if(deletedBlankDirOrFile.lastIndexOf(".") < 0) {
	            // 出于保护机制:如果当前文件夹中是空的,那么才能删除成功
            	flag = ftpClient.removeDirectory(deletedBlankDirOrFile);
            	// 不排除那些 没有后缀名的文件 存在的可能;
            	// 如果删除空文件夹失败,那么其可能是没有后缀名的文件,那么尝试着删除文件
            	if (!flag) {
            		flag = ftpClient.deleteFile(deletedBlankDirOrFile);
            	}
            }else {/// 如果是文件,那么直接删除该文件
            	String tempWorkingDirectory = "";
            	int index = deletedBlankDirOrFile.lastIndexOf("/");
            	if (index > 0) {
            		tempWorkingDirectory = deletedBlankDirOrFile.substring(0, index);
            	}else {
            		tempWorkingDirectory = "/";
            	}
            	// 切换至要下载的文件所在的目录,否者下载下来的文件大小为0
	            ftpClient.changeWorkingDirectory(tempWorkingDirectory);
	            flag = ftpClient.deleteFile(deletedBlankDirOrFile.substring(index + 1));
            }
        } finally {
        	ftpClient.logout();
            if(ftpClient.isConnected()){ 
                ftpClient.disconnect();
            } 
        }
        if (flag == false) {
        	System.out.println(" FTPUtils -> deleteBlankDirOrFile Maybe [" + deletedBlankDirOrFile 
		               				+ "]  doesn't exist !");
        }
        System.out.println(" FTPUtils -> deleteBlankDirOrFile [" + deletedBlankDirOrFile 
        		               + "] boolean result is ---> " + flag);
        return flag; 
    }
    
    
    /**
     * deleteBlankDirOrFile的加强版 -> 可删除文件、空文件夹、非空文件夹
     *
     * @param deletedBlankDirOrFile
     * @return
     * @throws IOException
     * @DATE 2018年9月27日 上午1:25:16
     */
    public boolean recursiveDeleteBlankDirOrFile(String deletedBlankDirOrFile) throws IOException{ 
    	boolean result = true; 
    	if(!destDirExist(deletedBlankDirOrFile)) {
    		System.out.println(" " + deletedBlankDirOrFile + " maybe is a  non-suffix file!, try delete!");
    		boolean flag = deleteBlankDirOrFile(deletedBlankDirOrFile);
    		String flagIsTrue = " FTPUtils -> recursiveDeleteBlankDirOrFile " 
    		                        + deletedBlankDirOrFile + "---> success!";
    		String flagIsFalse = " FTPUtils -> recursiveDeleteBlankDirOrFile " 
                    + deletedBlankDirOrFile + "---> target file is not exist!";
    		System.out.println(flag == true ? flagIsTrue : flagIsFalse);
    		return true;
    	}
    	// remoteDirOrRemoteFile是一个明确的文件  还是  一个目录
    	if (deletedBlankDirOrFile.indexOf(".") >= 0 || !ftputilsChangeWorkingDirectory(deletedBlankDirOrFile)) {
    		result = deleteBlankDirOrFile(deletedBlankDirOrFile);
    	} else {
	        /// 初步组装数据,调用递归方法;查询给定FTP目录以及其所有子孙目录、子孙文件        (含其自身)
	        // 存放  文件夹路径
    		// 其实逻辑是:先往alreadyQueriedDirList里面存,再进行的查询。此处可以这么处理。
	        List<String> alreadyQueriedDirList = new ArrayList<>(16); 
	        alreadyQueriedDirList.add(deletedBlankDirOrFile);
	        // 存放  文件路径
	        List<String> alreadyQueriedFileList = new ArrayList<>(16); 
	        // 存放 文件夹路径
	        List<String> requiredQueryDirList = new ArrayList<>(16); 
	        requiredQueryDirList.add(deletedBlankDirOrFile);
	        queryAllChildrenDirAndChildrenFile(alreadyQueriedDirList, 
								        		alreadyQueriedFileList, 
								        		requiredQueryDirList);
	        
	        // 循环调用deleteBlankDirOrFile()方法,删除文件
	        for (int i = 0; i < alreadyQueriedFileList.size(); i++) {
	        	boolean isSuccess = deleteBlankDirOrFile(alreadyQueriedFileList.get(i));
	        	if (!isSuccess) {
	        		result = false;
	        	}
			}
	        
	        // 对alreadyQueriedDirList进行排序,以保证等下删除时,先删除的空文件夹是 最下面的
	        String[] alreadyQueriedDirArray = new String[alreadyQueriedDirList.size()];
	        alreadyQueriedDirArray = alreadyQueriedDirList.toArray(alreadyQueriedDirArray);
	        sortArray(alreadyQueriedDirArray);
	        
	        // 循环调用deleteBlankDirOrFile()方法,删除空的文件夹
	        for (int i = 0; i < alreadyQueriedDirArray.length; i++) {
	        	boolean isSuccess = deleteBlankDirOrFile(alreadyQueriedDirArray[i]);
	        	if (!isSuccess) {
	        		result = false;
	        	}
			}
    	}
        System.out.println(" FTPUtils -> recursiveDeleteBlankDirOrFile "
        		               + " boolean result is---> " + result);
        return result;
    }
    
    
    
    /*  -------------JustryDeng-------------以下为辅助方法-------------JustryDeng------------- */
    
    /**
     * 根据数组元素的长度,来进行排序(字符串长的,排在前面)
     * 数组元素不能为null
     *
     * @DATE 2018年9月27日 上午12:54:03
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
     * 根据给出的FTP目录、对应本地目录; 查询该FTP目录的所有子目录 , 以及获得与每一个子目录对应的本地目录(含其自身以及与其自身对应的本地目录)
     *
     * @param storeDataMap
     *            存储FTP目录与本地目录的对应关系;key -> FTP目录, value -> 与key对应的本地目录
     * @param alreadyQueryDirList
     *            所有已经查询过了的FTP目录,即:key集合
     * @param requiredQueryDirList
     *            还需要查询的FTP目录
     * @throws IOException
     * @DATE 2018年9月26日 下午7:17:52
     */
    private void queryFTPAllChildrenDirectory(Map<String, String> storeDataMap, 
									    		List<String> alreadyQueriedDirList, 
									    		List<String> requiredQueryDirList) throws IOException {
    	List<String> newRequiredQueryDirList = new ArrayList<>(16); 
    	initFtpClient();
    	try { 
    		if(requiredQueryDirList.size() == 0) {
    			return;
    		}
    		for (int i = 0; i < requiredQueryDirList.size(); i++) {
    			String rootRemoteDir = requiredQueryDirList.get(i);
    			String rootLocalDir = storeDataMap.get(requiredQueryDirList.get(i));
	            // 获取rootRemoteDir目录下所有 文件以及文件夹(或  获取指定的文件)
	            FTPFile[] ftpFiles = ftpClient.listFiles(rootRemoteDir);
	            for(FTPFile file : ftpFiles){ 
	             	if (file.isDirectory()) {
	             		String tempName = file.getName();
	             		String ftpChildrenDir = "";
         				ftpChildrenDir = rootRemoteDir + "/" + tempName  ;
	             		String localChildrenDir = "";
             			localChildrenDir = rootLocalDir + "/" + tempName  ;
             			alreadyQueriedDirList.add(ftpChildrenDir);
	             		newRequiredQueryDirList.add(ftpChildrenDir);
	             		storeDataMap.put(ftpChildrenDir, localChildrenDir);
	             	}
	            } 
    		}
    	} finally{ 
    		// 先登出、再关闭连接
    		ftpClient.logout(); 
    		if (ftpClient.isConnected()){ 
    			ftpClient.disconnect();
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
     * @throws IOException
     * @DATE 2018年9月27日 上午12:12:53
     */
    private void queryAllChildrenDirAndChildrenFile(List<String> alreadyQueriedDirList, 
									    		List<String> alreadyQueriedFileList, 
									    		List<String> requiredQueryDirList) throws IOException {
    	List<String> newRequiredQueryDirList = new ArrayList<>(16); 
    	initFtpClient();
    	try { 
    		if(requiredQueryDirList.size() == 0) {
    			return;
    		}
    		for (int i = 0; i < requiredQueryDirList.size(); i++) {
    			String dirPath = requiredQueryDirList.get(i);
	            // 获取dirPath目录下所有 文件以及文件夹(或  获取指定的文件)
	            FTPFile[] ftpFiles = ftpClient.listFiles(dirPath);
	            for(FTPFile file : ftpFiles){ 
	             	if (file.isDirectory()) {
	             		String tempName = file.getName();
	             		String ftpChildrenDir = dirPath + "/" + tempName;
	             		alreadyQueriedDirList.add(ftpChildrenDir);
	             		newRequiredQueryDirList.add(ftpChildrenDir);
	             	} else {
	             		String tempName = file.getName();
	             		String ftpChildrenFile = dirPath + "/" + tempName;
	             		alreadyQueriedFileList.add(ftpChildrenFile);
	             	}
	            } 
    	 
    		}
    	} finally{ 
    		// 先登出、再关闭连接
    		ftpClient.logout(); 
    		if (ftpClient.isConnected()){ 
    			ftpClient.disconnect();
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
     * @throws IOException 
     * @DATE 2018年9月26日 下午3:42:20
     */
    private boolean makeDirectory(String dir) throws IOException {
        boolean flag = false;
        flag = ftpClient.makeDirectory(dir);
        if (flag) {
            System.out.println(" FTPUtils -> makeDirectory -> create Dir [" + dir + "] success!");
        } else {
            System.err.println(" FTPUtils -> makeDirectory -> create Dir [" + dir + "] fail!");
        }
        return flag;
    }
    
    /**
     * 在FTP服务器上创建remoteDir目录(不存在,则创建;存在,则不创建)
     *
     * @param remoteDir
     *            要创建的目录   为null或为"" 则视为  根目录 
     * @return 结果
     * @throws IOException
     * @DATE 2018年9月26日 下午2:19:37
     */
    private boolean CreateDirecroty(String remoteDir) throws IOException {
        boolean success = true;
        String directory = null;
        if(remoteDir == null || remoteDir.trim().equals("")) {
        	directory = "/";
        }else if(remoteDir.endsWith("/")) {
        	directory = remoteDir;
        }else {
        	directory = remoteDir + "/";
        }
        // directory不为根目录 且 切换至该目录失败 -> 说明FTPServer中不存在该目录,那么进行创建
        /*
         * .changeWorkingDirectory(directory)中的directory为 要切换到的目录
         *   可为 -> 绝对路径; 可为 -> 相对路径(如果为相对路径,那么相对于当前session所处目录)
         */
        if (!directory.equals("/") && !ftpClient.changeWorkingDirectory(directory)) {
        	// 获得每一个节点目录的起始位置
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            // 循环创建目录
            String dirPath = "";
            String paths = "";
            while (true) {
                String subDirectory = directory.substring(start, end);
                dirPath = dirPath + "/" + subDirectory;
                if (!ftpClient.changeWorkingDirectory(dirPath)) {
                    makeDirectory(dirPath);
                    ftpClient.changeWorkingDirectory(dirPath);
                    // 当前session所处FTP目录位置
                    String currentDirPath = ftpClient.printWorkingDirectory();
                    System.out.println(" FTPUtils -> current position dirPath ---> " + currentDirPath);
                } 
                // 根性子节点目录名 index起始位置
                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end < 0) {
                    break;
                }
            }
        }
        return success;
    }
    
    
    /**
     * 避免在代码中频繁 initFtpClient、logout、disconnect;
     * 这里包装一下FTPClient的.changeWorkingDirectory(String pathname)方法
     *
     * @param directory
     *            要切换(session)到FTP的哪一个目录下
     * @DATE 2018年9月27日 上午11:24:25
     */
    private boolean ftputilsChangeWorkingDirectory(String pathname) throws IOException{
    	boolean result = true;
	    try {
	    	initFtpClient();
	    	result = ftpClient.changeWorkingDirectory(pathname);
	    }finally{
	    	// 先登出、再关闭连接
	    	ftpClient.logout();
	        if(ftpClient.isConnected()){ 
	            ftpClient.disconnect();
	        }
	    }
	        return result;
	}
    
    /**
     * 判断FTP上某目录是否存在
     *
     * @param pathname
     *            要判断的路径(文件名全路径、文件夹全路径都可以)
     *            注:此路径应从根目录开始
     * @DATE 2018年9月27日 上午11:24:25
     */
    private boolean destDirExist(String pathname) throws IOException{
    	boolean result = true;
	    try {
	    	// 初始化时,当前session位置即为 “/”
	    	initFtpClient();
	    	if (!pathname.startsWith("/")) {
	    		pathname = "/" + pathname;
	    	}
	    	if (pathname.lastIndexOf(".") >= 0) {
	    		int index = pathname.lastIndexOf("/");
	    		if (index != 0) {
	    			pathname = pathname.substring(0, index);
	    		} else {
	    			return true;
	    		}
	    	}
	    	result = ftpClient.changeWorkingDirectory(pathname);
	    }finally{
	    	// 先登出、再关闭连接
	    	ftpClient.logout();
	        if(ftpClient.isConnected()){ 
	            ftpClient.disconnect();
	        }
	    }
	    return result;
	}
}
