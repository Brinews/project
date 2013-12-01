import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

class CarAddPanel extends JPanel
{
	private JButton mesg;
	private JPanel panel;
	private JPanel feature;
	private JPanel btnPanel;

	private Vector<String> featureID = new Vector<String>();
	private Vector<String> featureName = new Vector<String>();
	private Vector<String> featureSel = new Vector<String>();
	private Vector<JCheckBox> jcbList = new Vector<JCheckBox>();

	JTextField jtfID;
	JTextField jtfManu;
	JTextField jtfModel;
	JTextField jtfMaker;
	JTextField jtfYear;

	public CarAddPanel(String index)
	{
		getFeatureInfo();

		setLayout(new BorderLayout());

		mesg = new JButton("  ");
		mesg.setEnabled(false);
		add(mesg, BorderLayout.NORTH);

		DocumentListener listener = new KeyFieldListener();

		JPanel center = new JPanel(new GridLayout(2, 1));

		panel = new JPanel(new GridLayout(6, 2));
		addLabel("Car ID(Auto Increment)..:");
		jtfID = new JTextField("", 10);
		panel.add(jtfID);
		jtfID.getDocument().addDocumentListener(listener);

		addLabel("Car Manufacture.........:");
		jtfManu = new JTextField("", 30);
		panel.add(jtfManu);
		addLabel("Car Model...............:");
		jtfModel = new JTextField("", 30);
		panel.add(jtfModel);
		addLabel("Car Maker...............:");
		jtfMaker = new JTextField("", 30);
		panel.add(jtfMaker);
		addLabel("Car Make Year...........:");
		jtfYear = new JTextField("", 4);
		panel.add(jtfYear);
		addLabel("Car Feature List........:");

		feature = new JPanel();
		addFeatureCheckBox();
		
		center.add(panel);
		center.add(feature);

		add(center, BorderLayout.CENTER);

		ActionListener command = new CarCommandAction();

		btnPanel = new JPanel();
		addButton("[+] Add", command);
		addButton("[-] Del", command);
		addButton("[u] Upd", command);
		
		add(btnPanel, BorderLayout.SOUTH);
	}

	private String buildAddSql()
	{
		int maxNo = getMaxCarNo() + 1;
		jtfID.setText(""+maxNo);

		String sql = "insert into CAR values ('";

		sql += jtfID.getText();
		sql += "', '";
		sql += jtfManu.getText();
		sql += "', '";
		sql += jtfModel.getText();
		sql += "', '";
		sql += jtfMaker.getText();
		sql += "', '";
		sql += jtfYear.getText();

		sql += "'); ";

		getFeatureState();
		/* insert into CAF */
		for (int i = 0; i < featureID.size(); i++) {
			sql += "insert into CFR values('";
			sql += jtfID.getText();
			sql += "', '";
			sql += featureID.elementAt(i);
			sql += "', '";
			sql += featureSel.elementAt(i);
			sql += "');";
		}

		return sql;
	}

	private String buildDelSql()
	{
		String sql = "delete from CAR where CANO ='";
		sql += jtfID.getText();
		sql += "'; ";

		/* delete feature relation */
		sql += "delete from CFR where CFRCID = '";
		sql += jtfID.getText();
		sql += "';";

		return sql;
	}

	private String buildUpdSql()
	{
		String sql = "update CAR set CAMNF = '";

		sql += jtfManu.getText();
		sql += "', CAMOD = '";
		sql += jtfModel.getText();
		sql += "', CAMAK = '";
		sql += jtfMaker.getText();
		sql += "', CAMKY = '";
		sql += jtfYear.getText();

		sql += "' where CANO = '";
		sql += jtfID.getText();
		sql += "';";

		/* update feature relation */
		getFeatureState();

		for (int i = 0; i < featureID.size(); i++) {
			sql += "update CFR set CFRHAS = '";
			sql += featureSel.elementAt(i);
			sql += "' where CFRCID = '";
			sql += jtfID.getText();
			sql += "' and CFRFID = '";
			sql += featureID.elementAt(i);
			sql += "';";
		}

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

	private void getFeatureState()
	{
		for (int i = 0; i < jcbList.size(); i++)
		{
			JCheckBox j = jcbList.elementAt(i);
			if (j.isSelected())
				featureSel.setElementAt("1", i);
			else
				featureSel.setElementAt("0", i);
		}
	}

	private void addFeatureCheckBox()
	{
		for (int i = 0; i < featureName.size(); i++) {
			JCheckBox jcb = new JCheckBox(featureName.elementAt(i));
			jcbList.add(jcb);
			jcb.setSelected(false);
			feature.add(jcb);
		}
	}

	private void getFeatureInfo()
	{
		MySqlProxy mySql = new MySqlProxy();
		String sql = "select * from CAF";

		try {
			mySql.spyConnectDB();

			ResultSet rs = mySql.spyDoQuery(sql);
			while (rs.next()) {
				featureID.add(rs.getString(1));
				featureName.add(rs.getString(2));
				featureSel.add("0");
			}

			rs.close();
			mySql.spyCloseConnection();
		}
		catch(Exception e)
		{
			MyLogProxy.logWrite(e.getMessage());
		}
	}

	private void updateField()
	{
		String cid = jtfID.getText();
		if (!"".equals(cid)) {
			MySqlProxy mySql = new MySqlProxy();

			String sql = "select * from CAR where CANO = '"+ cid + "'";

			try {
				mySql.spyConnectDB();

				ResultSet rs = mySql.spyDoQuery(sql);
				while (rs.next()) {
					jtfManu.setText(rs.getString(2));
					jtfModel.setText(rs.getString(3));
					jtfMaker.setText(rs.getString(4));
					jtfYear.setText(rs.getString(5));
				}

				rs.close();

				/** update feature list */
				sql = "select * from CFR where CFRCID = '" + cid + "'";
				rs = mySql.spyDoQuery(sql);
				while (rs.next()) {
					JCheckBox jc = jcbList.elementAt(Integer.parseInt(rs.getString(2))-1);
					boolean state = false;
					if (rs.getString(3).equals("0"))
						state = false;
					if (rs.getString(3).equals("1"))
						state = true;

					//MyLogProxy.logWrite(rs.getString(2) + "---"+ rs.getString(3));

					jc.setSelected(state);
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

	private int getMaxCarNo()
	{
		int no = 1;

		String sql = "Select max(CANO) from CAR";
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

	private class CarCommandAction implements ActionListener
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
