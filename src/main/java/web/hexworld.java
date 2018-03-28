package web;

import application.OnlineClientMain;

public class hexworld {

	public static void main(String[] args) {
		if(args.length==0)
		{
				OnlineClientMain newClientGUI = new OnlineClientMain();
				newClientGUI.run(args);
		}
		else if(args.length==4)
		{
			int port = Integer.parseInt(args[0]);
			String readpassword = args[1];
			String writepassword = args[2];
			String adminpassword = args[3];
			myServer newServer = new myServer(port,readpassword,writepassword,adminpassword);
			newServer.run();
		}
		
		
		
		
	}

}
