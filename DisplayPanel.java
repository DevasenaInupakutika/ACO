
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DisplayPanel extends JPanel {
	BufferedImage bi;
	
	public DisplayPanel(BufferedImage bi, Dimension size) {
		super();
		this.bi = bi;
		this.setPreferredSize(size);
	}
	
	public void paint(Graphics g) {
		//super.paint(g);
		
		//Image already flushed
		//bi.flush();
		g.drawImage(bi, 0, 0, null);
	}
	
	public void setImage(BufferedImage newBI){
		synchronized (bi) {
			this.bi = newBI;
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				repaint();
			}
		});

	}
}
