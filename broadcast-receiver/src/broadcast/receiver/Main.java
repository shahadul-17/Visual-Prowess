package broadcast.receiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JOptionPane;

public class Main {
	
	public static final int BUFFER_LENGTH = 1024, PORT = 60499, SOCKET_TIMEOUT = 10000;
	
	public static String hostName;
	public static final String TITLE = "Broadcast Receiver";
	
	private static Frame frame;
	
	public static void main(String[] args) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/shahadul-17/Smart-Glass/master/host-name.txt").openStream()));
			
			while ((hostName = bufferedReader.readLine()) == null) {
				/*
				 * wait until "hostName" is not null...
				 */
			}
			
			bufferedReader.close();
			
			frame = new Frame();
			frame.setVisible(true);
		}
		catch (Exception exception) {
			JOptionPane.showMessageDialog(frame, exception.getStackTrace(), TITLE, JOptionPane.ERROR_MESSAGE);
		}
	}
	
}