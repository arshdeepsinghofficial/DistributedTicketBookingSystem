package logs;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logs 
{
	public static final String CLIENTLOGSPATH = "E:\\Logs\\Client\\";
	public static final String SERVERLOGSPATH = "E:\\Logs\\Server\\";
	
	public File file;
	public FileWriter writer;
	public String input;
	public BufferedWriter Bufwriter;
	public PrintWriter printwriter;
	
	public Logs(String fileName)throws IOException
	{
		file = new File(fileName);
		file.createNewFile();
		
	}
	
	public void createClientLogs(String clientID)throws IOException
	{
		
		writer = new FileWriter(CLIENTLOGSPATH+"\\"+clientID+".txt",true);
		Bufwriter = new BufferedWriter(writer);
		printwriter = new PrintWriter(Bufwriter);
		printwriter.println(input);
		printwriter.flush();
		
		Bufwriter.close();
		printwriter.close();
		writer.close();
	}
	
	public void createServerLogs(String serverID)throws IOException
	{
		
		writer = new FileWriter(SERVERLOGSPATH+"\\"+serverID+".txt",true);
		Bufwriter = new BufferedWriter(writer);
		printwriter = new PrintWriter(Bufwriter);
		printwriter.println(input);
		printwriter.flush();
		
		Bufwriter.close();
		printwriter.close();
		writer.close();
	}
}
