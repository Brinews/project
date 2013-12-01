import java.util.*;
import java.io.*;

public class MyLogProxy {
	public MyLogProxy()
	{
	}

	public static void logWrite(String log)
	{
		try {
			PrintWriter out = new PrintWriter(new FileWriter("log.txt", true));

			out.println(log);

			out.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] argv)
	{
		MyLogProxy.logWrite("test");
	}
}
