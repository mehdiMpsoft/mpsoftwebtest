package com.GenAutoTest.prg;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;


public class operations {
	public static WebDriver driver = null; 
	String url="";
	long nseqXSR=0;
	long nseqXSN=0;
	
	public void setauh(String urlMp,String matricule) throws IOException, InterruptedException { 
		  
		System.setProperty("webdriver.chrome.driver", "D:\\springIDE\\selenium webdriver\\ChromeDriver\\chromedriver_win32\\chromedriver.exe");

		driver = new ChromeDriver();
	
		driver.get(urlMp);
		
		driver.manage().window().maximize();
		String url=urlMp.substring(7);
		driver.get("http://"+matricule+":"+matricule + "@" + url);	
    } 
	
	
	@Test(priority = 1)
	public void testOpe() throws ClassNotFoundException, SQLException, IOException, InterruptedException {
		System.out.println("---------------------------------------------------------------------------------");
		CnxDataBase datab = new CnxDataBase();
	    datab.setUpDataBase();
		Connection connect = datab.connectedC;
		Statement stmt0=null;
		Statement stmt1=null;
		Statement stmt2=null;
		ResultSet resulRe0=null;
		ResultSet resulRe1=null;
		CallableStatement csPrm = null;
		String plt="WEBLOGIC";
		//String snr="SNRCPT01";
		String snr="RU01";
		String Pplt="P_CODPLT";
		String Psnr="P_CODSNR";
		String Ptrss="P_TYPRSS";
		String mod="";
		String rss="";
		String mat="17001";
		
     
		String sbPrm1 ="{call MP.setPRM('"+Psnr+"','"+snr+"')}";
		String sbPrm2 ="{call MP.setPRM('"+Pplt+"','"+plt+"')}";
		String sbPrmX="";
		
		try {
		csPrm = connect.prepareCall(sbPrm1);
        csPrm.execute();
        csPrm.close();
        csPrm = connect.prepareCall(sbPrm2);
        csPrm.execute();
        csPrm.close();
        
		stmt0 = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		stmt1 = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		stmt2= connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		resulRe0 = stmt0.executeQuery("select geturltsa(mp.getprm('"+Pplt+"')) as url from dual");
		//-------------------Ajout recuperation N°sequence XSN
		nseqXSN=getSequence(connect,"SEQ_XSN");
		
		if (resulRe0.next()) {
			url=resulRe0.getString("URL");
		System.out.println("url :"+url);
		setauh(url,mat);
		resulRe0.close();
		}
		resulRe0 =stmt2.executeQuery("select * from vSNRSMR");
		while (resulRe0.next()) {
			
			nseqXSR=getSequence(connect,"SEQ_XSR");
			rss = resulRe0.getString("TYPRSSSMR");
			
			sbPrmX ="{call MP.setPRM('"+Ptrss+"','"+rss+"')}";
			
			System.out.println("typ rss++++++++++++++++++++++++++="+sbPrmX);
			csPrm = connect.prepareCall(sbPrmX);
	        csPrm.execute();
	        csPrm.close();
	        String codsnr = resulRe0.getString("CODSNR");
			String operation = resulRe0.getString("CODRSSSMR");
			System.out.println("ope+++++++"+operation);
			sbPrmX ="{call MP.setPRM('P_CODRSS','"+operation+"')}";
			System.out.println("ope param +++++++"+sbPrmX);
			csPrm = connect.prepareCall(sbPrmX);
	        csPrm.execute();
	        csPrm.close();
	        mod =resulRe0.getString("MODSMR");
	        
	        sbPrmX ="{call MP.setPRM('P_MODSMR','"+mod+"')}";
	        csPrm = connect.prepareCall(sbPrmX);
	        csPrm.execute();
	        csPrm.close();
	        System.out.println("ope snr +++++++"+sbPrmX);
	        System.out.println("url snr +++++++"+url + operation +"&CODMAJ="+mod);
	        
	       driver.navigate().to(url + operation +"&CODMAJ="+mod);
	       
			resulRe1 =stmt1.executeQuery("select * from VVALTST_test");
			
			traiterXSD(connect,resulRe1);
			//------------------------------------------------------------------------------------------------------

		
			//------------------------------------------------------------------------------------------------------
			
			insertXSR(connect, nseqXSR, nseqXSN,codsnr , rss,operation , mod );
			System.out.println("------------------------77777777777777777777777777777------------------------------");
		}
		//insertXSN(connect );
        
        }
        catch(SQLException e){
        	
        	
        }
        finally {
        	if (stmt0 != null) stmt0.close();
        	   if (resulRe0 != null) resulRe0.close();
        }
	}
	
