import javax.swing.*;

public class MyMainForm extends JFrame {

	public static void main(String[] argv)
	{
		MyMainForm maf = new MyMainForm();

		maf.setTitle("Car Market");
		maf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		maf.setVisible(true);

	}

	public MyMainForm()
	{
		MySqlHash.doInit();

		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		MyControlPanel mcp = new MyControlPanel();
		add(mcp);
	}

	public static final int DEFAULT_WIDTH = 960;
	public static final int DEFAULT_HEIGHT = 600;
}
