package com.using.weixin.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.bae.api.util.BaeEnv;
import java.io.File;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 网页抓取
 * @author 胡阳
 * @blog http://www.the5fire.com
 *
 */
public class WebGet {
	private String myUrl;
	private HttpURLConnection con;
	private StringBuilder contextAll = new StringBuilder("");
    private Logger logger = Logger. getLogger("WebGet"); 

	private int pageCount = 0;
	private String pageType = "";
	public WebGet() {

	}

	public WebGet(String url) {
		this.myUrl = url;
	}
	
	public WebGet(String url,int pageCount,String pageType) {
		this.myUrl = url;
		this.pageCount = pageCount;
		this.pageType = pageType;
	}

	/**
	 * 正则表达式
	 * */
	public String regex() {
		String googleRegex = "";
		return googleRegex;
	}

	public void init(String url, String page) throws IOException {
		this.myUrl = "http://www.tianyabook.com/qita/hougeixue/";
		this.init(page);
	}

	public void init(String page) throws IOException {
		if (myUrl != null && !myUrl.equals("")) {
			URL urlmy = new URL(myUrl + page + ".html");
			con = (HttpURLConnection) urlmy.openConnection();
			con.setFollowRedirects(true);
			con.setInstanceFollowRedirects(false);
			con.connect();
		}
	}
  

	/**
	 * 写字符串中数据到txt文件
	 * @param context
	 * @return
	 * @throws IOException
	 */
	public String writeTxt(String context,String fileName) throws IOException {
      logger.log(Level.INFO, "writeTxt filename="+fileName);
      String filePath = BaeEnv.getTmpfsPath()+fileName;
      File file=new File(filePath);      
      file.deleteOnExit();
      
      OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(
        filePath));
      osw.write(context, 0, context.length());
      osw.flush();
      osw.close();
      
      return filePath;
	}
	
	/**
	 * 获得网页内容，要指定编码格式
	 * @param codeType GB2312/UTF-8/……
	 * @return
	 * @throws IOException 
	 * @throws  
	 */
	public String getContent(String codeType) throws IOException{
		if(pageCount < 1){
			return "null";
		}
		//System.out.println("开始抓取内容。。。。。");
        
		//for (int i = 1; i < pageCount; i++) {
			//System.out.println("抓取第 " + i + "页");
			//this.init(String.valueOf(i));
      
      if (myUrl != null && !myUrl.equals("")) {
			URL urlmy = new URL(myUrl);
			con = (HttpURLConnection) urlmy.openConnection();
			con.setFollowRedirects(true);
			con.setInstanceFollowRedirects(false);
			con.connect();
				}
			BufferedReader br = new BufferedReader(new InputStreamReader(con
					.getInputStream(), codeType));
			String s = "";
			StringBuffer sb = new StringBuffer("");
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}

			String result = sb.toString();
			Pattern pattern = Pattern.compile(regex());
			Matcher matcher = pattern.matcher(result);

			while (matcher.find()) {
				String title = matcher.group().replaceAll("<.*?>", "")
						.replaceAll(" ", "");

				contextAll.append(title + "\n\t");
			}
			//System.out.println("完成：" + i + "页");
			//System.out.println("");
          logger.log(Level.INFO, "finished fetch .. contextAll ="+contextAll);
          
		//}

		return contextAll.toString();
	}

	public static String main(String[] args) throws IOException {

      String filePath = null;
      
		WebGet wg = new WebGet("http://www.tianyabook.com/qita/hougeixue/",227,"html");
		try {
			filePath = wg.writeTxt(wg.getContent("GB2312"),"wxtxt.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
      return filePath;
		
	}
}