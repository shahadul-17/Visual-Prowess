package broadcast.receiver;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageView extends JPanel {
	
	private static final long serialVersionUID = 6469412568010312648L;
	
	private Image image;
	
	private JFrame frame = null;
	
	public void updateImage(byte[] buffer) {
		try {
			image = new ImageIcon(buffer).getImage();
			
			paintComponents(getGraphics());
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void paintComponents(Graphics graphics) {
		int preferredFrameWidth = image.getWidth(null) + 6, preferredFrameHeight = image.getHeight(null) + 65;
		
		if (frame == null) {
			frame = (JFrame) getParent().getParent().getParent().getParent();
		}
		
		if (frame.getWidth() != preferredFrameWidth || frame.getHeight() != preferredFrameHeight) {
			frame.setSize(preferredFrameWidth, preferredFrameHeight);
			frame.setLocationRelativeTo(null);
		}
		
		graphics.drawImage(image, 0, 0, null);
	}
	
}