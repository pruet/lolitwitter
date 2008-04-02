// Copyright (C) 2008 Pruet Boonma <pruetboonma@gmail.com>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  US


/**
 *	@author	Pruet Boonma <pruetboonma@gmail.com>
 *	@version 0.1.0, March 28, 08
 */

/* Based on Sun Microsystem Example code */

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.microedition.io.*;

class TwitterConnection {
	protected HttpConnection con;
	protected InputStream is;
	protected OutputStream os;
	protected String ua;
	protected String recenturl;
	protected String submiturl;
	protected String user;
	protected String password;
	protected static String lastUpdate = "";
	
	TwitterConnection(String u, String p) {
		user = u;
		password = p;
		ua = "Profile/" + System.getProperty("microedition.profiles") + " Configuration/" + System.getProperty("microedition.configuration");
		recenturl = "http://twitter.com/statuses/friends_timeline.xml";
		submiturl = "http://twitter.com/statuses/update.xml";
	}
	public static String URLencode(String s)
	{
		if (s!=null) {
			StringBuffer tmp = new StringBuffer();
			int i=0;
			try {
				while (true) {
					int b = (int)s.charAt(i++);
					if ((b>=0x30 && b<=0x39) || (b>=0x41 && b<=0x5A) || (b>=0x61 && b<=0x7A)) {
						tmp.append((char)b);
					}
					else {
						tmp.append("%");
						if (b <= 0xf) tmp.append("0");
						tmp.append(Integer.toHexString(b));
					}
				}
			}
			catch (Exception e) {}
			return tmp.toString();
		}
		return null;
	}
	
	protected void sendRequest(String msg, String _url) throws IOException {
		int status = -1;
		String url = _url;
		String auth = null;
		is = null;
		os = null;
		con = null;
		int len = msg.getBytes().length;
		if(len > 140) len = 140;
		while (con == null) {
			con = (HttpConnection)Connector.open(url);
			con.setRequestMethod(HttpConnection.POST);
			con.setRequestProperty("User-Agent", ua);
			String locale = System.getProperty("microedition.locale");
			if (locale == null) {
				locale = "th-TH";
			}
			con.setRequestProperty("Accept-Language", locale);
			con.setRequestProperty("Content-Length", "" + len);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Accept", "text/plain");
			if(!lastUpdate.equals("")) {
				//con.setRequestProperty("If-Modified-Since", lastUpdate);
			}
			if (user != null && password != null) {
				con.setRequestProperty("Authorization", "Basic " +  BasicAuth.encode(user, password));
			}

			os = con.openOutputStream();
			//System.out.println(url + ":" + msg + ":" + len);
			if(msg.length() > 0) {
				os.write(msg.getBytes());
			}
			os.close();
			os = null;

			status = con.getResponseCode();
			//System.out.println("responscode = " + status);
			switch (status) {
			case HttpConnection.HTTP_OK:
				break;
			case HttpConnection.HTTP_TEMP_REDIRECT:
			case HttpConnection.HTTP_MOVED_TEMP:
			case HttpConnection.HTTP_MOVED_PERM:
				url = con.getHeaderField("location");
				os.close();
				os = null;
				con.close();
				con = null;
				break;
			default:
				os.close();
				con.close();
				throw new IOException("Response status not OK:" + status);
			}
		}
		is = con.openInputStream();
	}

	protected void sendUpdate(String msg) throws IOException {
	
		StringBuffer stb = new StringBuffer();
		int ch = 0;
		try {
			sendRequest("status=" + msg, submiturl);
			lastUpdate = con.getHeaderField("Date");
			int n = (int)con.getLength();
			if(n != -1) {
				for(int i = 0; i < n; i++) {
					if((ch = is.read()) != -1) {
						stb.append((char) ch);
					}
				}
			} else {
				while((ch = is.read()) != -1) {
					n = is.available();
					stb.append((char) ch);
				}
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException ioe) {
				throw ioe;
			}
		}
		System.out.println("xxx" +  stb.toString());
	}

	protected String getUpdates()  throws IOException {
		StringBuffer stb = new StringBuffer();
		int ch = 0;
		try {
			sendRequest("", recenturl);
			lastUpdate = con.getHeaderField("Date");
			int n = (int)con.getLength();
			if(n != -1) {
				for(int i = 0; i < n; i++) {
					if((ch = is.read()) != -1) {
						stb.append((char) ch);
					}
				}
			} else {
				while((ch = is.read()) != -1) {
					n = is.available();
					stb.append((char) ch);
				}
			}
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException ioe) {
				throw ioe;
			}
		}
		return stb.toString();
	}
}
