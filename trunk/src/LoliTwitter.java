// Copyright (C) 2008 Pruet Boonma <pruetboonma@gmail.com>

//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA	02111-1307	US


/**
 *	@author	Pruet Boonma <pruetboonma@gmail.com>
 *	@version 0.1.0, March 28, 08
 */


// Standard MIDP library, please
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;
import nanoxml.kXMLElement;

public class LoliTwitter extends MIDlet implements CommandListener, ThaiListBoxCBInf
{
	protected Display display;
	protected Setting setting;
	protected ThaiPickBoard thaipickboard;
	protected ThaiListBox thailistbox;
	protected Displayable prevDisplay;

	protected Command exitCommand;

	protected Form submitForm;
	protected TextField updateTextField;
	protected Command submitCommand;
	protected Command updatesCommand;
	protected Command aboutCommand;
	protected Command clearCommand;
	protected Command backCommand;
	protected Command setupCommand;
	protected Command switchIMCommand;
	protected Command replyCommand;
	protected ChoiceGroup cg;
	
	protected Form setupForm;
	protected TextField timeoutTextField;
	protected TextField userTextField;
	protected TextField passwdTextField;
	protected TextField numUpdateTextField;
	protected ChoiceGroup keyTypeChoiceGroup;
	protected ChoiceGroup showKeyHintChoiceGroup;

//	protected TextBox thaitextbox;
	
	protected String tempString;
	private byte[] tempByteArray;
	
	public static final String TH_PREFIX = "te";
	public static final String EN_PREFIX = "et";

	public static final int IM_TEXTFIELD = 0;
	public static final int IM_PICKBOARD = 1;

	public static final int VIEW_TDIS = 0;
	public static final int VIEW_TBOX = 1;
	
	public static final int XML_OK = 0;
	public static final int XML_NO_USER = 1;
	public static final int XML_ERROR = 2;
	
	String recentUser[];
	String recentText[];

	public LoliTwitter()
	{
		display = Display.getDisplay(this);
	setting = new Setting("LoliTwitter");
	display = Display.getDisplay(this);
	createUI();
	}
	
