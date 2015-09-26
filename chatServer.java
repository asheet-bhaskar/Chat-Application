import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class  chatServer {
  Vector<String> users = new Vector<String>();
  Vector<HandleClient> clients = new Vector<HandleClient>();

  public void process() throws Exception  {
      ServerSocket server = new ServerSocket(9999);
      out.println("Server Started...");
      while( true) {
 		 Socket client = server.accept();
 		 HandleClient c = new HandleClient(client);
  		 clients.add(c);
     }  // end of while
  }
  public static void main(String ... args) throws Exception {
      new chatServer().process();
  } // end of main

  public void unicast(String user, String dst, String message)  {
	    // send message to all connected users
	    for ( HandleClient c : clients )
	       if ( c.getUserName().equals(dst) )
	          c.sendMessage(user,message);
  }
 
  public void broadcast(String user, String message)  {
	    // send message to all connected users
	    for ( HandleClient c : clients )
	       if ( ! c.getUserName().equals(user) )
	          c.sendMessage(user,message);
}
  class  HandleClient extends Thread {
    String name = "";
	BufferedReader input;
	PrintWriter output;

	public HandleClient(Socket  client) throws Exception {
         // get input and output streams
	 input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
	 output = new PrintWriter ( client.getOutputStream(),true);
	 // read name
	 name  = input.readLine();
	 users.add(name); // add to vector
	 start();
        }

        public void sendMessage(String uname,String  msg)  {
	    output.println( uname + ":" + msg);
	}
		
        public String getUserName() {  
            return name; 
        }
        public void run()  {
    	     String line;
	     try    {
                while(true)   {
		 line = input.readLine();
		 if ( line.equals("end") ) {
		   clients.remove(this);
		   users.remove(name);
		   break;
                 }
		 String dst="",msg="";
		 
		 int i,flag=0;
		 for(i=0;i<line.length();i++){
			 if(line.charAt(i) !=':'){
				 dst=dst+line.charAt(i);
			 }
			 else{
				 flag=1;
				 break;
			 }
		 }
		 for(;i<line.length();i++){
			msg=msg+line.charAt(i);
		 }
		 if(flag==1){
			 unicast(name,dst,msg);
		 }
		 else{
			 broadcast(name,line);
		 }
		  // method  of outer class - send messages to all
	       } // end of while
	     } // try
	     catch(Exception ex) {
	       System.out.println(ex.getMessage());
	     }
        } // end of run()
   } // end of inner class

} // end of outer class.