	//methode 01
	private void traiterXSD(Connection connect,ResultSet resulRe1) throws SQLException
	{
		while (resulRe1.next()) {
			System.out.println("------------------------33333333333333333333333------------------------------");
			String codsnr=resulRe1.getString("CODSNRSMR");
			String typrss=resulRe1.getString("TYPRSSSMR");
			String codrss=resulRe1.getString("CODRSSSMR");
			String modsnr=resulRe1.getString("MODSMR");
			String codrub=resulRe1.getString("CODRUBRSD");
			String ordxsd=resulRe1.getString("ORDRSD");
			String sttxsd=resulRe1.getString("STTRSD");
			long nseqXSD=getSequence(connect,"SEQ_XSD");
			String tmsgerr=resulRe1.getString("MSGRSD");
			String msgree="";
			String idtab = resulRe1.getString("TABOPE");
			System.out.println("id tabzone+++++:"+idtab);
			String value = resulRe1.getString("VALOPE");
			System.out.println("value+++++:"+value);
			String testList = resulRe1.getString("TYPLSTZON");
	    	 System.out.println("nvl liste ----------" + testList );
	    	 	
	    	 		
	    	 	
	    	 if(testList.equals("1")) {
	    		 value = resulRe1.getString("VALLSTOPE");
	    		 System.out.println("valrubXXXXXXXXXXXXXX----------" + value );
	    		 }
	    	 
	    	 
	    	 if (resulRe1.getString("CARZON").equals("X")) {
	    		 System.out.println("condition" );
					   System.out.println("condition2" );
					}else {
						String text=driver.findElement(By.id(idtab)).getText();
						if(!resulRe1.getString("TYPZON").equals("B")) {	
							System.out.println("id tabzone AFTER CLEAR+++++:"+idtab+text+"+++++++valeur +"+value);
							
                                if (!driver.findElement(By.id(idtab)).equals(null)) {
								driver.findElement(By.id(idtab)).sendKeys(Keys.CONTROL + "a");
								driver.findElement(By.id(idtab)).sendKeys(Keys.BACK_SPACE);
								Utile.threadTime(1000);
								driver.findElement(By.id(idtab)).sendKeys(Keys.TAB);
							}
							
						}
						
						driver.findElement(By.id(idtab)).sendKeys(value);
						driver.findElement(By.id(idtab)).sendKeys(Keys.TAB);
						Utile.threadTime(1000);
						
					}
	    	msgree = driver.findElement(By.id("divChg")).getText();
			 System.out.println("message d'erreur ResultXXX---> : " + msgree +"   tra msg ="+tmsgerr);
			///------------------------
			 // si ind = (j'ai un message d'erreur)
			 //       recuperation msgerr
			 //       comparaison msgerr avec msgree
			 //       si egalité
			 //             rien à faire
			 //       sinon
			 //            anomalie
			 // sinon
			 //		 est ceque msgree non null 
			 //      		anomalie
			if(tmsgerr!=null) {
				if (tmsgerr.equals(msgree)) {
					System.out.println("true Message:");
				}else {
					System.out.println("Anomalie :");
				}
				
			}else {
				if(!(msgree.equals(""))) {
					System.out.println("false Message:");
				}
				
			}
			System.out.println("values a inserer :"+nseqXSD+"************"+nseqXSN+"************"+nseqXSR+"************"+codsnr+"************"+typrss+"************"+codrss+"************"+modsnr+"************"+codrub+"************"+ordxsd+"************"+value+"************"+sttxsd);
			
			insertXSD(connect,nseqXSD,nseqXSN,nseqXSR,codsnr,typrss,codrss,modsnr,codrub,ordxsd,value,sttxsd,msgree,tmsgerr);	
			
			driver.findElement(By.id("b5")).click();
			
			System.out.print("seqxsn val:"+nseqXSN +"    seqxsr val:"+nseqXSR);
		
	}
		}
	
