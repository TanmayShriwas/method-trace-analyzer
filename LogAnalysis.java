package ibmcodes;
import ibmcodes.Sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;



public class LogAnalysis {
	public static void countFrequencies(ArrayList<String> list) 
    { 
        // hashmap to store the frequency of element 
        Map<String, Integer> hm = new HashMap<String, Integer>(); 
  
        for (String i : list) { 
            Integer j = hm.get(i); 
            hm.put(i, (j == null) ? 1 : j + 1); 
        } 
  
        // displaying the occurrence of elements in the arraylist 
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%30s %20s", "TraceEntry", "Count");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        for (Map.Entry<String, Integer> val : hm.entrySet()) { 
        	System.out.format("%30s %20s",val.getKey(), val.getValue());
            System.out.println();
//            System.out.print(val.getKey() + "\t\t\t "+ val.getKey()); 
//            System.out.println();
            
        } 
    } 
	
	public static void main(String[] args) {
		
		
		String[] command =
	    {
	        "cmd",
	    };
		   
	    Process p;
		
	    HashMap<Double,String> hmm=new HashMap<Double,String>();  
    	HashMap<Double,String> hm=new HashMap<Double,String>();
    	
    	ArrayList<String> list = new ArrayList<String>(); 
    	ArrayList<String> lis = new ArrayList<String>(); 
	    
	    try {
		
		//Converting to log file
		p = Runtime.getRuntime().exec(command);
	    new Thread(new Sync(p.getErrorStream(), System.err)).start();
	    new Thread(new Sync(p.getInputStream(), System.out)).start();
	    PrintWriter stdin = new PrintWriter(p.getOutputStream());
	    IbmGuiWindow g = new IbmGuiWindow();
	    String[] filenames = LogAnalysis.getFileNames();
	  	String str = filenames[0];
	    String str2 = filenames[1];
	    if(str.equals("") && str2.equals("")) {
	    	System.out.println("Enter appropriate names of the java files.");
			
	    }
	    else {
	    stdin.println("cd C:\\methodtracer\\jdk-12.0.1.12-openj9");
	    stdin.println("javac "+str+".java");
	    stdin.println("java "+str);
	    stdin.println(".\\bin\\java -Xtrace:none -Xtrace:methods="+str+",trigger=method{"+str+",jstacktrace},maximal=mt,output="+str+"Trace.trc "+str);
	    stdin.println(".\\bin\\java  com.ibm.jvm.format.TraceFormat "+str+"trace.trc "+str+"logFile");
	  
	    stdin.println("javac "+str2+".java");
	    stdin.println("java "+str2);
	    stdin.println(".\\bin\\java -Xtrace:none -Xtrace:methods="+str2+",trigger=method{"+str2+",jstacktrace},maximal=mt,output="+str2+"Trace2.trc "+str2);
	    stdin.println(".\\bin\\java  com.ibm.jvm.format.TraceFormat "+str2+"trace2.trc "+str2+"logFile2");
	    stdin.close();
	     p.waitFor();
	     
	   
	     
	     System.out.println("\n");
	     //Check Exception
	     File f1 = new File("C:\\methodtracer\\jdk-12.0.1.12-openj9\\AbcdlogFile");
         Scanner sc1 = new Scanner(f1);
     	 File f2 = new File("C:\\methodtracer\\jdk-12.0.1.12-openj9\\AbcdexlogFile2");
         Scanner sc2 = new Scanner(f2);
         
         String class1="null",class2="null";
         int class1Length = 0, class2Length = 0;
         int countStar = 0;
         String line,type, traceEntry;
         Stack<String> jstack1 = new Stack<String>();
         Stack<String> jstack2 = new Stack<String>();
         Stack<String> jstack1Line = new Stack<String>();
         
         //Skipping unnecessary content
         while(sc1.hasNextLine()) {
         	String read = (sc1.nextLine()).trim();	            	
	        class1 = str;
         	class1Length = class1.length();

         	if(read.indexOf("J9 timer(UTC)") == 0) {
         		break;
         	}
         }
         while(sc2.hasNextLine()) {
         	String read = (sc2.nextLine()).trim();         	
	        class2 = str2;
         	class2Length = class2.length();
         
         	if(read.indexOf("J9 timer(UTC)") == 0) {
         		break;
         	}
         }
         
         
         
         //jstacktrace
         while(sc1.hasNextLine()){             
         	 line = sc1.nextLine();
             String [] details1 = line.split("\\s+");             
             type = details1[3];             
             traceEntry =details1[4];         
           
             
             if(type.equals("Event")== true && traceEntry.equals("jstacktrace:")== false){
             	String methodName = (details1[5]).substring(class1Length+1);                
             	if(jstack1.search(methodName)<1) {
             		jstack1.push(methodName);
             		jstack1Line.push(details1[6]);
             	}
             }                
             
         }
         while(sc2.hasNextLine()){
         	line = sc2.nextLine();
             String [] details1 = line.split("\\s+");
             type = details1[3];            
             traceEntry =details1[4];
             
             if(type.equals("Event")== true && traceEntry.equals("jstacktrace:")== false){
             	String methodName = (details1[5]).substring(class2Length+1);
             	if(jstack2.search(methodName)<1) {
             		jstack2.push(methodName);
             	}
             }
             
           //Count number of stars to check if exception exists
             if(traceEntry.charAt(0) == '*' && type.equals("Debug")== false) {
             	countStar++;
             	break;

             }
         }
         
         //Display whether exception exists or not
        if(countStar==1) {
        	 System.out.println("______________________________Exception  exists___________________________________________");
         }else {
        	 System.out.println("No exception");
         }
        
         System.out.println("------------------------------------------");
         
         //Display the anomalies in the jstacktrace files
         System.out.println("Anomalies in jstacktrace for files\n" +class1+ ".java and "+class2+ ".java :\n");
         while(jstack1.empty() == false) {
         	String s1 = jstack1.peek();
         	String s2 = jstack2.peek();
         	
         	if(s1.equals(s2) == true){            		
         		jstack1.pop();
         		jstack2.pop();
         		jstack1Line.pop();
         		
         	}else{
         		System.out.println(jstack1.peek() +"() " + jstack1Line.peek());
         		jstack1.pop();
         		jstack1Line.pop();
         	}
         }
         System.out.println("-----------------------------------------------------");
         
         sc1.close();
         sc2.close();
	     
         File f = new File("C:\\methodtracer\\jdk-12.0.1.12-openj9\\AbcdlogFile");
         File exp= new File("C:\\methodtracer\\jdk-12.0.1.12-openj9\\AbcdexlogFile2");
         
         Scanner sc = new Scanner(f);
         Scanner sci = new Scanner(exp);
       
         //Skipping unnecessary content
         while(sc.hasNextLine()) {
         	String read = sc.nextLine();
         	if(read.indexOf("J9 timer(UTC)") == 0) {
         		break;
         	}
         }
         while(sci.hasNextLine()) {
         	String rea = sci.nextLine();
         	if(rea.indexOf("J9 timer(UTC)") == 0) {
         		break;
         	}
         }
         
         //String line, timer, thdID, tpID, type, traceEntry;
         String time1="ki", time2="ki";
        		 String tim1="null",tim2="null";
        
         Stack<String> stack = new Stack<>();
         Stack<String> stac = new Stack<>();
         //HashMap<Double,String> hm=new HashMap<Double,String>();  
         System.out.println("______________________________Passing case___________________________________________");
         while(sc.hasNextLine()){               
         	
         	line = sc.nextLine();
             String[] details = line.split("\\s+");
             String timer = (details[0]);                       
             //String thdID = (details[1]);                
             //String tpID= (details[2]);                
             type = (details[3]);            
             traceEntry = (details[4]);
             
             if(type.equals("Entry")==true){
             	stack.push(timer);
             
             }else if(type.equals("Exit")==true){
             	
             	time2=timer;
             	list.add(traceEntry);
             	time1 = stack.peek();
           		//String popResult = stack.pop();
             	
             	//System.out.println();
             	System.out.println("\n"+traceEntry);
             	System.out.println("Entry Time = "+time1);                	
             	System.out.println("Exit Time = "+time2);                	
                 
                 String dateStart = time1;
             	String dateStop = time2;
             	
             	String h1,m1,s1;
             	h1=null;
             	m1=null;
             	s1=null;                	
             	char[] ch=dateStart.toCharArray();
             	char []hr={ch[0],ch[1]};
             	char []min={ch[3],ch[4]};
             	
             	h1=new String(hr);
             	m1=new String(min);
             	s1 = dateStart.substring(6);
             	
             	String h2,m2,s2;
             	h2=null;
             	m2=null;
             	s2=null;
             	char[] c=dateStop.toCharArray();
             	char []hrl={c[0],c[1]};
             	char []minl={c[3],c[4]};
             	
             	h2=new String(hrl);
             	m2=new String(minl);
             	s2 = dateStop.substring(6);
             	
             	double d1=Double.parseDouble(s1) + (Double.parseDouble(h1))*3600 + (Double.parseDouble(m1))*60;
             	double d2=Double.parseDouble(s2)+ (Double.parseDouble(h2))*3600 + (Double.parseDouble(m2))*60;
             	
             	double timetaken=d2-d1;
         
             	
             	System.out.println("Execution Time = "+timetaken+" seconds");
             	
             	String methodname;
             	
             	if(traceEntry.charAt(0)=='*'){
             		methodname=traceEntry.substring(1);
             	}else{
             		methodname=traceEntry;
             	}
             	
             	hm.put(timetaken,methodname);  
             }
         }
         
         System.out.println("\n______________________________Failing  case___________________________________________");
         while(sci.hasNextLine()){                    
             	
             	String lin = sci.nextLine();
                 String[] detail = lin.split("\\s+");
                 String time = (detail[0]);                           
                 //String thdI = (detail[1]);                    
                 //String tpI= (detail[2]);                    
                 String typ = (detail[3]);                  
                 String traceEntr = (detail[4]);
                 
                 if(typ.equals("Entry")==true){
                 	stac.push(time);
                 }
                 if(typ.equals("Exit")==true){
                 	lis.add(traceEntr);
                 	tim2=time;
                 	tim1 = stac.peek();
                 	String popResul = stac.pop();
                 	System.out.println("\n"+traceEntr);
                 	System.out.println("Entry Time = "+tim1);
                 	System.out.println("Exit Time = "+tim2);
                     String dateStar = tim1;
                 	String dateSto = tim2;
                 	String h1,m1,s1;
                 	h1=null;
                 	m1=null;
                 	s1=null;
                 	char[] ch=dateStar.toCharArray();
                 	char []hr={ch[0],ch[1]};
                 	char []min={ch[3],ch[4]};
                 	
                 	h1=new String(hr);
                 	m1=new String(min);
                 	s1=dateStar.substring(6);
                 	
                 	String h2,m2,s2;
                 	h2=null;
                 	m2=null;
                 	s2=null;
                 	char[] c=dateSto.toCharArray();
                 	char []hrl={c[0],c[1]};
                 	char []minl={c[3],c[4]};
                 	
                 	h2=new String(hrl);
                 	m2=new String(minl);
                 	s2=dateSto.substring(6);
                 	
                 	double d1=Double.parseDouble(s1);
                 	double d2=Double.parseDouble(s2);  
                 	
                 	double timetaken=d2-d1;
                 	
                 	System.out.println("Execution Time =  "+timetaken+ " seconds");
                 	
                 	String methodname;
                 	
                 	if(traceEntr.charAt(0)=='*')
                 	{
                 		methodname=traceEntr.substring(1);
                 	}
                 	else{
                 		methodname=traceEntr;
                 	}
                 	hmm.put(timetaken,methodname);  
                 }
             
             /*for(Map.Entry m:hm.entrySet()){  
             	   System.out.println(m.getKey()+" "+m.getValue());  
             	  } */ 
         }
                     
         
       //Comparing Files passing and failing, and analysing if method takes longer time to execute
         Iterator<Map.Entry<Double, String>> itr = hmm.entrySet().iterator(); //passing
         Iterator<Map.Entry<Double, String>> it = hm.entrySet().iterator(); //failing
         int flag=0;
         while(itr.hasNext()) 
         { 
         	Map.Entry<Double, String> entry = itr.next();
     		String nm=entry.getValue();
     		double pass=entry.getKey();
         	while(it.hasNext()) 
             { 
         		Map.Entry<Double, String> entr = it.next();
         		String name=entr.getValue();
         		String a,b;
         		a=null;
         		b=null;
         		double fail=entr.getKey();
         		
         		//System.out.println("Value fail"+name+" Value pass "+nm);
         		
         		int i;
         		for(i=0;i<name.length();i++){
         			if(name.charAt(i)=='.'){
         				a=name.substring(i+1);
         			}
         		}
         		int j;
         		for(j=0;j<nm.length();j++){
         			if(nm.charAt(j)=='.')
         			{
         				b=nm.substring(j+1);
         			}
         		}
         		
         		
             	if(a.equals(b)==true){
             		
             		double diff=pass - fail;
             		 System.out.println("-----------------------------------------------------------------------------");
             		System.out.println("\nPassing case takes "+ diff + " seconds more to execute.");
             		 System.out.println("-----------------------------------------------------------------------------");
             	
             		System.out.println();
             		flag=1;
             		break;
             	}
             	
             	
             }
         	
         	if(flag==1){
         		break;
         	}
         	
         	}
         
         countFrequencies(list); 
         sc.close();
         sci.close();
	    }
		} catch (Exception e) {
	 		e.printStackTrace();
		}
		
		
	}
	//file handling for bringing the value of passing and failing file from IbmGuiWindow
	public static String[] getFileNames() {
	  File file = new File("C:\\Users\\tanmay\\eclipse-workspace\\MTA\\src\\filenames.txt"); 
	  String[] filenames = new String[2];
	  BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String st = br.readLine();
					filenames = st.split(" ");
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			return filenames;
		}
	}
	
	
}
