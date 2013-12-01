import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

class MyControlPanel extends JPanel
{
	/* For View Panel Select */
	public int panelID;
	
	/* Left control panel */
	public JPanel topPanel;
	
	/* Main view panel */
	public JPanel viewPanel;

	/* For Message Display */
	public JButton display;

	/* view panel class */
	List<JPanel> panelList;

	MemListPanel mlp;
	MemAddPanel map;

	CarListPanel clp;
	CarAddPanel cap;

	ReviewListPanel vlp;
	ReviewAddPanel vap;

	RatingListPanel rlp;
	RatingAddPanel rap;

	public MyControlPanel()
	{
		panelList = new ArrayList<JPanel>();

		panelID = 0;
		setLayout(new BorderLayout());

		display = new JButton("[...]");
		display.setEnabled(false);
		add(display, BorderLayout.SOUTH);

		ActionListener sqlcommand = new CommandAction();
		ActionListener setstate = new StateAction();

		topPanel = new JPanel();
		viewPanel = new JPanel();

		/***
		 * Left control buttons
		 */
		addButton("DB INIT", sqlcommand);

		addButton("MEM LIST", setstate);
		addButton("MEM ADD", setstate);

		addButton("CAR LIST", setstate);
		addButton("CAR ADD", setstate);

		addButton("REVIEW LIST", setstate);
		addButton("REVIEW ADD", setstate);

		addButton("RATING LIST", setstate);
		addButton("RATING ADD", setstate);

		topPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));
		add(topPanel, BorderLayout.NORTH);

		/***
		 * View Panels Init
		 */
		mlp = new MemListPanel();
		map = new MemAddPanel("");

		panelList.add(mlp);
		panelList.add(map);
		
		viewPanel.add(mlp);
		viewPanel.add(map);

		clp = new CarListPanel();
		cap = new CarAddPanel("");

		panelList.add(clp);
		panelList.add(cap);
		
		viewPanel.add(clp);
		viewPanel.add(cap);

		vlp = new ReviewListPanel();
		vap = new ReviewAddPanel("");

		panelList.add(vlp);
		panelList.add(vap);
		
		viewPanel.add(vlp);
		viewPanel.add(vap);

		rlp = new RatingListPanel();
		rap = new RatingAddPanel("");

		panelList.add(rlp);
		panelList.add(rap);
		
		viewPanel.add(rlp);
		viewPanel.add(rap);

		viewPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));
		hideViewPanel(0);

		add(viewPanel, BorderLayout.CENTER);
	}

	public void hideViewPanel(int f)
	{
		int i = 0;
		for(JPanel p:panelList)
		{
			i++;
			p.setVisible(false);
			if (i == f) {
				p.setVisible(true);
			}
		}
	}

	public void addButton(String label, ActionListener listener)
	{
		JButton button = new JButton(label);
		button.addActionListener(listener);
		
		topPanel.add(button);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}

	private class StateAction implements ActionListener {
		public void actionPerformed(ActionEvent event)
		{
			String input = event.getActionCommand();

			if (input.equals("MEM LIST"))
				panelID = 1;
			if (input.equals("MEM ADD"))
				panelID = 2;
			if (input.equals("CAR LIST"))
				panelID = 3;
			if (input.equals("CAR ADD"))
				panelID = 4;
			if (input.equals("REVIEW LIST"))
				panelID = 5;
			if (input.equals("REVIEW ADD"))
				panelID = 6;
			if (input.equals("RATING LIST"))
				panelID = 7;
			if (input.equals("RATING ADD"))
				panelID = 8;

			/***
			 *repaint
			 */
			hideViewPanel(panelID);
		}
	}
}

class CommandAction implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{
		String input = event.getActionCommand();

		String sql = MySqlHash.getHashSql(input);

		MySqlProxy mySql = new MySqlProxy();

		try {

			mySql.spyConnectDB();
			mySql.spyDoUpdate(sql);
			mySql.spyCloseConnection();

		} catch (Exception e) {
			MyLogProxy.logWrite(e.getMessage());
		}
	}
}
