// ============================================================
// FileName		: Protocol.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Protocol implements Serializable {

	// ============================================================
	// Constants
	// ============================================================

	// ============================================================
	// Fields
	// ============================================================

	private String title;
	private ArrayList<Action> actions;

	// ============================================================
	// Constructors
	// ============================================================

	public Protocol() {
		this("", null);
	}

	public Protocol(String title, ArrayList<Action> actions) {
		this.title = title;
		this.actions = actions;
	}

	// ============================================================
	// Getter & Setter
	// ============================================================

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================

	@Override
	public Protocol clone() {
		Protocol protocol = new Protocol(title, new ArrayList<Action>());

		protocol.title = title;

		for (Action action : actions)
			protocol.actions.add(action.clone());

		return protocol;
	}

	// ============================================================
	// Methods
	// ============================================================

	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================

}
