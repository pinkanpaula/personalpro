import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.io.*;



public class YahooFinance {
	
	String CSVFILE_DEFAULT = "market-research.csv";
	String[] SYMBOLS_DEFAULT = {"GOOG", "FB", "AAPL"};
	String COLUMNS_DEFAULT = "snj1pr";
	String[] HEADERS_DEFAULT = {"Symbol", "Name", "Market Cap", "Previous Close Price", 
	                          "P/E Ratio", "Shares", "EPS", "Earnings"};
	
	
	BufferedReader br=null;
	String line="";
	String csvSplitBy=",";
	String yfSplitter="+";
	
	
	public YahooFinance(){}
	
	/**
	 * @param args
	 */
	
	public String financeURL(String symbol,String column){
		String[] token=symbol.split("\\s");
		String ySymbols="";
		for(int i=0;i<token.length;i++){
			ySymbols=ySymbols.concat(token[i]);
			if(i+1<token.length){
				ySymbols=ySymbols.concat(yfSplitter);
			}				
		}
		String yurl="http://finance.yahoo.com/d/quotes.csv?s=";
		return yurl.concat(ySymbols).concat("&f=").concat(column);
	}
	
	public double marketCapFloat(String marketCapString){
		return Float.parseFloat(marketCapString)*1e9;
	}
	
	public void csvToConsole(String result){
		try {
			//br=new BufferedReader(new FileReader(result));
			System.out.println(Arrays.toString(HEADERS_DEFAULT));
			double shares=0.0;
			double eps=0.0;
			double earnings=0.0;
			CSVReader cr=new CSVReader(new FileReader(result));
			String[] row=null;
			List content=cr.readAll();
			for(int i=0;i<content.size();i++){
				row=(String[])content.get(i);
					//System.out.println(row[2]+"#"+row[3]);
					shares=Math.round(marketCapFloat(row[2].split("B")[0])/Float.parseFloat(row[3]));
					eps=Math.round(Float.parseFloat(row[3])/Float.parseFloat(row[4]));
					earnings=eps*shares;
					NumberFormat nf=NumberFormat.getCurrencyInstance();
					nf.setMaximumFractionDigits(3);
					String outrow=Arrays.toString(row).concat("\t").concat(String.valueOf(shares)).concat("\t").concat(String.valueOf(eps)).concat("\t").concat(nf.format(earnings));
					System.out.println(outrow);
					
			}
			cr.close();
			/*while((line=cr.readLine())!=null){
				String[] rows=line.split("\\s");
				for(int i=0;i<rows.length;i++){
					System.out.println("rows:"+rows[i]);
				}				
			}*/
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void writeFile(String url){
		File file=new File(CSVFILE_DEFAULT);
		try {
			FileWriter fw=new FileWriter(file);
			FileUtils.copyURLToFile(new URL(url), file);
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public  void marketResearch(String symbol){
		String[] symbols=SYMBOLS_DEFAULT;
		String column=COLUMNS_DEFAULT;
		String yURL=financeURL(symbol,column);		
		System.out.println("yURL: "+yURL);
		writeFile(yURL);
		csvToConsole(CSVFILE_DEFAULT);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		YahooFinance yf=new YahooFinance();
		String is="";
		System.out.println("enter the symbols");
		InputStreamReader input=new InputStreamReader(System.in);
		BufferedReader reader=new BufferedReader(input);
		try {
			is=reader.readLine();
			yf.marketResearch(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
