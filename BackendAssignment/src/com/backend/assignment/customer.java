package com.backend.assignment;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class customer implements Runnable{
	
	ArrayList<bank> banks=null;
	public String name=null;
	public final int ACTUAL_FUND;
	public volatile int custFund;
	public volatile int acceptedRejected;
	public Thread custThread=null;
	private BlockingQueue<String> queue = null;
	
	public customer(String name,int custFund,ArrayList<bank> banks,BlockingQueue<String> queue){
		this.banks=new ArrayList<>(banks);
		this.name=name;
		this.custFund=custFund;
		this.ACTUAL_FUND=custFund;
		this.acceptedRejected=-1;
		this.queue=queue;
		this.custThread=new Thread(this,this.name+"Thread");
	}
	private void display(String str){
		try{
			queue.put(str);
		}catch(Exception e){}
	}
	public void consume(int amt,int selectedBank) throws InterruptedException{
		bank bankObj=banks.get(selectedBank);
		synchronized(bankObj.changedAmt){
			while(bankObj.changedAmt.get(0)!=0){
				bankObj.changedAmt.wait();
			}
			bankObj.changedAmt.clear();
			bankObj.changedAmt.add(amt);
			bankObj.customerOperating=this;
			display(this.name+" requests a loan of "+bankObj.changedAmt.get(0)+" dollar(s) from "+bankObj.name);
			Thread.sleep(100);
			bankObj.changedAmt.notify();
			
		}	
		while(acceptedRejected<0){
			Thread.sleep(1);
		}
		if(acceptedRejected==1)						custFund-=amt;
		else										banks.remove(selectedBank);
		acceptedRejected=-1;
	}
	
	@Override
	public void run(){
		try{
			Thread.sleep(100);
		}catch(Exception e){}
		while(custFund>0  &&  banks.size()>0){
			try{
				Random rand = new Random();
				Thread.sleep(rand.nextInt(90)+10);
				int maxVal=custFund<50?custFund:50;
				consume(rand.nextInt(maxVal)+1,rand.nextInt(banks.size()));
			}catch(Exception e){e.printStackTrace();}
			
		}
		if(this.custFund==0)						money.addEndMsg(this.name+" has reached the objective of "+this.ACTUAL_FUND+" dollar(s). Woo Hoo!");
		else										money.addEndMsg(this.name+" was only able to borrow "+(this.ACTUAL_FUND-this.custFund)+" dollar(s). Boo Hoo!");
		
		if(money.custThreads>0)						--money.custThreads;	
	}
}
