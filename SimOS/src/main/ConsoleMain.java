package main;

import multilevel.MultiLevel;
import roundrobin.RoundRobin;
import simos.*;
import simos.Process;
import simos.OS.OSEventListener;
import srjf.SRJF;

public class ConsoleMain {

	public static void main(String[] args) {
		
		System.out.println("Init System");
		
		OS os = new OS();
		
		createListener(os);
		
		Planner planner1 = new MultiLevel(true, false);
		Processor p1 = new Processor("Processor 1", planner1);
		planner1.addAlgorithm(new RoundRobin());
		planner1.addAlgorithm(new SRJF());
		planner1.addAlgorithm(new Algorithm());
		
		Planner planner2 = new MultiLevel(true, false);
		Processor p2 = new Processor("Processor 2", planner2);
		planner2.addAlgorithm(new RoundRobin());
		planner2.addAlgorithm(new SRJF());
		planner2.addAlgorithm(new Algorithm());
		
		os.addProcessor(p1);
		os.addProcessor(p2);
		
		os.addResource("Screen");
		os.addResource("Printer");
		
		/* allocate processes for processor one */
		
		p1.addProcess(new Process("process1", 4, new String[]{"Printer"}, 1));

		p1.addProcess(new Process("process2", 3, new String[]{"Screen"}, 2));
		p1.addProcess(new Process("process3", 4, new String[]{"Screen"}, 2));
		
		p1.addProcess(new Process("process4", 2, new String[]{"Screen", "Printer"}, 3));
		
		/* allocate processes for processor two */
		
		p2.addProcess(new Process("process5", 6, new String[]{"Screen"}, 1));

		p2.addProcess(new Process("process6", 3, new String[]{"Printer"}, 2));
		p2.addProcess(new Process("process7", 4, new String[]{"Printer"}, 2));
		
		p2.addProcess(new Process("process8", 2, new String[]{"Screen"}, 3));
		
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
	
	static void createListener(OS os) {
		
		OSEventListener console = new OSEventListener() {

			@Override
			public void onRunProcess(Process pr, Processor p) {
				System.out.println(p);
			}
			
			@Override
			public void onEndProcess(Process pr) {
				System.out.println("ended " + pr.getName());
			}
			

			@Override
			public void onSuspendProcess(Process pr, Processor p) {
				System.out.println("suspendend " + pr.getName());				
			}
			
			@Override
			public void onAddResource(Resource r) {
				System.out.println("new resource " + r);
			}
			
			@Override
			public void onAddProcess(Process pr, Processor p, Algorithm alg) {
				System.out.println("Added: " + pr.getName() + " to " + alg);
			}

			@Override
			public void onBlockProcess(Process pr, Processor p) {
				System.out.println("Blocked: " + pr.getName());
				
			}

			@Override
			public void onActiveResource(Resource r, Process pr) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFreeResource(Resource r) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBlockResource(Resource r) {
				// TODO Auto-generated method stub
				
			}
			
			
		};
		
		os.addListener(console);
	}
}
