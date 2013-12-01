import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class MemAddPanel extends JPanel
{
	private JButton mesg;
	private JPanel panel;
	private JPanel btnPanel;

	JTextField jtfName;
	JTextField jtfAge;
	JTextField jtfGender;
	JTextField jtfCity;
	JTextField jtfState;
	JTextField jtfEmail;

	public MemAddPanel(String index)
	{
		setLayout(new BorderLayout());

		mesg = new JButton("  ");
		mesg.setEnabled(false);
		add(mesg, BorderLayout.NORTH);

		DocumentListener listener = new KeyFieldListener();

		panel = new JPanel(new GridLayout(6, 2));
		addLabel("Mem Name..:");
		jtfName = new JTextField("", 30);
		panel.add(jtfName);
		jtfName.getDocument().addDocumentListener(listener);

		addLabel("Mem Age...:");
		jtfAge = new JTextField("", 8);
		panel.add(jtfAge);
		addLabel("Mem Gender:");
		jtfGender = new JTextField("", 10);
		panel.add(jtfGender);
		addLabel("Mem City..:");
		jtfCity = new JTextField("", 30);
		panel.add(jtfCity);
		addLabel("Mem State.:");
		jtfState = new JTextField("", 30);
		panel.add(jtfState);
		addLabel("Mem Email.:");
		jtfEmail = new JTextField("", 30);
		panel.add(jtfEmail);

		add(panel, BorderLayout.CENTER);

		ActionListener command = new MemCommandAction();

		btnPanel = new JPanel();
		addButton("[+] Add", command);
		addButton("[-] Del", command);
		addButton("[u] Upd", command);
		
		add(btnPanel, BorderLayout.SOUTH);
	}

	private String buildAddSql()
	{
		String sql = "insert into MEM values ('";

		sql += jtfName.getText();
		sql += "', '";
		sql += jtfAge.getText();
		sql += "', '";
		sql += jtfGender.getText();
		sql += "', '";
		sql += jtfCity.getText();
		sql += "', '";
		sql += jtfState.getText();
		sql += "', '";
		sql += jtfEmail.getText();

		sql += "')";

		return sql;
	}

	private String buildDelSql()
	{
		String sql = "delete from MEM where MEMNAM ='";
		sql += jtfName.getText();
		sql += "'";

		return sql;
	}

	private String buildUpdSql()
	{
		String sql = "update MEM set MEMAGE = '";

		sql += jtfAge.getText();
		sql += "', MEMGND = '";
		sql += jtfGender.getText();
		sql += "', MEMCTY = '";
		sql += jtfCity.getText();
		sql += "', MEMSTA = '";
		sql += jtfState.getText();
		sql += "', MEMEML = '";
		sql += jtfEmail.getText();

		sql += "' where MEMNAM = '";
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
		String memName = jtfName.getText();
		if (!"".equals(memName)) {
			MySqlProxy mySql = new MySqlProxy();

			String sql = "select * from MEM where MEMNAM = '"+ memName + "'";

			try {
				mySql.spyConnectDB();

				ResultSet rs = mySql.spyDoQuery(sql);
				while (rs.next()) {
					jtfAge.setText(rs.getString(2));
					jtfGender.setText(rs.getString(3));
					jtfCity.setText(rs.getString(4));
					jtfState.setText(rs.getString(5));
					jtfEmail.setText(rs.getString(6));
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

	private class KeyFieldListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent event) { updateField(); }
		public void removeUpdate(DocumentEvent event) { updateField(); }
		public void changedUpdate(DocumentEvent event) {}
	}

	private class MemCommandAction implements ActionListener
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
