package simos;

import simos.OS.OSEventListener;

public class Resource {
	
	public enum State{ FREE, BUSY, BLOCKED };
	
	String name;
	State state;
	Process process;
	
	OS os;
	
	public Resource(String name) {
		this.name = name;
		this.state = State.FREE;
	}
	
	public boolean isFree() {
		return state == State.FREE;
	}
	
	public void free() {
		
		if (state == State.BLOCKED) {
			return;
		}
		
		state = State.FREE;
		
		for (OSEventListener listener : os.listeners) {
			listener.onFreeResource(name);
		}
		
		process = null;
	}
	
	public void active(Process pr) {
		
		if (state == State.BLOCKED) {
			return;
		}
		
		state = State.BUSY;
		process = pr;
		
		for (OSEventListener listener : os.listeners) {
			listener.onActiveResource(name, pr.name);
		}
	}
	
	public void block() {
		state = State.BLOCKED;
		
		for (OSEventListener listener : os.listeners) {
			listener.onBlockResource(name);
		}
		
		process = null;
	}
}
