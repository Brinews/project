import java.util.*;

class MySqlHash {

	private static HashMap<String, String> sqlhash;

	public MySqlHash()
	{
	}

	/***
	 * SQL TO EXECUTE
	 */
	public static void doInit()
	{
		sqlhash = new HashMap<String, String>();
		//sqlhash.put("DB INIT", "drop table MEM; create table MEM(MEMNAM varchar(30) primary key, MEMAGE number(8), MEMGND varchar(10), MEMCTY varchar(30), MEMSTA varchar(30), MEMEML varchar(30)); ");
		sqlhash.put("DB INIT", "insert into MEM values('A', '18', 'Male', 'NewYork', 'USA', 'test@gmail.com')");
		sqlhash.put("MEM LIST", "select * from MEM");
		sqlhash.put("CAR LIST", "select * from CAR");
		sqlhash.put("REVIEW LIST", "select * from REW");
		sqlhash.put("RATING LIST", "select * from RVR");
	}

	public static String getHashSql(String key)
	{
		//MyLogProxy.logWrite(key);
		//MyLogProxy.logWrite(sqlhash.get(key));

		if (sqlhash.containsKey(key))
			return sqlhash.get(key);

		return "";
	}
}
