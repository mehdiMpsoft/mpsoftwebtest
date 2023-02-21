package com.GenAutoTest.prg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CnxDataBase {

	public Connection connectedC;


	 public void setUpDataBase() throws ClassNotFoundException, SQLException {
 
	String url = "jdbc:oracle:thin:@//192.168.0.117:1521/mpbank";
	String user = "tsa";
	String password = "tsa";
	
	
		Class.forName("oracle.jdbc.OracleDriver");
		Connection connected = DriverManager.getConnection(url, user, password);
		connectedC = connected ;
		
		System.out.println("Connection is successful with" + " " + connectedC);
		


}

}