	private int getSequence(Connection connect,String SEQ) throws SQLException
	{ System.out.println("seq1---> : ");
		Statement stmt=null;
		ResultSet resulRe=null;
		int val=0;
		stmt = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		resulRe = stmt.executeQuery("select "+SEQ+".nextval as val from dual");
		 System.out.println("seq+++++---> : ");
		while (resulRe.next()) {
				val = resulRe.getInt("VAL");
				System.out.println("seq1---> : "+val);
			}
		
			return val;
		
	}
	private void insertXSD(Connection connect ,long seqD,long seqN,long seqR,String codsnr,String typrss,String codrss,String modsnr,String codrub,String ordxsd,String val,String sttxsd,String msgxsd,String msrxsd) throws SQLException{
		// create a Statement from the connection
		PreparedStatement stmt=connect.prepareStatement("INSERT INTO XSD (NUMXSD, NUMXSRXSD, NUMXSNXSD,DSYSXSD,CODSNRXSD,TYPRSSXSD,CODRSSXSD,MODXSD,CODRUBXSD,ORDXSD,VALXSD, STTXSD,MSGXSD,MSRXSD) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");  
		int ordxsdn = Integer.parseInt(ordxsd);
		stmt.setLong(1,seqD);
		stmt.setLong(2,seqR);
		stmt.setLong(3,seqN);
		stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
		stmt.setString(5, codsnr);
		stmt.setString(6, typrss);
		stmt.setString(7, codrss);
		stmt.setString(8, modsnr);
		stmt.setString(9, codrub);
		stmt.setInt(10, ordxsdn);
		stmt.setString(11, val);
		stmt.setString(12, sttxsd);
		stmt.setString(13, msgxsd);
		stmt.setString(14, msrxsd);
		System.out.println("values a inserer v2 :"+seqD+"************"+seqN+"************"+seqR+"************"+codsnr+"************"+typrss+"************"+codrss+"************"+modsnr+"************"+codrub+"************"+ordxsd+"number :"+ ordxsdn+"************"+val+"************"+sttxsd);
		
		// insert the data
		stmt.executeUpdate();
		
		System.out.print("insertion dans xsd");
	}
	private void insertXSR(Connection connect ,long seqN,long seqR,String codsnrxsr,String typrssxsr,String codrssxsr,String modxsr) throws SQLException{
		// create a Statement from the connection
		PreparedStatement stmt=connect.prepareStatement("INSERT INTO XSR (NUMXSR,NUMXSNXSR,DSYSXSR,CODSNRXSR,TYPRSSXSR,CODRSSXSR,MODXSR) VALUES (?,?,?,?,?,?,?)"); 
		stmt.setLong(1,seqR);
		stmt.setLong(2,seqN);
		stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
		stmt.setString(4, codsnrxsr);
		stmt.setString(5, typrssxsr);
		stmt.setString(6, codrssxsr);
		stmt.setString(7, modxsr);
		System.out.println("values a inserer v2 :************"+seqN+"************"+seqR+"************"+codsnrxsr+"************"+typrssxsr+"************"+codrssxsr+"************"+modxsr+"************************");
		System.out.println("values a inserer v2 :++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		// insert the data
		stmt.executeUpdate();
		System.out.println("values a inserer v22222222222222 :++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.print("insertion dans xsr");
	}
	private void insertXSN(Connection connect )
	{
		System.out.print("insertion dans xsn");
	}
		
	
	
	//@AfterTest
	public void closeTesting() {
		driver.close();
	}
}
