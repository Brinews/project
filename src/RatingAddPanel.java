import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class RatingAddPanel extends JPanel
{
	private JButton mesg;
	private JPanel panel;
	private JPanel btnPanel;

	JTextField jtfID;
	JTextField jtfName;
	JTextField jtfDsp;

	public RatingAddPanel(String index)
	{
		setLayout(new BorderLayout());

		mesg = new JButton("  ");
		mesg.setEnabled(false);
		add(mesg, BorderLayout.NORTH);

		DocumentListener listener = new KeyFieldListener();

		JPanel center = new JPanel(new GridLayout(2, 1));

		panel = new JPanel(new GridLayout(3, 2));
		addLabel("Review ID....................:");
		jtfID = new JTextField("", 10);
		jtfID.getDocument().addDocumentListener(listener);
		panel.add(jtfID);

		addLabel("Review Rating Member Name...:");
		jtfName = new JTextField("", 30);
		jtfName.getDocument().addDocumentListener(listener);
		panel.add(jtfName);

		addLabel("Review Rating Description...:");
		jtfDsp = new JTextField("", 30);
		jtfDsp.setEnabled(false);
		panel.add(jtfDsp);

		ActionListener complete = new CompleteAction();

		JPanel radio = new JPanel();
		ButtonGroup group = new ButtonGroup();
		JRadioButton f = new JRadioButton("extremely helpful", true);
		f.addActionListener(complete);
		radio.add(f);
		JRadioButton s = new JRadioButton("very helpful", false);
		s.addActionListener(complete);
		radio.add(s);
		JRadioButton t = new JRadioButton("helpful", false);
		t.addActionListener(complete);
		radio.add(t);
		JRadioButton e = new JRadioButton("not helpful", false);
		e.addActionListener(complete);
		radio.add(e);
		JRadioButton n = new JRadioButton("misleading", false);
		n.addActionListener(complete);
		radio.add(n);

		center.add(panel);
		center.add(radio);
		add(center, BorderLayout.CENTER);


		ActionListener command = new RatCommandAction();

		btnPanel = new JPanel();
		addButton("[+] Add", command);
		addButton("[-] Del", command);
		addButton("[u] Upd", command);
		
		add(btnPanel, BorderLayout.SOUTH);
	}

	private String buildAddSql()
	{
		String sponsorName = getReviewSponsor(jtfID.getText());

		if (sponsorName.equals(jtfName.getText())) {
			mesg.setText("Member Cannot Give A Rating To His/Her Own Review");

			return "";
		}

		String sql = "insert into RVR values ('";

		sql += jtfID.getText();
		sql += "', '";
		sql += jtfName.getText();
		sql += "', '";
		sql += jtfDsp.getText();

		sql += "')";

		return sql;
	}

	private String buildDelSql()
	{
		String sql = "delete from RVR where RRRID ='";
		sql += jtfID.getText();
		sql += "' and RRMNA = '";
		sql += jtfName.getText();
		sql += "'";

		return sql;
	}

	private String buildUpdSql()
	{
		String sql = "update RVR set RRDSP = '";

		sql += jtfDsp.getText();

		sql += "' where RRRID = '";
		sql += jtfID.getText();
		sql += "' and RRMNA = '";
		sql += jtfName.getText();
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
		String name = jtfName.getText();

		if (!"".equals(cid) && !"".equals(name)) {
			MySqlProxy mySql = new MySqlProxy();

			String sql = "select * from RVR where RRRID = '"+ cid + "' and RRMNA = '" + name + "'";

			try {
				mySql.spyConnectDB();

				ResultSet rs = mySql.spyDoQuery(sql);
				while (rs.next()) {
					jtfDsp.setText(rs.getString(3));
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

	private String getReviewSponsor(String rid)
	{
		String name = "";

		String sql = "Select RVMNA from REW where RVID = '" + rid + "'";
		MySqlProxy mySql = new MySqlProxy();

		try {
			mySql.spyConnectDB();

			ResultSet rs = mySql.spyDoQuery(sql);
			while (rs.next()) {
				name = rs.getString(1);
			}

			rs.close();

			mySql.spyCloseConnection();
		}
		catch (Exception e)
		{
			MyLogProxy.logWrite(e.getMessage());
		}

		return name;
	}

	private class CompleteAction implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String input = event.getActionCommand();

			jtfDsp.setText(input);
		}
	}

	private class KeyFieldListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent event) { updateField(); }
		public void removeUpdate(DocumentEvent event) { updateField(); }
		public void changedUpdate(DocumentEvent event) {}
	}

	private class RatCommandAction implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String input = event.getActionCommand();

			String sql = "";
			String ret = input + " >>>> ";

			if (input.equals("[+] Add")) sql = buildAddSql();
			if (input.equals("[-] Del")) sql = buildDelSql();
			if (input.equals("[u] Upd")) sql = buildUpdSql();

			if ("".equals(sql)) return;

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
