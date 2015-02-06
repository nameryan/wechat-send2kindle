package com.using.weixin.common;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.baidu.bae.api.util.BaeEnv;
import java.util.List; 
import java.util.ArrayList;
import java.io.File;
import java.io.Writer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.URL;
import java.net.URLConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;  


public class JavaMail3 {
  private Logger logger = Logger. getLogger("JavaMail3"); 
  String contentAll= null;
  String contentTitle= null;
  
  public JavaMail3(){
  }
  
 /**
  * @param args
  */
  public boolean main(String emailAddr,String url) throws Exception{
    // TODO Auto-generated method stub   
    final String tto=emailAddr;//收件人地址
    final String ttitle="this is a kindle push mail, not spam！！";
    final String tcontent="r\n------------------------------------------------------------\r\n发送到Kindle推送服务，请关注微信公众号：发送到Kindle！";
    final String SMTP_SERVER = "smtp.qq.com";
    final String SMTP_ACCOUNT_NAME = "@qq.com";
    final String SMTP_ACCOUNT_PSW = "";
    final String bccEmail = "name.ryan@gmail.com";

      
    //获取应用私有的临时文件夹的绝对路径
    //String tmpfsPath = BaeEnv.getTmpfsPath();
    //String tfj="D:\\Downloads\\dbschema.sql";//附件内容
    //String tfj = testFile();
    
    String tfj = createFileOfUrl(url);
    if (tfj == null){
      return false;
    }
    /*
    WebGet wg = new WebGet(url);
		try {
			filePath = wg.writeTxt(wg.getContent("GB2312"),"wxtxt.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
    String tfj =  BaeEnv.getTmpfsPath()+"wxtxt.txt";
  */
    logger.log(Level.INFO, "tfj path="+tfj);
    
    Properties props=new Properties();
    props.put("mail.smtp.host", SMTP_SERVER);
    props.put("mail.smtp.auth","true");
    Session s=Session.getInstance(props);
    s.setDebug(true);
    
    MimeMessage message=new MimeMessage(s);
    
    //给消息对象设置发件人/收件人/主题/发信时间
    InternetAddress from=new InternetAddress(SMTP_ACCOUNT_NAME);
    message.setFrom(from);
    InternetAddress to=new InternetAddress(tto);
    message.setRecipient(Message.RecipientType.TO,to);
    
    //添加密送名单
    /*
    InternetAddress bccTo=new InternetAddress(bccEmail);
    message.setRecipient(Message.RecipientType.BCC, bccTo);
    */
    
    message.setSubject(ttitle);
    //message.setSubject(contentTitle);
    message.setSentDate(new Date());
    
    Multipart test=new MimeMultipart();//新建一个MimeMultipart对象用来存放多个BodyPart对象
    
    //设置信件文本内容
    BodyPart mdp=new MimeBodyPart();//新建一个存放信件内容的BodyPart对象
    mdp.setContent(tcontent,"text/html;charset=gb2312");//给BodyPart对象设置内容和格式/编码方式
    test.addBodyPart(mdp);//将含有信件内容的BodyPart加入到MimeMultipart对象中
    
    //设置信件的附件
    logger.log(Level.INFO, "start to set mail attachment!");
    mdp=new MimeBodyPart();
    FileDataSource fds=new FileDataSource(tfj);    
    DataHandler dh=new DataHandler(fds);    
    //int i=tfj.lastIndexOf("\\");
    //String fname=tfj.substring(i);//提取文件名
    String fname =contentTitle+".txt";
    //String fname = "test.txt";
    logger.log(Level.INFO, "fname="+fname);
    //mdp.setFileName(fname);//可以和原文件名不一致,但最好一样
    mdp.setFileName(MimeUtility.encodeWord(fname, "UTF-8",null)); 
    logger.log(Level.INFO, "encodedfname!!!");
    mdp.setDataHandler(dh);
    test.addBodyPart(mdp);
    
    message.setContent(test);//把mm作为消息对象的内容
    
    message.saveChanges();
    logger.log(Level.INFO, "start to transport!!!");
    Transport transport=s.getTransport("smtp");
    transport.connect(SMTP_SERVER,SMTP_ACCOUNT_NAME,SMTP_ACCOUNT_PSW);
    transport.sendMessage(message,message.getAllRecipients());
    transport.close();
    logger.log(Level.INFO, "邮件发送成功！！");
    return true;
  }

  //private String TMP_FILE_NAME = "mytxt4.txt";
  
  private String createFileOfUrl(String url)throws Exception{
    //String content = HttpRequestTools.getHttpClientHtml(url);
    String content = getContent(url);
    String filePath;
    //content = "1234567890";
    logger.log(Level.INFO, "content="+content);
    if (content == null || contentTitle == null){
      return null;
    }
    
    filePath = BaeEnv.getTmpfsPath()+contentTitle+".txt";
    //filePath = BaeEnv.getTmpfsPath()+TMP_FILE_NAME;
    File file=new File(filePath);
    logger.log(Level.INFO, "file path="+file);
    if (file.exists()){
      file.delete();
    }
    if (!file.exists()) {
      file.createNewFile();
    }
    
    Writer writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
    writer.write(content);
    writer.close();
    
    return filePath;
  }

