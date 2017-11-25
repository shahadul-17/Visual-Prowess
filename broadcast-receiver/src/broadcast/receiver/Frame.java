package broadcast.receiver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Frame extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 5051337286018857204L;
	
	private JPanel contentPane, panelControls;
	private ImageView imageView;
	private JButton buttonStart, buttonStop;
	
	private BroadcastReceiver broadcastReceiver;
	
	public Frame() throws Exception {
		initialize();
		
		broadcastReceiver = new BroadcastReceiver(imageView);
	}
	
	private void initialize() {
		setTitle(Main.TITLE);
		setSize(400, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(this);
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		imageView = new ImageView();
		imageView.setBackground(Color.BLACK);
		contentPane.add(imageView, BorderLayout.CENTER);
		
		panelControls = new JPanel();
		panelControls.setBackground(Color.WHITE);
		((FlowLayout) panelControls.getLayout()).setAlignment(FlowLayout.RIGHT);
		panelControls.setPreferredSize(new Dimension(0, 36));
		contentPane.add(panelControls, BorderLayout.SOUTH);
		
		buttonStart = new JButton("Start");
		buttonStart.addActionListener(this);
		panelControls.add(buttonStart);
		
		buttonStop = new JButton("Stop");
		buttonStop.setEnabled(false);
		buttonStop.addActionListener(this);
		panelControls.add(buttonStop);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton tempButton, button = (JButton) event.getSource();
		
		if (event.getSource().equals(buttonStart)) {
			tempButton = buttonStop;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					broadcastReceiver.start();
				}
			}).start();
		}
		else {
			tempButton = buttonStart;
			
			broadcastReceiver.stop();
		}
		
		button.setEnabled(false);
		tempButton.setEnabled(true);
	}

	@Override
	public void windowActivated(WindowEvent event) {
		/*
		 * nothing to be implemented here... 
		 */
	}

	@Override
	public void windowClosed(WindowEvent event) {
		/*
		 * nothing to be implemented here... 
		 */
	}

	@Override
	public void windowClosing(WindowEvent event) {
		broadcastReceiver.stop();
	}

	@Override
	public void windowDeactivated(WindowEvent event) {
		/*
		 * nothing to be implemented here... 
		 */
	}

	@Override
	public void windowDeiconified(WindowEvent event) {
		/*
		 * nothing to be implemented here... 
		 */
	}

	@Override
	public void windowIconified(WindowEvent event) {
		/*
		 * nothing to be implemented here... 
		 */
	}

	@Override
	public void windowOpened(WindowEvent event) {
		/*
		 * nothing to be implemented here... 
		 */
	}
	
}