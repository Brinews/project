import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import java.util.Date;
import java.text.SimpleDateFormat;

class ReviewAddPanel extends JPanel
{
	private JButton mesg;
	private JPanel panel;
	private JPanel areaPanel;
	private JPanel btnPanel;

	JTextField jtfID;
	JTextField jtfCID;
	JTextField jtfName;
	JTextField jtfFeature;
	JTextField jtfFeatOpt;
	JTextField jtfTime;
	JTextArea jtfContent;

	public ReviewAddPanel(String index)
	{
		setLayout(new BorderLayout());

		mesg = new JButton("  ");
		mesg.setEnabled(false);
		add(mesg, BorderLayout.NORTH);

		DocumentListener listener = new KeyFieldListener();
		DocumentListener listener2 = new CarFieldListener();

		JPanel centerPanel = new JPanel(new GridLayout(2, 1));

		panel = new JPanel(new GridLayout(6, 2));
		addLabel("Review ID(Auto Increment)..:");
		jtfID = new JTextField("", 10);
		jtfID.getDocument().addDocumentListener(listener);
		panel.add(jtfID);

		addLabel("Car ID..................:");
		jtfCID = new JTextField("", 30);
		jtfCID.getDocument().addDocumentListener(listener2);
		panel.add(jtfCID);

		addLabel("Member Name.............:");
		jtfName = new JTextField("", 30);
		panel.add(jtfName);
		
		addLabel("Review Feature..........:");
		jtfFeature = new JTextField("", 30);
		panel.add(jtfFeature);

		addLabel("Car Feature Options.....:");
		jtfFeatOpt = new JTextField("", 30);
		jtfFeatOpt.setEnabled(false);
		panel.add(jtfFeatOpt);

		Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = format.format(today);

		addLabel("Review Time.............:");
		jtfTime = new JTextField("", 30);
		jtfTime.setText(time);
		panel.add(jtfTime);

		areaPanel = new JPanel(new GridLayout(1, 2));
		areaPanel.add(new JLabel("Review Content..........:"));
		jtfContent = new JTextArea(5, 30);
		areaPanel.add(new JScrollPane(jtfContent));

		centerPanel.add(panel);
		centerPanel.add(areaPanel);

		add(centerPanel, BorderLayout.CENTER);

		ActionListener command = new RewCommandAction();

		btnPanel = new JPanel();
		addButton("[+] Add", command);
		addButton("[-] Del", command);
		addButton("[u] Upd", command);
		
		add(btnPanel, BorderLayout.SOUTH);
	}

	private String buildAddSql()
	{
		int maxNo = getMaxRewNo() + 1;
		jtfID.setText(""+maxNo);

		String sql = "insert into REW values ('";

		sql += jtfID.getText();
		sql += "', '";
		sql += jtfCID.getText();
		sql += "', '";
		sql += jtfName.getText();
		sql += "', '";
		sql += jtfFeature.getText();
		sql += "', to_date('";
		sql += jtfTime.getText();
		sql += "', 'yyyy-mm-dd hh24:mi:ss'), '";
		sql += jtfContent.getText();

		sql += "')";

		return sql;
	}

	private String buildDelSql()
	{
		String sql = "delete from REW where RVID ='";
		sql += jtfID.getText();
		sql += "'";

		return sql;
	}

	private String buildUpdSql()
	{
		String sql = "update REW set RVODR = '";

		sql += jtfFeature.getText();
		sql += "', RVDSP = '";
		sql += jtfContent.getText();

		sql += "' where RVID = '";
		sql += jtfID.getText();
		sql += "'";

		return sql;
	}

	private void addLabel(String txt)
	{
		JLabel label = new JLabel(txt);
		panel.add(label);
	}

	private void addButton(String label, ActionListener listener)
	{
		JButton button = new JButton(label);
		button.addActionListener(listener);
		btnPanel.add(button);
	}

	public void updateField()
	{
		String cid = jtfID.getText();
		if (!"".equals(cid)) {
			MySqlProxy mySql = new MySqlProxy();

			String sql = "select * from REW where RVID = '"+ cid + "'";

			try {
				mySql.spyConnectDB();

				ResultSet rs = mySql.spyDoQuery(sql);
				while (rs.next()) {
					jtfCID.setText(rs.getString(2));
					jtfName.setText(rs.getString(3));
					jtfFeature.setText(rs.getString(4));
					jtfTime.setText(rs.getString(5));
					jtfContent.setText(rs.getString(6));
				}

				rs.close();

				mySql.spyCloseConnection();
			}
			catch(Exception e)
			{
				MyLogProxy.logWrite(e.getMessage());
			}
		}
	}

	private int getMaxRewNo()
	{
		int no = 1;

		String sql = "Select max(RVID) from REW";
		MySqlProxy mySql = new MySqlProxy();

		try {
			mySql.spyConnectDB();

			ResultSet rs = mySql.spyDoQuery(sql);
			while (rs.next()) {
				no = Integer.parseInt(rs.getString(1));
			}

			rs.close();

			mySql.spyCloseConnection();
		}
		catch (Exception e)
		{
			MyLogProxy.logWrite(e.getMessage());
		}

		return no;
	}

	private class KeyFieldListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent event) { updateField(); }
		public void removeUpdate(DocumentEvent event) { updateField(); }
		public void changedUpdate(DocumentEvent event) {}
	}

	private void updateOptField()
	{
		jtfFeatOpt.setText("");
		String cid = jtfCID.getText();
		String opt = "";

		if (!"".equals(cid)) {
			String sql = "select * from CFR where CFRCID = '" + cid + "'";
			MySqlProxy mySql = new MySqlProxy();
			try {
				mySql.spyConnectDB();

				ResultSet rs = mySql.spyDoQuery(sql);
				while(rs.next()) {
					if (rs.getString(3).equals("1"))
						opt += rs.getString(2) + " ";
				}

				jtfFeatOpt.setText(opt);

				rs.close();
				mySql.spyCloseConnection();
			}
			catch (Exception e)
			{
				MyLogProxy.logWrite(e.getMessage());
			}
		}
	}

	private class CarFieldListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent event) { updateOptField(); }
		public void removeUpdate(DocumentEvent event) { updateOptField(); }
		public void changedUpdate(DocumentEvent event) {}
	}

	private class RewCommandAction implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String input = event.getActionCommand();

			String sql = "";
			String ret = input + " >>>> ";

			if (input.equals("[+] Add")) sql = buildAddSql();
			if (input.equals("[-] Del")) sql = buildDelSql();
			if (input.equals("[u] Upd")) sql = buildUpdSql();

			MyLogProxy.logWrite(sql);

			MySqlProxy mySql = new MySqlProxy();

			try {

				mySql.spyConnectDB();
				ret += mySql.spyDoUpdate(sql);
				mySql.spyCloseConnection();

			} catch (Exception e) {
				MyLogProxy.logWrite(e.getMessage());
			}

			mesg.setText(ret);
		}
	}
}
