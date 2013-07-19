
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DebugSettings {

	private static String FILE_NAME="/tmp/ant-stats";
	
	public static boolean ALL_PATH_LENGTHS=false;
	public static boolean ANTS_ON_EACH_PATH=false;
	//Number paths ending in food per iteration. In other words, The number of ants that
	//find food on an average.
	public static boolean PATH_LEN_RATE = false;
	public static boolean LOOPING_PATHS = false;
	
	private static BufferedWriter writer;
	
	static {
		try {
			writer = new BufferedWriter(new FileWriter(FILE_NAME));
		} catch (IOException e) {
			System.out.println("#### IO ERROR $$$$ "+e.toString());
		}
	}
	
	public synchronized static void writeToFile(String s){
		try {
			writer.write(s);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			System.out.println("#### IO ERROR $$$$ "+e.toString());
		}
	}
	public static void closeFile(){
		try {
			writer.close();
		} catch (IOException e) {
			//do nothing
		}
	}
}
