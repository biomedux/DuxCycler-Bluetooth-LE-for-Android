package com.biomedux.duxcycler.util;

public abstract class Handler {
	
	// ============================================================
	// Constants
	// ============================================================
	
	// ============================================================
	// Fields
	// ============================================================
	
	// ============================================================
	// Constructors
	// ============================================================
	
	// ============================================================
	// Getter & Setter
	// ============================================================
	
	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================
	
	// ============================================================
	// Methods
	// ============================================================
	
	public final Object send() {
		return message(new Message(0, 0, null, null));
	}
	
	public final Object send(int msg) {
		return message(new Message(msg, 0, null, null));
	}
	
	public final Object send(int msg, int arg) {
		return message(new Message(msg, arg, null, null));
	}
	
	public final Object send(int msg, Object obj) {
		return message(new Message(msg, 0, obj, null));
	}
	
	public final Object send(int msg, int arg, Object obj) {
		return message(new Message(msg, arg, obj, null));
	}
	
	public final Object send(int msg, int arg, Object obj0, Object obj1) {
		return message(new Message(msg, arg, obj0, obj1));
	}
	
	public abstract Object message(Message msg);
	
	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================
	
	public class Message {
		public int msg;
		public int arg;
		public Object obj0;
		public Object obj1;
		
		public Message(int msg, int arg, Object obj0, Object obj1) {
			this.msg = msg;
			this.arg = arg;
			this.obj0 = obj0;
			this.obj1 = obj1;
		}
	}
}
