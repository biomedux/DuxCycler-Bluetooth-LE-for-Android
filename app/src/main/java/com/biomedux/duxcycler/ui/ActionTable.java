// ============================================================
// FileName		: ActionTable.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import com.biomedux.duxcycler.beans.Action;
import com.biomedux.duxcycler.util.Util;

public class ActionTable extends TableLayout {

	// ============================================================
	// Constants
	// ============================================================

	public static final int HEADER_LENGTH = 4;

	public static final int LABEL	= 0;
	public static final int TEMP		= 1;
	public static final int TIME		= 2;
	public static final int REMAIN	= 3;

	private static final int TEXT_COLOR				= 0xFF696969;
	private static final int COLOR_ROW_DEFAULT		= 0xFFFFFFFF;
	private static final int COLOR_ROW_REVERSE		= 0xFFEBEBEB;
	private static final int COLOR_ROW_SELECTED		= 0xFFD1D1D1;

	// ============================================================
	// Fields
	// ============================================================

	private Activity activity;
	private OnClickListener onClickListener;

	private ArrayList<TableRow> tableRows;
	private ArrayList<TextView[]> textViews;

	private boolean toggle;
	private int length;

	// ============================================================
	// Constructors
	// ============================================================

	public ActionTable(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// ============================================================
	// Getter & Setter
	// ============================================================

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public int getLength() {
		return length;
	}

	public String getItem(int row, int col) {
		return textViews.get(row)[col].getText().toString();
	}

	public void setItem(int row, int col, String text) {
		textViews.get(row)[col].setText(text);
	}

	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================

	// ============================================================
	// Methods
	// ============================================================

	public void initial(Activity activity) {
		this.activity = activity;
		clear();
	}

	private void insert(TableRow tableRow, TextView[] textViews, String[] items) {
		TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
		int color = length % 2 == 0 ? COLOR_ROW_DEFAULT : COLOR_ROW_REVERSE;

		tableRow.setBackgroundColor(color);

		for (int i = 0; i < items.length; i++) {
			textViews[i].setLayoutParams(params);
			textViews[i].setTextSize(15);
			textViews[i].setGravity(Gravity.CENTER);
			textViews[i].setTextColor(TEXT_COLOR);
			textViews[i].setText(items[i]);
			tableRow.addView(textViews[i]);
		}

		addView(tableRow);
	}

	public void update(ArrayList<Action> actions) {
		clear();

		for (int i = 0; i < actions.size(); i++) {
			TableRow tableRow = new TableRow(activity);
			TextView[] textView = new TextView[HEADER_LENGTH];
			String[] items = new String[HEADER_LENGTH];

			tableRow.setOnClickListener(onClickListener);
			tableRow.setId(tableRows.size());

			for (int j = 0; j < HEADER_LENGTH; j++)
				textView[j] = new TextView(activity);

			items[0] = actions.get(i).getLabel();
			items[1] = actions.get(i).getTemp();
			items[2] = items[0].equals("GOTO") ? actions.get(i).getTime() : Util.toHMS(Integer.parseInt(actions.get(i).getTime()));
			items[2] = items[2].equals("0s") ? "∞" : items[2];
			items[3] = "";

			insert(tableRow, textView, items);
			this.tableRows.add(tableRow);
			this.textViews.add(textView);
			length++;
		}
	}

	public void clear() {
		removeAllViews();
		this.tableRows = new ArrayList<TableRow>();
		this.textViews = new ArrayList<TextView[]>();
		length = 0;
	}

	public void selection(int index) {
		for (int i = 0; i < length; i++)
			tableRows.get(i).setBackgroundColor(i % 2 == 0 ? COLOR_ROW_DEFAULT : COLOR_ROW_REVERSE);

		if (index < 0 || index >= length)
			return;

		for (int i = 0; toggle && i < 4; i++)
			tableRows.get(index).setBackgroundColor(COLOR_ROW_SELECTED);
		toggle = !toggle;
	}

	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================

}
