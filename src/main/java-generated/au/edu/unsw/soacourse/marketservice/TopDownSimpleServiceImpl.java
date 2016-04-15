package au.edu.unsw.soacourse.marketservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.jws.WebService;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVReader;

@WebService(endpointInterface = "au.edu.unsw.soacourse.marketservice.TopDownSimpleService")
public class TopDownSimpleServiceImpl implements TopDownSimpleService {
	ObjectFactory factory = new ObjectFactory();
	
	
	@Override
	public ImportMarketDataResponse importMarketData(ImportMarketDataRequest parameters)
			throws ImportMarketDataFaultMsg, DateRangeFaultMsg, DateFormatFaultMsg {
		// TODO Auto-generated method stub
		String[] startDate = parameters.startDate.split("-");
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);		
		Calendar cal2 = Calendar.getInstance();
		cal2.setLenient(false);
		
		String[] endDate = parameters.endDate.split("-");
		Date currentDate = new Date();
		startDate[1] = Integer.toString((Integer.parseInt(startDate[1])-1));
		endDate[1] = Integer.toString((Integer.parseInt(endDate[1])-1));
		if(!(parameters.dateRange.matches("d|w|m|v"))){
			String msg = "dateRange field should be one of (d|w|m|v)";
			String code = "ERR_DATE_RANGE";
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new ImportMarketDataFaultMsg(msg,fault);
		}
		if((parameters.getSec().length() > 6) || (parameters.getSec().length() < 3)){
			String msg = "SEC code should be between 3 to 6 characters long";
			String code = "ERR_SEC";
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new ImportMarketDataFaultMsg(msg,fault);
		}
		URL sec;
		try {
			sec = new URL("https://au.finance.yahoo.com/q?s="+parameters.getSec());
			HttpURLConnection testConnection = (HttpURLConnection) sec.openConnection();
			testConnection.setRequestMethod("HEAD");
	        if(!(testConnection.getResponseCode() == HttpURLConnection.HTTP_OK)){
	    		String msg = "Requested Security not listed, please try another";
				String code = "ERR_SEC_NOT_EXIST";
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ImportMarketDataFaultMsg(msg,fault);
	        }
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Do date checking here
		try{		
			
			cal.set(Integer.parseInt(startDate[2]), Integer.parseInt(startDate[1]), Integer.parseInt(startDate[0]));
			Date formatStartDate = cal.getTime();
			cal2.set(Integer.parseInt(endDate[2]), Integer.parseInt(endDate[1]), Integer.parseInt(endDate[0]));
			Date formatEndDate = cal2.getTime();
			if((formatStartDate.compareTo(formatEndDate)>0) ||
					(formatEndDate.compareTo(currentDate) > 0)||
					(formatEndDate.equals(formatStartDate))){
				String msg = "Starting date must be before ending date,end date must not exceed current date";
				String code = "ERR_DATE_RANGE";
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new DateRangeFaultMsg(msg,fault);			
			}
		}catch(Exception e){
			String msg = "One or more dates have the incorrect format, the correct cormat is DD-MM-YYYY";
			String code = "ERR_DATE_FORMAT";
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new DateFormatFaultMsg(msg,fault);
		}

		StringBuilder csvRequest = new StringBuilder();
		

		//Request for csv from remote server
		csvRequest.append("http://real-chart.finance.yahoo.com/table.csv?");
		//Security Code
		csvRequest.append("s=").append(parameters.sec).append("&");
		//Start Month
		csvRequest.append("a=").append(startDate[1]).append("&");
		//Start Day
		csvRequest.append("b=").append(startDate[0]).append("&");
		//Start Year
		csvRequest.append("c=").append(startDate[2]).append("&");
		//End Month
		csvRequest.append("d=").append(endDate[1]).append("&");
		//End Day
		csvRequest.append("e=").append(endDate[0]).append("&");
		//End Year
		csvRequest.append("f=").append(endDate[2]).append("&");
		//Date Range Interval
		csvRequest.append("g=").append(parameters.dateRange).append("&");
		csvRequest.append("ignore=.csv");
		
	  
	    
		try {
			URL remoteCSV;
			ImportMarketDataResponse response = new ImportMarketDataResponse();
			remoteCSV = new URL(csvRequest.toString());
			HttpURLConnection connection = (HttpURLConnection) remoteCSV.openConnection();
		    connection.setRequestMethod("HEAD");
	        if(!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)){
				String msg = "Remote CSV not found";
				String code = "ERR_INVALID";
				
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ImportMarketDataFaultMsg(msg,fault);
	        }
	        BufferedReader in = new BufferedReader(
		            new InputStreamReader(remoteCSV.openStream())); 
	        //new InputStreamReader(remoteCSV.openStream());
		    File tempFile = File.createTempFile(parameters.sec, ".csv");
		    String fileName = tempFile.getName();
		    fileName = fileName.substring(0,fileName.lastIndexOf("."));
		    BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		    //Write custom header for CSV
		    StringBuilder csvHeader = new StringBuilder();
		    csvHeader.append("# Sec, ").append(parameters.sec).append(", ");
		    csvHeader.append("dOption, ").append(parameters.dateRange).append(", ");
		    csvHeader.append("Currency, AUD, ");
		    csvHeader.append("Created, ").append(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(currentDate));
		    csvHeader.append("\r\n");	    
		    out.write(csvHeader.toString());
		    //Copy contents of remote csv to local copy
		    String inputLine;
		    int i = 0;
		    while((inputLine = in.readLine())!= null){
		    	out.write(inputLine+"\r\n");
		    	i++;
		    }
		    //Empty Sheet
		    if(i < 2){
		    	out.close();
			    in.close();
			    tempFile.delete();
				String msg = "No securities historical data for requested date range found, please try another date range";
				String code = "ERR_NOT_FOUND";
				
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ImportMarketDataFaultMsg(msg,fault);
		    }
		    out.close();
		    in.close();
		    response.setEventSetId(fileName);
		    return response;
		    
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			String msg = "Remote CSV not found";
			String code = "ERR_INVALID";
			
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new ImportMarketDataFaultMsg(msg,fault);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String msg = "Error writing CSV file";
			String code = "ERR_IO";
			
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new ImportMarketDataFaultMsg(msg,fault);
		}
	    
		
	
        
	}
	@Override
	public DownloadFileResponse downloadFile(DownloadFileRequest parameters) 
			throws DownloadFileFaultMsg {
		String source = System.getProperty("java.io.tmpdir")+"/"+parameters.eventSetId+".csv";
		File sourceCsv = new File(source);
		if(!sourceCsv.exists()){
			String msg = "EventSetID does not exist";
			String code = "ERR_EVENTSETID_INVALID";
			
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new DownloadFileFaultMsg(msg,fault);
		}
		String root = System.getProperty("catalina.home")+"/webapps/ROOT/";
		File dir = new File(root+"EventSetDownloads");
		if(!dir.exists()){
			dir.mkdir();
		}
		// TODO Auto-generated method stub
		DownloadFileResponse response = factory.createDownloadFileResponse();
		String downloadPath = System.getProperty("catalina.home")+"/webapps/ROOT/EventSetDownloads/"+parameters.eventSetId+".csv";
		try {
			Files.copy(Paths.get(source), Paths.get(downloadPath), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String msg = "Error writing to output destination,try again later";
			String code = "ERR_IO";
			
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new DownloadFileFaultMsg(msg,fault);
		}
		response.setDataURL("EventSet Id: " + System.getProperty("catalina.home"));
		return response;
	}

	@Override
	public ConvertMarketDataResponse convertMarketData(ConvertMarketDataRequest parameters)
			throws ConvertMarketDataFaultMsg {
		// TODO Auto-generated method stub
		
		//http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=USDAUD
		if(!(parameters.getTargetCurrency().length()==3)){
			String msg = "Target Currency Code should be exactly 3 characters long";
			String code = "ERR_INVALID_CURRENCY_LEN";
			
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new ConvertMarketDataFaultMsg(msg,fault);
		}
		String source = System.getProperty("java.io.tmpdir")+"/"+parameters.getEventSetId()+".csv";
		// TODO Auto-generated method stub
		ConvertMarketDataResponse response = factory.createConvertMarketDataResponse();
		String downloadPath = System.getProperty("catalina.home")+"/webapps/ROOT/EventSetDownloads/"+parameters.eventSetId+".csv";
		try {
			
			InputStream in = Files.newInputStream(Paths.get(source));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String inputLine;
			inputLine = reader.readLine();
	        String delims = "[,# ]+";
	        String[] headerInfo = inputLine.split(delims);
	        String sourceCurrency = headerInfo[6];
	        headerInfo[6] = parameters.getTargetCurrency();
	        headerInfo[8] = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
	        StringBuilder queryURL = new StringBuilder();
			queryURL.append("http://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=");
			queryURL.append(sourceCurrency).append(parameters.getTargetCurrency()).append("=x");
			
			URL remoteExchangeCSV;
			
			remoteExchangeCSV = new URL(queryURL.toString());
			HttpURLConnection connection = (HttpURLConnection) remoteExchangeCSV.openConnection();
		    connection.setRequestMethod("HEAD");
	        if(!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)){
				String msg = "Remote CSV not found";
				String code = "ERR_INVALID";
				
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ConvertMarketDataFaultMsg(msg,fault);
	        }
	        BufferedReader ex = new BufferedReader(
		            new InputStreamReader(remoteExchangeCSV.openStream()));
	        CSVReader exchangeReader = new CSVReader(ex);
	        String[] exchangeParse;
	        exchangeParse = exchangeReader.readNext();
	        if(exchangeParse[1].equals("N/A")){
	        	String msg = "Exchange Rate not found";
				String code = "ERR_EXCHANGE";
				
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ConvertMarketDataFaultMsg(msg,fault);
	        }
			float exchangeRate = Float.parseFloat(exchangeParse[1]);
		    
		    
		    //Write new CSV header
		    StringBuilder csvHeader = new StringBuilder();
		    csvHeader.append("# Sec, ").append(headerInfo[2]).append(", ");
		    csvHeader.append("dOption, ").append(headerInfo[4]).append(", ");
		    csvHeader.append("Currency, ").append(headerInfo[6]).append(", ");
		    csvHeader.append("Created, ").append(headerInfo[8]);
		    csvHeader.append("\r\n");
		    File tempFile = File.createTempFile(headerInfo[2], ".csv");
		    BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
			String fileName = tempFile.getName();
		    fileName = fileName.substring(0,fileName.lastIndexOf("."));
		    out.write(csvHeader.toString());
		    //Write CSV format
		    inputLine = reader.readLine();
		    out.write(inputLine+"\r\n");
		    
		    
		    //Read and write body
		    CSVReader csvReader = new CSVReader(reader);
		    String[] csvParser;
		    //csvParser = csvReader.readNext();
		    //csvParser = csvReader.readNext();
		    while((csvParser = csvReader.readNext()) != null){
		    	for(int i = 1;i<5;i++){
		    		
		    		csvParser[i] = Float.toString((Float.parseFloat(csvParser[i])*exchangeRate));
		    	}
		    	csvParser[6] = Float.toString((Float.parseFloat(csvParser[6])*exchangeRate));
		    	for(int i = 0; i< csvParser.length;i++){
		    		out.write(csvParser[i]+",");
		    	}
		    	out.write("\r\n");
		    }
		    out.close();
		    in.close();
			response.setEventSetId(fileName);
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String msg = "IO Error";
			String code = "ERR_IO";
			
			ServiceFaultType fault = factory.createServiceFaultType();
			fault.setErrcode(code);
			fault.setErrtext(msg);
			throw new ConvertMarketDataFaultMsg(msg,fault);
		}

	}

	@Override
	public ShowMarketDataResponse showMarketData(ShowMarketDataRequest parameters) throws ShowMarketDataFaultMsg {
		// TODO Auto-generated method stub
		ShowMarketDataResponse response = factory.createShowMarketDataResponse();
		String root = System.getProperty("catalina.home")+"/webapps/ROOT/";
		File dir = new File(root+"EventSetCharts");
		if(!dir.exists()){
			dir.mkdir();
		}
		File js = new File(root+"EventSetCharts/Chart.js");
		if(!js.exists()){
			String chart = System.getProperty("catalina.home")+"/webapps/TopDownSimpleService/WEB-INF/WebContent/js/Chart.js";
			
			try {
				Files.copy(Paths.get(chart), Paths.get(root+"EventSetCharts/Chart.js"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		String output = root+"EventSetCharts/view-now.html";
		File currentChart = new File(output);
		try {
			
			//Parse 1st file
			String firstSec = System.getProperty("java.io.tmpdir")+"/"+parameters.getEventSetId().get(0)+".csv";
			InputStream in = Files.newInputStream(Paths.get(firstSec));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			CSVReader csvReader1 = new CSVReader(reader);
			StringBuilder datesBuilder = new StringBuilder();
			String dates;
			StringBuilder firstPrices = new StringBuilder();
			String[] csvParser = null;
			
			csvParser = csvReader1.readNext();
			String secCode = csvParser[1];
			String dateRange = csvParser[3];
			String currency = csvParser[5]; 
			csvParser = csvReader1.readNext();
			while((csvParser = csvReader1.readNext()) != null){
				datesBuilder.insert(0,"'"+csvParser[0].substring(0, 7)+"',");
				firstPrices.insert(0,"'"+csvParser[4]+"',");
			}
			csvReader1.close();
			//Generate Labels
			dates = datesBuilder.toString().substring(0,datesBuilder.length());
			firstPrices.substring(0,firstPrices.length());
			
			//Parse 2nd file
			String secondSec = System.getProperty("java.io.tmpdir")+"/"+parameters.getEventSetId().get(1)+".csv";
			InputStream in2 = Files.newInputStream(Paths.get(secondSec));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2));
			CSVReader csvReader2 = new CSVReader(reader2);	
			StringBuilder dates2Builder = new StringBuilder();
			String dates2;
			StringBuilder secondPrices = new StringBuilder();
			csvParser = csvReader2.readNext();
			String secCode2 =csvParser[1];
			String dateRange2 = csvParser[3];			
			String currency2 = csvParser[5]; 
			csvParser = csvReader2.readNext();
			while((csvParser = csvReader2.readNext()) != null){
				dates2Builder.insert(0,"'"+csvParser[0].substring(0, 7)+"',");
				secondPrices.insert(0,"'"+csvParser[4]+"',");
			}
			csvReader2.close();
			dates2 = dates2Builder.toString().substring(0,dates2Builder.length());
			secondPrices.substring(0,secondPrices.length());
			
			if(!dates.equals(dates2)){
				String msg = "The date ranges differ between data sets, please use an event that has matching start and end dates";
				String code = "ERR_DATE_MISMATCH";
				System.out.println(dates);
				System.out.println(dates2);
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ShowMarketDataFaultMsg(msg,fault);
				
			}
			if((!dateRange.equals(dateRange2)) || (!dateRange.equals(" m"))){
				String msg = "Both Event sets must use Monthly Security data";
				String code = "ERR_DATE_RANGE";
				
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ShowMarketDataFaultMsg(msg,fault);
			
			}
			if(!currency.equals(currency2)){
				String msg = "Both Event sets must use the same currency";
				String code = "ERR_CURR_MISMATCH";
				
				ServiceFaultType fault = factory.createServiceFaultType();
				fault.setErrcode(code);
				fault.setErrtext(msg);
				throw new ShowMarketDataFaultMsg(msg,fault);
			} 
			String templatePath = System.getProperty("catalina.home")+"/webapps/TopDownSimpleService/WEB-INF/WebContent/view-now.html";
			File template = new File(templatePath);
			String htmlString = FileUtils.readFileToString(template);
			htmlString = htmlString.replace("$labels", dates);
			htmlString = htmlString.replace("$SEC1",secCode);
			htmlString = htmlString.replace("$SEC2",secCode2);
			htmlString = htmlString.replace("$data1",firstPrices);
			htmlString = htmlString.replace("$data2",secondPrices);
			FileUtils.writeStringToFile(currentChart, htmlString);
			
			//read in the first column and the closing column of each
			//First first column as labels
			//closing column as plot points
			//BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			response.setDataURL(output);
			return response;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
		
		
	}

}
