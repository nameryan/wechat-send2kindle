package com.using.weixin.common;

import com.baidu.bae.api.util.BaeEnv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Level;


public class JdbcTools{
  private Logger logger = Logger. getLogger("JdbcTools"); 
  
  private String host = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_IP);
  private String port = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_PORT);
  private String username = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_AK);
  private String password = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
  private String driverName = "com.mysql.jdbc.Driver";
  private String dbUrl = "jdbc:mysql://";
  private String serverName = host + ":" + port + "/";
 
  //从平台查询应用要使用的数据库名
  private String databaseName = "FlquaCcvfOUnkWVKSBqA";
  final private String TB_NAME_INFO = "wechat_info";
  final private String TB_NAME_LOG = "wechat_log";
  private String connName = dbUrl + serverName + databaseName;
  
  public JdbcTools(){
  }
  
  public int insertUserInfo(String userId,String email){
    logger.log(Level.INFO, "insertUserInfo:userId="+userId+" email="+email);
	Connection connection = null;
	Statement stmt = null;
	ResultSet rs = null;
    int count = 0;
	try {
		Class.forName(driverName);
                //具体的数据库操作逻辑
		connection = DriverManager.getConnection(connName, username,
				password);
		//stmt = connection.createStatement();
      
      String sql = "INSERT INTO "+TB_NAME_INFO+"(user_id, email, timestamp)"+" VALUES (?,?,?)";
      
//       String sql = "INSERT INTO staff(name, age, sex,address, depart, worklen,wage)"  
//                    + " VALUES ('Tom1', 32, 'M', 'china','Personnel','3','3000')";  // 插入数据的sql语句  
               
      java.util.Date date=new java.util.Date();
      Timestamp tt=new Timestamp(date.getTime());
      
      PreparedStatement pstmtInsert = connection.prepareStatement(sql);
      pstmtInsert.setString(1,userId);
      pstmtInsert.setString(2,email);
      pstmtInsert.setTimestamp(3,tt);
      count = pstmtInsert.executeUpdate();
//            count = stmt.executeUpdate(sql);  // 执行插入操作的sql语句，并返回插入数据的个数
		 
	} catch (ClassNotFoundException ex) {
		// 异常处理逻辑
      //throw ex;
      logger.log(Level.INFO, "classNotFoundException e="+ex);
	} catch (SQLException e) {
		// 异常处理逻辑
 		//throw e;
            logger.log(Level.INFO, "SQLException e="+e);
	} finally {
		try {
			if (connection != null) {
				connection.close();
              logger.log(Level.INFO, "connection.close()");
			}
          connection = null;
	    } catch (SQLException e) {
 			//throw e;
          logger.log(Level.INFO, "SQLException e="+e);
			}
	}
    
    return count;
  }
 
  public String queryUserEmail(String userId){
    logger.log(Level.INFO, "queryUserEmail:userId="+userId);
	Connection connection = null;
	Statement stmt = null;
	ResultSet rs = null;
    String email = null;
	try {
		Class.forName(driverName);
                //具体的数据库操作逻辑
		connection = DriverManager.getConnection(connName, username,
				password);
		stmt = connection.createStatement();
      
       String sql = "select * from "+TB_NAME_INFO;
      
       rs = stmt.executeQuery(sql);
		 
      while (rs.next()) {			
        if (rs.getString("user_id").equals(userId)){
           email= rs.getString("email");
          //logger.log(Level.INFO, "find the right email address = "+email);
        }
		}
      
	} catch (ClassNotFoundException ex) {
		// 异常处理逻辑
      logger.log(Level.INFO, "classNotFoundException e="+ex);
	} catch (SQLException e) {
		// 异常处理逻辑
            logger.log(Level.INFO, "SQLException e="+e);
	} finally {
		try {
			if (connection != null) {
				connection.close();
               logger.log(Level.INFO, "connection.close()");
			}
          connection = null;
	    } catch (SQLException e) {
          logger.log(Level.INFO, "SQLException e="+e);
			}
	}
    logger.log(Level.INFO, "find the right email address = "+email);
    return email;
  }
  
  public int insertUserLog(String userId,String email, String url, int ret){
    logger.log(Level.INFO, "insertUserLog:userId="+userId+" email="+email+" url="+url);
	Connection connection = null;
	Statement stmt = null;
	ResultSet rs = null;
    int count = 0;
     
	try {
		Class.forName(driverName);
                //具体的数据库操作逻辑
		connection = DriverManager.getConnection(connName, username,
				password);
		//stmt = connection.createStatement();
      
      String sql = "INSERT INTO "+TB_NAME_LOG+"(timestamp,ret,user_id,email, url)"+" VALUES (?,?,?,?,?)";
      
      java.util.Date date=new java.util.Date();
      Timestamp tt=new Timestamp(date.getTime());
      
      PreparedStatement pstmtInsert = connection.prepareStatement(sql);
      pstmtInsert.setTimestamp(1,tt);
      pstmtInsert.setInt(2,ret);
      pstmtInsert.setString(3,userId);
      pstmtInsert.setString(4,email);
      pstmtInsert.setString(5,url);
      count = pstmtInsert.executeUpdate();
	} catch (ClassNotFoundException ex) {
		// 异常处理逻辑
      logger.log(Level.INFO, "classNotFoundException e="+ex);
	} catch (SQLException e) {
		// 异常处理逻辑
            logger.log(Level.INFO, "SQLException e="+e);
	} finally {
		try {
			if (connection != null) {
				connection.close();
              logger.log(Level.INFO, "connection.close()");
			}
          connection = null;
	    } catch (SQLException e) {
 			//throw e;
          logger.log(Level.INFO, "SQLException e="+e);
			}
	}
    return count;
  }
  
  public void readMySql(){
   String sql = "select * from "+TB_NAME_INFO;
    
	Connection connection = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
		Class.forName(driverName);
                //具体的数据库操作逻辑
		connection = DriverManager.getConnection(connName, username,
				password);
		stmt = connection.createStatement();
		rs = stmt.executeQuery(sql);
        int id = 0;
		String userId = "" , email ="";
      //out.println("id&nbsp;&nbsp;&nbsp;&nbsp;name<br/>");
        
		while (rs.next()) {
			id = rs.getInt("_id");
			userId = rs.getString("user_id");
            email= rs.getString("email");
          //out.println(id + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + name + "<br/>");
            logger.log(Level.INFO, "_id ="+id+" userId="+userId+" email="+email);
		}
	} catch (ClassNotFoundException ex) {
		// 异常处理逻辑
      //throw ex;
	} catch (SQLException e) {
		// 异常处理逻辑
 		//throw e;
	} finally {
		try {
			if (connection != null) {
				connection.close();
			}
	    } catch (SQLException e) {
 			//throw e;
			}
	}
  }
}