	protected void createUI()
	{
		submitForm = new Form("LoliTwitter");
		setupForm = new Form("Setup");
		thaipickboard = new ThaiPickBoard();
		thailistbox = new ThaiListBox(this);
		prevDisplay = null;
	
		// All Command Button
		backCommand = new Command("Back", Command.BACK, 1);
		submitCommand = new Command("Update", Command.OK, 1);
		updatesCommand = new Command("Recent", Command.SCREEN, 1);
		clearCommand = new Command("Clear", Command.SCREEN, 3);
		switchIMCommand = new Command("Switch Input", Command.SCREEN, 4);
		aboutCommand = new Command("About", Command.SCREEN, 6);
		setupCommand = new Command("Setup", Command.SCREEN, 7);
		exitCommand = new Command("Exit", Command.STOP, 10);
		replyCommand = new Command("Reply", Command.OK, 1);
		// Eng->Thai From
		updateTextField = new TextField("What are you doing?", "", 140, TextField.ANY);
		submitForm.append(updateTextField);	

		submitForm.addCommand(submitCommand);
		submitForm.addCommand(updatesCommand);
		submitForm.addCommand(clearCommand);
		submitForm.addCommand(setupCommand);
		submitForm.addCommand(aboutCommand);
		submitForm.addCommand(exitCommand);
		submitForm.addCommand(switchIMCommand);
		submitForm.setCommandListener(this);
	
		// Setup Form
		String user = setting.get("user");
		if(user == null) {
			setting.set("user", "" );
			user = "";
		}
		userTextField = new TextField("Email", user, 30, TextField.ANY);
	
		String passwd = setting.get("passwd");
		if(passwd == null) {
			setting.set("passwd", "" );
			passwd = "";
		}
		passwdTextField = new TextField("Password", passwd, 10, TextField.PASSWORD);
	
		String nupdate = setting.get("nupdate");
		if(nupdate == null) {
			setting.set("nupdate", "5" );
			nupdate = "5";
		}
		numUpdateTextField = new TextField("# of Recent Msgs Shown", nupdate, 10, TextField.NUMERIC);	
	
		String[] key_types = {"QWERTY", "ABC-VERT", "ABC-HORI"};
		keyTypeChoiceGroup = new ChoiceGroup("Keyboard Type", ChoiceGroup.EXCLUSIVE, key_types, null );
		String keylayout = setting.get("keytype");
		int keyi = 0;
		if(keylayout != null) {
			keyi = Integer.parseInt(keylayout);
		} else {
			setting.set("keytype", "" + keyi);
			setting.get("keytype");
		}
		keyTypeChoiceGroup.setSelectedIndex(keyi, true);
	
		String[] show_hint = {"YES", "NO"};
		showKeyHintChoiceGroup = new ChoiceGroup("Show Key Hint", ChoiceGroup.EXCLUSIVE, show_hint, null);
		String showhint = setting.get("showhint");
		int showk = 0;
		if(showhint != null) {
			showk = Integer.parseInt(showhint);
		} else {
			setting.set("showhint", "" + showk);
			setting.get("showhint");
		}
		showKeyHintChoiceGroup.setSelectedIndex(showk, true);

		String timeout = setting.get("keytimeout");
		if(timeout == null) {
			timeout = "" + thaipickboard.getKeyRepeatTimeout();
			setting.set("keytimeout", timeout );
		}
		timeoutTextField = new TextField("Key Timeout", timeout, 10, TextField.NUMERIC);
	
		setupForm.append(userTextField);
		setupForm.append(passwdTextField);
		setupForm.append(numUpdateTextField);
		setupForm.append(keyTypeChoiceGroup);
		setupForm.append(timeoutTextField);
		setupForm.append(showKeyHintChoiceGroup);
		setupForm.addCommand(backCommand);
		setupForm.setCommandListener(this);
	
		thaipickboard.addCommand(submitCommand);
		thaipickboard.addCommand(updatesCommand);
		thaipickboard.addCommand(clearCommand);
		thaipickboard.addCommand(setupCommand);
		thaipickboard.addCommand(aboutCommand);
		thaipickboard.addCommand(switchIMCommand);
		thaipickboard.addCommand(exitCommand);
		thaipickboard.setCommandListener(this);

		String il = setting.get("input_lang");
		if(il == null) {
			setting.set("input_lang", "" + ThaiPickBoard.KEY_ENG);
		}

		thailistbox.addCommand(replyCommand);
		thailistbox.addCommand(backCommand);
		thailistbox.setCommandListener(this);

		thaipickboard.setKeyboardLayout(Integer.parseInt(setting.get("keytype")));
		thaipickboard.setShowKeyHint(Integer.parseInt(setting.get("showhint")));
		thaipickboard.setKeyRepeatTimeout(Integer.parseInt(setting.get("keytimeout")));
		thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
		thaipickboard.reset();

		String vm = setting.get("view_method");
		if(vm == null) {
			setting.set("view_method", "" + VIEW_TDIS);
		}
		String im = setting.get("input_method");
		if(im == null) {
			setting.set("input_method", "" + IM_TEXTFIELD);	
		}
		if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
			display.setCurrent(submitForm); 
		} else {
			display.setCurrent(thaipickboard);
		}
	}

	public void commandAction(Command command, Displayable displayable)
	{
		Runnable execute = new CommandExecuter(this, command, displayable);
		new Thread(execute).start();
	}

	public void handleCommandAction(Command command, Displayable displayable)
	{
	try {
		if (command.getCommandType() == Command.STOP) {
			setting.set("input_lang", "" + thaipickboard.getActiveLanguage());
			closeApp(false);
		} else if(command == switchIMCommand) {
			if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
				setting.set("input_method", "" + IM_PICKBOARD);
				thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
				thaipickboard.reset(ByteArray.convertFromSaraUm(ByteArray.convertFromString(updateTextField.getString())));
				display.setCurrent(thaipickboard);
			} else {
				setting.set("input_method", "" + IM_TEXTFIELD);
				updateTextField.setString(ByteArray.convertToString(ByteArray.convertToSaraUm(thaipickboard.getBytes())));
				display.setCurrent(submitForm);
			}
		} else if(command == aboutCommand) {
			StringBuffer sb = new StringBuffer();
			String version;
			String copyright;
			if((version = getAppProperty("MIDlet-Version")) == null) {
				version = new String("0.1.0A1");
			}
			if((copyright = getAppProperty("Copyright")) == null) {
				copyright = new String("2003-2008 Pruet Boonma, 2008 Sugree Phatanapherom");
			}
			sb.append("LoliTwitter " + version + " Copyright (C)" + copyright + " GPL Applied\n");
			sb.append("ThaiFontDisplay Copyright (C) 2002 Vuthichai Ampornaramveth <vuthi@vuthi.com> GPL Applied");
			prevDisplay = null;
			Alert aboutAlert = new Alert("LoliTwitter", sb.toString(), null, AlertType.INFO);
			aboutAlert.setTimeout(Alert.FOREVER);
			display.setCurrent(aboutAlert);
		 } else if(command == clearCommand) {
			updateTextField.setString(new String(""));
			thaipickboard.reset();
		 }
		if(displayable == submitForm) {
			if(command == submitCommand || command.getCommandType() == Command.OK) {
					updateStatus(ByteArray.convertFromSaraUm(ByteArray.convertFromString(updateTextField.getString())));
			 } else if(command == updatesCommand) {
				int ret;
				if((ret = retrieveRecent()) == XML_OK) {
					byte result[][]= new byte[recentText.length][200];
					for(int i = 0; i != recentText.length; i++) {
						result[i] = ByteArray.getBytes(recentUser[i] + ":" + recentText[i]);
						
					}
					thailistbox.displayText(result);
					display.setCurrent(thailistbox);
				} else if(ret == XML_NO_USER) {
					Alert noUserAlert = new Alert("Error", "Please setup your user/password first", null, AlertType.ERROR);
					noUserAlert.setTimeout(Alert.FOREVER);
					display.setCurrent(noUserAlert);
				} else {
				}
			 } else if(command == setupCommand) {
			 	display.setCurrent(setupForm);
			 }
		} else if(displayable == thaipickboard) {	
			if(command == submitCommand) {
				updateStatus(thaipickboard.getBytes());
			} else if(command == setupCommand) {
					display.setCurrent(setupForm);
			}	
		} else if(displayable == setupForm) {
			if(command == backCommand) {
				setting.set("user", userTextField.getString());
				setting.set("passwd", passwdTextField.getString());
				setting.set("nupdate", numUpdateTextField.getString());
				setting.set("keytype", Integer.toString(keyTypeChoiceGroup.getSelectedIndex()));
				setting.set("showhint", Integer.toString(showKeyHintChoiceGroup.getSelectedIndex()));
				setting.set("keytimeout", timeoutTextField.getString());
				if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
					display.setCurrent(submitForm);
				} else {
					thaipickboard.setKeyboardLayout(Integer.parseInt(setting.get("keytype")));
					thaipickboard.setShowKeyHint(Integer.parseInt(setting.get("showhint")));
					thaipickboard.setKeyRepeatTimeout(Integer.parseInt(setting.get("keytimeout")));
					thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
					thaipickboard.reset();
					display.setCurrent(thaipickboard);
				}
			}
		} else if(displayable == thailistbox) {
			if(command == replyCommand) {
				ThaiListBoxCommandActionCallBack(Canvas.RIGHT);
			} else if(command == backCommand) {
				BackFromThaiListbox();
			}
		}
	} catch(Exception e) {
		e.printStackTrace();
		Alert al = new Alert("Exception", e.getMessage(), null, AlertType.ERROR);
		al.setTimeout(Alert.FOREVER);
		display.setCurrent(al);
	}
	} 
	
	private void	updateStatus(byte[] status)
	{
		if(status == null || status.length <= 0) {
			return;
		}
		int ch;
		StringBuffer update = new StringBuffer();
		for(int i = 0; i != status.length; i++) {
			if(status[i]< 0) {
				ch = status[i] + 256;
				update.append("%26%23").append(new Integer(ch + 3424).toString()).append("%3B");
			} else {
				update.append((char)status[i]);
			}
		}
		if(update.length() > 0) {
			String user = setting.get("user");
			String passwd = setting.get("passwd");
			try {
				TwitterConnection h = new TwitterConnection(user, passwd);
				h.sendUpdate(update.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
				Alert aboutAlert = new Alert("LoliTwitter", ex.toString(), null, AlertType.ERROR);
				aboutAlert.setTimeout(Alert.FOREVER);
				display.setCurrent(aboutAlert);
				return;
			}
			updateTextField.setString(new String(""));
			thaipickboard.reset();
			return;
		}
	}
	
	private int retrieveRecent() {
		String user = setting.get("user");
		String passwd = setting.get("passwd");
		int nupdate = Integer.parseInt(setting.get("nupdate"));
		if(user == null || user.equals("") || passwd == null || passwd.equals("")) {
			return XML_NO_USER;
		}
		recentUser = new String[nupdate];
		recentText = new String[nupdate];
		TwitterConnection h = new TwitterConnection(user, passwd);

		// Set message to send, user, password
		String xml;
		try {
			xml = h.getUpdates();
		} catch(Exception ex) {
			return XML_ERROR;
		}
	//	System.out.println(xml);
		if (xml.length() == 0) {
			return XML_ERROR;
		} else {
			kXMLElement foo = new kXMLElement();
			foo.parseString(xml, 0);
			String tagContent = new String();
			StringBuffer out = new StringBuffer();
			Enumeration e = foo.enumerateChildren();
			Enumeration sc, uc;
			kXMLElement bar, baz, qux;
			int count = 0;
			while (e.hasMoreElements()) {
				bar = (kXMLElement)(e.nextElement());
				if(bar.getTagName().toLowerCase().equals("status")) {
					 sc = bar.enumerateChildren();
					 while(sc.hasMoreElements()) {
						baz = (kXMLElement)sc.nextElement();
						if(baz.getTagName().toLowerCase().equals("text")) {
							tagContent = baz.getContents();
							//System.out.println(tagContent);
						} else if(baz.getTagName().toLowerCase().equals("user")) {
							uc = baz.enumerateChildren();
							while(uc.hasMoreElements()) {
								qux = (kXMLElement)uc.nextElement();
								if(qux.getTagName().toLowerCase().equals("screen_name")) {
									recentUser[count] = qux.getContents();
									recentText[count] = Utils.replace(tagContent, "&amp;", "@");
									count++;
									if(count >= nupdate) return XML_OK;
								}
							}
						}
					 }
				}
			}
		}
		return XML_OK;
	}

	public void closeApp(boolean con)
	{
		destroyApp(con);
		notifyDestroyed();
	}
	
	public void startApp()
	{
			display.setCurrent(submitForm);

		// should we implement threading ? for pause/start buz
	}

	public void pauseApp()
	{
	}
	
	public void destroyApp(boolean con)
	{
	}
	
	public void ThaiListBoxCommandActionCallBack(int cmd)
	{
	if(cmd == Canvas.RIGHT) {
		//System.out.println("prefix search");
		updateTextField.setString("@" + recentUser[thailistbox.getSelectedEntry()] + " ");
		thaipickboard.reset(("@" + recentUser[thailistbox.getSelectedEntry()] + " ").getBytes());
	} 
	BackFromThaiListbox();
	}
	
	public void BackFromThaiListbox()
	{
	
	if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
		display.setCurrent(submitForm);
	} else {
		thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
		display.setCurrent(thaipickboard);
	}
	}

}
