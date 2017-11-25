package broadcast.receiver;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class BroadcastReceiver {
	
	private volatile boolean run = false;
	private byte[] buffer;
	
	private static final String[] COMMANDS = { "reset", "start", "stop" };
	
	private ImageView imageView;
	
	private ByteArrayOutputStream byteArrayOutputStream;
	private DatagramSocket datagramSocket;
	private DatagramPacket[] datagramPackets;
	
	public BroadcastReceiver(ImageView imageView) throws Exception {
		buffer = new byte[Main.BUFFER_LENGTH];
		datagramPackets = new DatagramPacket[3];
		
		this.imageView = imageView;
		byteArrayOutputStream = new ByteArrayOutputStream(Main.BUFFER_LENGTH);
		
		for (int i = 0; i < datagramPackets.length; i++) {
			switch (i) {
			case 0:
				datagramPackets[i] = new DatagramPacket(buffer, 0, buffer.length, InetAddress.getByName(Main.hostName), Main.PORT);
				
				break;
			case 1:
			case 2:
				datagramPackets[i] = new DatagramPacket(COMMANDS[i].getBytes(), 0, COMMANDS[i].length(), InetAddress.getByName(Main.hostName), Main.PORT);
				
				break;
			default:
				break;
			}
		}
	}
	
	public void start() {
		if (!run) {
			try {
				datagramSocket = new DatagramSocket();		// creating datagram-socket might throw exception...
				datagramSocket.setSoTimeout(Main.SOCKET_TIMEOUT);
				datagramSocket.send(datagramPackets[1]);
				
				byteArrayOutputStream.reset();
				
				run = true;
				
				while (run) {
					try {
						datagramSocket.receive(datagramPackets[0]);
						
						if (datagramPackets[0].getLength() > 0) {
							if (datagramPackets[0].getLength() == COMMANDS[0].length()) {
								String response = new String(datagramPackets[0].getData(), datagramPackets[0].getOffset(), datagramPackets[0].getOffset() + datagramPackets[0].getLength());
								
								if (response.equals(COMMANDS[0])) {
									try {
										imageView.updateImage(byteArrayOutputStream.toByteArray());
									}
									catch (Exception exception) {
										exception.printStackTrace();
									}
									
									byteArrayOutputStream.reset();
									
									System.gc();		// calling garbage collector...
								}
							}
							else {
								byteArrayOutputStream.write(datagramPackets[0].getData(), datagramPackets[0].getOffset(), datagramPackets[0].getOffset() + datagramPackets[0].getLength());
							}
						}
					}
					catch (SocketTimeoutException socketTimeoutException) {
						Thread.sleep(100);
						
						datagramSocket.send(datagramPackets[1]);		// sends "start"...
					}
					catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				
				datagramSocket.send(datagramPackets[2]);		// sends "stop"...
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
			
			datagramSocket.close();
		}
	}
	
	public void stop() {
		if (run) {
			run = false;
		}
	}
	
}