  private String getContent(String strUrl){
    logger.log(Level.INFO, "start to getContent from "+strUrl);
    StringBuilder contextAll = new StringBuilder("");    
    List<String> newsList = null;
    URLConnection uc = null;
    //String all_content=null;
   
    try {
      //all_content =new  String();
      URL url = new URL(strUrl);

      uc = url.openConnection();
      String cType = uc.getContentType();
      //logger.log(Level.INFO, "contentType=="+cType);
      //uc.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)"); 
      //logger.log(Level.INFO, "after setRequestProperty");
      //System.out.println("-----------------------------------------");  
      //System.out.println("Content-Length:     "+uc.getContentLength());  
      //System.out.println("Set-Cookie:     "+uc.getHeaderField("Set-Cookie"));  
      //System.out.println("-----------------------------------------"); 
      //获取文件头信息
      //System.out.println("Header"+uc.getHeaderFields().toString());
      // System.out.println("-----------------------------------------");  
      if (uc == null){
        logger.log(Level.INFO, "fail to create connection");
        return null;
      }
                   

      /*
		InputStream ins = uc.getInputStream();
		ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
		byte[] str_b = new byte[1024];
                   int i = -1;
       				while ((i=ins.read(str_b)) > 0) {
                    outputstream.write(str_b,0,i);
                   }
                   all_content = outputstream.toString();
                  // System.out.println(all_content);
			*/
      
      BufferedReader br = new BufferedReader(new InputStreamReader(uc
                                                                   .getInputStream(), "UTF-8"));
      String s = "";
      StringBuffer sb = new StringBuffer("");
      while ((s = br.readLine()) != null) {
        //logger.log(Level.INFO, "s ="+s);
        sb.append(s);
      }

      contentAll = sb.toString();
      logger.log(Level.INFO, "contentAll= "+contentAll);
      
      /*
			Pattern pattern = Pattern.compile(regex());
			Matcher matcher = pattern.matcher(result);
			while (matcher.find()) {
				String title = matcher.group().replaceAll("<.*?>", "")
						.replaceAll(" ", "");
				contextAll.append(title + "\n\t");
			}
          logger.log(Level.INFO, "finished fetch .. contextAll ="+contextAll.toString());
          */
      //newsList = getNews(contentAll);
      contentTitle = getTitle(contentAll);
      contentAll = getPageContent(contentAll);
      

    } catch (Exception e) {
      e.printStackTrace();
      //log.error("获取网页内容出错");
      logger.log(Level.INFO, "获取网页内容出错 e="+e);
    }finally{
      uc = null;
    }
          
    // return new String(all_content.getBytes("ISO8859-1"));
    //System.out.println(all_content.length());
    //return all_content;
    //return contextAll.toString();
    return contentAll;
    //return newsList.get(0);
  }
  
  /**  
     *  
     * @param s  
     * @return 去掉标记  
     */  
    private String outTag(final String s) {  
        return s.replaceAll("<.*?>", "");  
    } 
   
  private String replaceHtml(final String s) {
        String tmpString;
        tmpString = s.replaceAll("<br  />", "\r\n");
        /* 过滤强回车换行: </p><p style="margin-top:8px;margin-bottom:8px;line-height:150%"> */
        tmpString = tmpString.replaceAll("</p>", "\r\n");
        return tmpString.replaceAll("&nbsp;", " ");
    } 
  
  /**  
     *  
     * @param s  
     * @return 获得网页标题  
     */  
  private String getTitle(String s) { 
    String regex;  
    String title = null;  
    List<String> list = new ArrayList<String>();  
    regex = "<title>.*?</title>";  
    Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);  
    Matcher ma = pa.matcher(s);  
    while (ma.find()) {  
      list.add(ma.group());  
    }  
    for (int i = 0; i < list.size(); i++) {  
      title = title + list.get(i);  
    }
    
    logger.log(Level.INFO, "getTitle="+title);
    return outTag(title);  
  }  
  
  private String getPageContent(String s){
    String regex;  
    String content = "";  
    List<String> list = new ArrayList<String>();  
    regex = "<div class=\"text\">.*?</div>";  
    Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);  
    Matcher ma = pa.matcher(s);  
    while (ma.find()) {  
      list.add(ma.group());  
    }  
    for (int i = 0; i < list.size(); i++) {  
      content = content + list.get(i);  
    }  
    return outTag(replaceHtml(content));  
  }
  
  private List<String> getNews(String s) {  
    logger.log(Level.INFO, "start to getNews ");
    String regex = "<a.*?</a>";  
    Pattern pa = Pattern.compile(regex, Pattern.DOTALL);  
    Matcher ma = pa.matcher(s);  
    List<String> list = new ArrayList<String>();  
    while (ma.find()) {  
      String tmps = ma.group();
      //logger.log(Level.INFO, "news ="+tmps.replaceAll("<.*?>", ""));
      list.add(outTag(tmps));
    }  
    return list;  
  }  
  
  private String testFile() throws Exception{    
    File file=new File(BaeEnv.getTmpfsPath()+"mytxt1.txt");
    logger.log(Level.INFO, "file path="+file);
    file.deleteOnExit();
    
    Writer writer = new OutputStreamWriter(new FileOutputStream(file));
    writer.write("01234567890123456789\n");
    writer.write("01234567890123456789\n");
    writer.write("01234567890123456789\n");
    writer.write("01234567890123456789\n");
    writer.write("01234567890123456789\n");
    writer.close();
    
    return (BaeEnv.getTmpfsPath()+"mytxt1.txt");
    //return putObjectByFile(file, "/txtFile/", "my.txt");
	}
}