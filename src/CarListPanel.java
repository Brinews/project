import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingWorker;

class CarListPanel extends JPanel
{
	private static final Timer time = new Timer("RUNNER");

	Vector<Vector<Object>> colData = new Vector<Vector<Object>>();
	Vector<String> colNames = new Vector<String>();
	private JTable rTable;

	public CarListPanel()
	{
		refreshData();

		rTable = new JTable(new DefaultTableModel(colData, colNames));
		add(new JScrollPane(rTable));

		//update every second
		time.schedule(new TimerTask() {
				@Override
				public void run() { update(); }
				}, 0, 1000);
	}

	private void update()
	{
		new SwingWorker<Object, Object>()
		{
			protected Object doInBackground() throws Exception
			{
				colData.clear();
				colNames.clear();

				refreshData();

				return null;
			}

			protected void done()
			{
				((DefaultTableModel) rTable.getModel()).fireTableDataChanged();
			}
		}.execute();
	}

	public void refreshData()
	{
		String[] names = {"ID", "Manufacture", "Model", "Maker", "MakeYear"};		

		for (int i = 0; i < names.length; i++)
			colNames.add(names[i]);

		String sql = MySqlHash.getHashSql("CAR LIST");
		MySqlProxy mySql = new MySqlProxy();

		try {
			mySql.spyConnectDB();
			ResultSet rs = mySql.spyDoQuery(sql);

			while (rs.next()) {

				Vector<Object> oneCol = new Vector<Object>();

				for (int i = 1; i <= names.length; i++)
				{
					oneCol.add(rs.getString(i));
				}

				colData.add(oneCol);
			}

			rs.close();

			mySql.spyCloseConnection();
		}
		catch (Exception e) {
			MyLogProxy.logWrite(e.getMessage());
		}
	}
}
