package servlets.challenges;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

import utils.ShepherdExposedLogManager;
import dbProcs.Database;

/**
 * Bad Crypto Challenge Three
 * Really bad crypto algorithm to break. Will reveal key if spaces are submitted
 * <br/><br/>
 * This file is part of the Security Shepherd Project.
 * 
 * The Security Shepherd project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.<br/>
 * 
 * The Security Shepherd project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br/>
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Security Shepherd project.  If not, see <http://www.gnu.org/licenses/>. 
 * @author Mark Denihan
 *
 */
public class BrokenCrypto3 extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(BrokenCrypto3.class);
	private static String levelName = "Broken Crypto Challenge 3";
	private static String levelHash = "2da053b4afb1530a500120a49a14d422ea56705a7e3fc405a77bc269948ccae1";
	public static String levelResult = "thisisthesecurityshepherdabcencryptionkey"; //Is used as encryption key in this level

	public void doPost (HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException
	{
		//Setting IpAddress To Log and taking header for original IP if forwarded from proxy
		ShepherdExposedLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
		
		//Attempting to recover user name of session that made request
		try
		{
			if (request.getSession() != null)
			{
				HttpSession ses = request.getSession();
				String userName = (String) ses.getAttribute("decyrptedUserName");
				log.debug(userName + " accessed " + levelName + " Servlet");
			}
		}
		catch (Exception e)
		{
			log.debug(levelName + " Servlet Accessed");
			log.error("Could not retrieve user name from session");
		}
		
		PrintWriter out = response.getWriter();
		out.print(getServletInfo());
		String htmlOutput = new String();
		try
		{
			String userData = request.getParameter("userData");
			log.debug("User Submitted - " + userData);
			
			log.debug("Decrypting user input");
			//Using level key as encryption key
			String decryptedUserData = decrypt(userData, levelResult);
			log.debug("Decrypted to: " + decryptedUserData);
			Encoder encoder = ESAPI.encoder();
			htmlOutput = "<h2>Plain text Result:</h2><p>Your cipher text was decrypted to the following:<br/><br/><em>"
					+ encoder.encodeForHTML(decryptedUserData)
					+ "</em></p>";
		}
		catch(Exception e)
		{
			htmlOutput = "An Error Occurred! You must be getting funky!";
			log.fatal(levelName + " - " + e.toString());
		}
		out.write(htmlOutput);
	}
	
	/**
	   * Decrypts the supplied string value using the submitted key
	   * @param hash The cipher text to be decrypted
	   * @param key The encryption key
	   * @return The plain text revealed from the decryption
	   * @throws Exception Throws illegal state Exception
	   */
	  public static String decrypt(String hash, String key) throws Exception 
	  {
	    try 
	    {
	    	return new String(xor(org.apache.commons.codec.binary.Base64.decodeBase64(hash.getBytes()), key), "UTF-8");
	    } 
	    catch (java.io.UnsupportedEncodingException ex) 
	    {
	      throw new IllegalStateException(ex);
	    }
	  }
	  
	  /**
	   * XOR Function
	   * @param input Byte array to be XOR'd
	   * @param key Encryption Key
	   * @return
	   */
	  private static byte[] xor(final byte[] input, String theKey) 
	  {
	    final byte[] output = new byte[input.length];
	    final byte[] secret = theKey.getBytes();
	    int spos = 0;
	    for (int pos = 0; pos < input.length; pos += 1) 
	    {
	      output[pos] = (byte) (input[pos] ^ secret[spos]);
	      spos += 1;
	      if (spos >= secret.length) {
	        spos = 0;
	      }
	    }
	    return output;
	  }
}