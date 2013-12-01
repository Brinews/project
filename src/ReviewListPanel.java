import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingWorker;

import java.util.Date;
import java.text.SimpleDateFormat;

class ReviewListPanel extends JPanel
{
	private static final Timer mytime = new Timer("REVIEWER");

	Vector<Vector<Object>> colData = new Vector<Vector<Object>>();
	Vector<String> colNames = new Vector<String>();
	private JTable rTable;

	private JPanel bot;

	private String sqlStr = "";

	public ReviewListPanel()
	{
		sqlStr = MySqlHash.getHashSql("REVIEW LIST");
		refreshData();

		rTable = new JTable(new DefaultTableModel(colData, colNames));
		add(new JScrollPane(rTable));

		bot = new JPanel(new GridLayout(3, 2));

		/* Today Time */
		Date today = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String time = fmt.format(today);
		time += " 00:00:00";

		//5
		String sql = "select * from REW where RVTIM > to_date('" + time + "', 'yyyy-mm-dd hh24:mi:ss')";
		addSqlSelectButton("ReviewsToday", sql);

		sql = MySqlHash.getHashSql("REVIEW LIST");
		addSqlSelectButton("AllReviews", sql);

		//6
		sql = "select * from REW where RVID not in (select RRRID from RVR)";
		addSqlSelectButton("NoRatingReview", sql);
		
		//7
		sql = "select * from REW where RVID in (select RRRID from (select RRRID, count(RRMNA) as CNT from RVR where length(RRDSP) >= 12 group by RRRID) where CNT >= 5)";
		addSqlSelectButton("Rating5+HelpfulReview", sql);

		//9
		sql = "select rrmna,0,0,0,0,0 from (select rrmna, count(distinct rrdsp) as cnt from rvr group by rrmna) where cnt = 1 and rrmna in (select distinct(rrmna) from rvr where rrdsp='misleading')";
		addSqlSelectButton("OnlyRatingMisleading", sql);

		//10
		sql = "with t as (select RRMNA, RVMNA from (select RRMNA, RVMNA, count(distinct RRDSP) as CNT  from (select a.RRRID, a.RRMNA, b.RVMNA, a.RRDSP from RVR a left join REW b on a.RRRID=b.RVID) group by RRMNA,RVMNA) where CNT=1 and RRMNA in (select RRMNA  from RVR where RRDSP='extremely helpful')) select e.RRMNA, e.RVMNA,0,0,0 from t e,t f where e.RRMNA=f.RVMNA and e.RVMNA=f.RRMNA";
		addSqlSelectButton("HelpAandB", sql);

		add(bot);
		
		//update every second
		mytime.schedule(new TimerTask() {
				@Override
				public void run() { update(); }
				}, 0, 1000);
	}

	public void addSqlSelectButton(String btnID, final String sql)
	{
		JButton btn = new JButton(btnID);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				sqlStr = sql;
			}
		};

		btn.addActionListener(listener);

		bot.add(btn);
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
		String[] names = {"Review ID", "Car ID", "Member Name", "Feature ID", "Review Time", "Review Content"};		

		for (int i = 0; i < names.length; i++)
			colNames.add(names[i]);

		MySqlProxy mySql = new MySqlProxy();

		try {
			mySql.spyConnectDB();
			ResultSet rs = mySql.spyDoQuery(sqlStr);

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
