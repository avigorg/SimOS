package main;


import simos.*;
import simos.Process;
import simos.OS.OSEventListener;

public class ConsoleMain {

	public static void main(String[] args) {
		
		System.out.println("Init System");
		
		OS os = new OS();
		
		Planner planner1 = new Planner();
		Algorithm alg1 = new Algorithm();
		planner1.addAlgorithm(alg1);
		Processor p1 = new Processor("Processor 1", planner1, os);
		
		Planner planner2 = new Planner();
		Algorithm alg2 = new Algorithm();
		planner2.addAlgorithm(alg2);
		Processor p2 = new Processor("Processor 2", planner2, os);
		
		os.addProcessor(p1);
		os.addProcessor(p2);
		
		os.addResource("Screen");
		os.addResource("Printer");
		
		Process pr1 = new Process("process1", 2, new String[]{"Screen"});
		Process pr2 = new Process("process2", 2, new String[]{"Screen"});
		Process pr3 = new Process("process3", 2, new String[]{"Printer"});
		
		p1.addProcess(pr1);
		
		p2.addProcess(pr2);
		p2.addProcess(pr3);
		
		OSEventListener console = new OSEventListener() {
			
			@Override
			public void onSuspendProcess(String pr, String p) {
				System.out.println("suspend " + pr);
			}
			
			@Override
			public void onRunProcess(String pr, String p) {
				System.out.println("run " + pr);
				
			}
			
			@Override
			public void onFreeResource(String r) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEndProcess(String pr) {
				System.out.println("Finish " + pr);
			}
			
			@Override
			public void onBlockResource(String r) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onBlockProcess(String pr, String p) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAddResource(String r) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAddProcess(String pr, String p, String alg) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onActiveResource(String r, String pr) {
				// TODO Auto-generated method stub
				
			}
		};
		
		os.addListener(console);
		
		while (true) {
			
			os.execute();
			System.out.println();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
