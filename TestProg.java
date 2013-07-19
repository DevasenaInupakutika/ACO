
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TestProg {

	public TestProg() {
		//This is to be done as per the swing guidelines i guess
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("Ant colo");
		BufferedImage i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

		//Graphics2D g = i.createGraphics();

		//g.setColor(new Color(255, 255, 255));
		//g.fillRect(0, 0, 100, 100);
		//g.dispose();
		for (int j = 0; j < 100; j++) {
			for (int j2 = 0; j2 <100; j2++) {
				i.setRGB(j, j2, 65432);	
			}

		}

		i = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

/*		ImagePanel panel = new ImagePanel(i);	

		MainCanvas c = new MainCanvas(i);*/
		TestPanel t = new TestPanel(i);

/*		JPanel pan = new JPanel();
		pan.add(c);*/


		JScrollPane p = new JScrollPane(t,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		p.setPreferredSize(new Dimension(500, 500));
		frame.add(p);



		frame.pack();
		frame.setLocation(250,25);
		//frame.setSize(new Dimension(50, 50));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		TestProg l = new TestProg();
	}
}
/*class ImagePanel extends JPanel{
	BufferedImage bi;
	public ImagePanel(BufferedImage gg) {
		super();
		this.bi=gg;
		this.setMinimumSize(new Dimension(1000,1000));

		JButton b = new JButton("dasdlkdjd");
		b.setSize(800, 800);
		add(b);
		for (int i = 0; i < 200; i++) {
			b = new JButton("dasdlkdjd");
			b.setSize(800, 800);
			add(b);
		}
	}

	@Override
	public void paintComponents(Graphics g) {
		super.paintComponent(g);
		System.out.println("Inside Paint Component--");
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);
		g.dispose();

		System.out.println("Inside Paint");
    	bi.flush();
		Graphics2D gg = bi.createGraphics();

		gg.fillRect(0, 0, 1000, 1000);
		gg.setColor(Color.black);
		for (int j = 0; j < 10; j++) {
			for (int j2 = 0; j2 <10; j2++) {
				//i.setRGB(j, j2, 8762348);
				gg.fillRect(j, j2, 1, 1);
			}
		}

		gg.fillRect(980, 600, 10, 10);
		gg.dispose();
        g.drawImage(bi, 0, 0, null);
        //Dimension s = getSize();
        //g.drawOval(0, 0, s.width, s.height);

	}
}
 */
class TestPanel extends JPanel {

	BufferedImage bi;

	public TestPanel(BufferedImage bi) {
		super();
		//l = new Label();
		this.bi = bi;
		this.setLayout(new BorderLayout());
		//this.add(c,BorderLayout.CENTER);
		this.setMaximumSize(new Dimension(1000, 1000));
		this.setSize(1000, 1000);
		this.setPreferredSize(new Dimension(1000,1000));
		System.out.println(this.getPreferredSize());
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		//super.paint(g);
		/*		//g.dispose();
        Dimension size = getSize();
        // diameter
        int d = Math.min(size.width, size.height); 
        int x = (size.width - d)/2;
        int y = (size.height - d)/2;

        // draw circle (color already set to foreground)
        g.fillOval(x, y, d, d);
        g.setColor(Color.black);
        g.drawOval(x, y, d, d);*/
		bi.flush();
		Graphics gg = bi.createGraphics();

		gg.fillRect(0, 0, 1000, 1000);
		gg.setColor(Color.black);
		for (int j = 0; j < 50; j++) {
			for (int j2 = 0; j2 <50; j2++) {
				bi.setRGB(j, j2, 255<<16);
				//gg.fillRect(j, j2, 1, 1);
			}
		}
		System.out.println("Paint");
		gg.fillRect(980, 990, 1, 1);
		gg.dispose();
		g.drawImage(bi, 0, 0, null);

	}
}

/*class MainCanvas extends Canvas
{
	BufferedImage bi;
	public MainCanvas(BufferedImage bi) {
		super();
		this.bi = bi;
		// TODO Auto-generated constructor stub

		//this.setPreferredSize(new Dimension(1000, 1000));
		this.setSize(new Dimension(1000, 1000));
	}
	@Override
	public void paintAll(Graphics g) {
		// TODO Auto-generated method stub
		super.paintAll(g);
	}
    public void paint(Graphics g)
    {
    	System.out.println("Inside asd");
    	bi.flush();
		Graphics2D gg = bi.createGraphics();

		gg.fillRect(0, 0, 1000, 1000);
		gg.setColor(Color.black);
		for (int j = 0; j < 10; j++) {
			for (int j2 = 0; j2 <10; j2++) {
				//i.setRGB(j, j2, 8762348);
				gg.fillRect(j, j2, 1, 1);
			}
		}

		gg.fillRect(980, 600, 10, 10);
		gg.dispose();
        g.drawImage(bi, 0, 0, null);
        //Dimension s = getSize();
        //g.drawOval(0, 0, s.width, s.height);
    }
    @Override
    public void update(Graphics g) {
    	// TODO Auto-generated method stub
    	super.update(g);
    	System.out.println("Inside updaa");
    }


}*/
