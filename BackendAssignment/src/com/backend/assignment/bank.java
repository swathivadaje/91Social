package com.backend.assignment;
import java.util.*;
import java.lang.*;
import java.util.concurrent.BlockingQueue;

public class bank implements Runnable{
	
	private BlockingQueue<String> queue = null;
	public volatile int bankFund=0;
	public final int ACTUAL_FUND;
	public String name=null;
	public volatile List<Integer> changedAmt;
	public customer customerOperating=null;
	public Thread bankThread=null;
	public volatile boolean exit;
	
	public bank(String name,int bankFund,BlockingQueue<String> queue){
		this.name=name;
		this.bankFund=bankFund;
		this.ACTUAL_FUND=bankFund;
		this.changedAmt=new ArrayList<>();
		this.changedAmt.add(0);
		this.exit=true;
		this.queue=queue;
		this.bankThread=new Thread(this,this.name+"Thread");
	}
	public void interruptCall(){
        exit=false;
		try{
			bankThread.interrupt();
		}catch(Exception e){}        
    }
	private void display(String str){
		try{
			queue.put(str);
		}catch(Exception e){}
	}
	public void produce() throws InterruptedException{
		synchronized(changedAmt){
			while(changedAmt.get(0)==0  &&  money.custThreads>0){
				changedAmt.wait();
			}
			int amt=changedAmt.get(0);
			if(bankFund-amt>=0){
				display(this.name+" approves a loan of "+amt+" dollars from "+customerOperating.name);
				customerOperating.acceptedRejected=1;
				bankFund-=amt;
			}else{
				display(this.name+" denies a loan of "+amt+" dollars from "+customerOperating.name);
				customerOperating.acceptedRejected=0;
			}
			changedAmt.clear();
			changedAmt.add(0);
			Thread.sleep(100);
			changedAmt.notifyAll();
			
		}
	}
	
	@Override
	public void run(){
		try{
			Thread.sleep(50);
		}catch(Exception e){}
		while(exit){
			try{
				produce();
			}catch(Exception e){}
			
		}
		money.addEndMsg(this.name+" has "+this.bankFund+" dollar(s) remaining.");
	}
}
