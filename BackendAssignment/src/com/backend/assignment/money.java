package com.backend.assignment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class money{
	
	private static Queue<String> endMsgs=new LinkedList<>();
	public static volatile int custThreads=0;
	
	public static synchronized void addEndMsg(String str){
		endMsgs.add(str);
	}
	public static void main(String[] args){
		ArrayList<bank> banks=null;
		ArrayList<customer> customers=null;
		BlockingQueue<String> queue = new LinkedBlockingQueue<>(20);
		try{
			BufferedReader bufferedReader=new BufferedReader(new FileReader("banks.txt"));
			String line=bufferedReader.readLine();
			banks=new ArrayList<>();
			while(line!=null  &&  line.trim().length()>0){
				line=line.trim();
				String[] subStrs=line.substring(1,line.length()-2).split(",");
				bank newBank=new bank(subStrs[0],(int)Integer.parseInt(subStrs[1]),queue);
				banks.add(newBank);
				line=bufferedReader.readLine();
			}
			
			
			bufferedReader=new BufferedReader(new FileReader("customers.txt"));
			line=bufferedReader.readLine();
			customers=new ArrayList<>();
			while(line!=null  &&  line.trim().length()>0){
				line=line.trim();
				String[] subStrs=line.substring(1,line.length()-2).split(",");
				customer newCustomer=new customer(subStrs[0],(int)Integer.parseInt(subStrs[1]),banks,queue);
				customers.add(newCustomer);
				line=bufferedReader.readLine();
				++custThreads;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(banks!=null  &&  banks.size()>0  &&  customers!=null  &&  customers.size()>0){
			
			System.out.println("** Customers and loan objectives **");
			for(customer loopCustomer:customers)				System.out.println(loopCustomer.name+": "+loopCustomer.custFund);
			
			System.out.println("\n** Banks and financial resources **");
			for(bank loopBank:banks){
				System.out.println(loopBank.name+": "+loopBank.bankFund);
				loopBank.bankThread.start();
			}
			
			for(customer loopCustomer:customers)				loopCustomer.custThread.start();
			
			System.out.println("");
		}else{
			System.out.println("Please provide atleast one record of each banks and customers..");
		}
			
		try{
			while(custThreads!=0){
				String str=queue.take();
				if(str.indexOf(" 0 dollars")<0)					System.out.println(str);
			}
		}catch(Exception e){}
		System.out.println("");
		for(bank loopBank:banks){
			loopBank.interruptCall();
		}
		
		while(!endMsgs.isEmpty())							System.out.println(endMsgs.remove());
	}
}
