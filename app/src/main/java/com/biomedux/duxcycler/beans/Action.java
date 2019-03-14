// ============================================================
// FileName		: Action.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.beans;

public class Action {

	// ============================================================
	// Constants
	// ============================================================

	// ============================================================
	// Fields
	// ============================================================

	private String label;
	private String temp;
	private String time;

	// ============================================================
	// Constructors
	// ============================================================

	public Action() {
		this("", "", "");
	}

	public Action(String label, String temp, String time) {
		this.label = label;
		this.temp = temp;
		this.time = time;
	}

	// ============================================================
	// Getter & Setter
	// ============================================================

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================

	@Override
	public Action clone() {
		Action action = new Action();

		action.label = label;
		action.temp = temp;
		action.time = time;

		return action;
	}

	// ============================================================
	// Methods
	// ============================================================

	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================

}
