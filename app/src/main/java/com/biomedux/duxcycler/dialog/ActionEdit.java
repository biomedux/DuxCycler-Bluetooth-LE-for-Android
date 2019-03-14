// ============================================================
// FileName		: ActionEdit.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import com.biomedux.duxcycler.R;
import com.biomedux.duxcycler.beans.Action;
import com.biomedux.duxcycler.ui.ActionTable;

public class ActionEdit implements DialogMessage {

	// ============================================================
	// Constants
	// ============================================================

	// ============================================================
	// Fields
	// ============================================================

	private static ActionEdit instance = null;

	private Activity activity;
	private View view;

	private ActionTable actionTable;
	private ArrayList<Action> actions;
	private int selectIndex;

	private Handler handler;

	// ============================================================
	// Constructors
	// ============================================================

	// ============================================================
	// Getter & Setter
	// ============================================================

	public static ActionEdit getInstance() {
		if (instance == null)
			instance = new ActionEdit();
		return instance;
	}

	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================

	// ============================================================
	// Methods
	// ============================================================

	public void initial(Activity activity, ActionTable listTable, Handler handler) {
		this.activity = activity;
		this.actionTable = listTable;
		this.handler = handler;
	}

	public void showAddDialog(ArrayList<Action> actions) {
		this.actions = actions;
		this.selectIndex = -1;

		view = activity.getLayoutInflater().inflate(R.layout.dialog_action_edit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		EditText etxtLabel = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Label);
		etxtLabel.setText(Integer.toString(findCurrentIndex(actions.size()) + 1));

		builder.setTitle("Action Edit");
		builder.setView(view);
		builder.setPositiveButton("Add", add);
		builder.setNegativeButton("Cancel", null);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void showEditDialog(ArrayList<Action> actions, int selectIndex) {
		this.actions = actions;
		this.selectIndex = selectIndex;

		view = activity.getLayoutInflater().inflate(R.layout.dialog_action_edit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		EditText etxtLabel = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Label);
		EditText etxtTemp = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Temp);
		EditText etxtTime = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Time);
		CheckBox cboxGoto = (CheckBox) view.findViewById(R.id.DialogActionEdit_CheckBox_GOTO);

		etxtLabel.setText(actions.get(selectIndex).getLabel());
		etxtTemp.setText(actions.get(selectIndex).getTemp());
		etxtTime.setText(actions.get(selectIndex).getTime());
		cboxGoto.setChecked(actions.get(selectIndex).getLabel().equals("GOTO"));

		etxtLabel.setEnabled(false);

		builder.setTitle("Action Edit");
		builder.setView(view);
		builder.setPositiveButton("Ok", ok);
		builder.setNeutralButton("Delete", delete);
		builder.setNegativeButton("Cancel", null);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private int findCurrentIndex(int index) {
		if (index == 0)
			return 0;

		if (actions.get(index - 1).getLabel().equals("GOTO"))
			return Integer.parseInt(actions.get(index - 2).getLabel());

		return  Integer.parseInt(actions.get(index - 1).getLabel());
	}

	private int findActionIndex(String label) {
		for (int i = 0; i < actions.size(); i++) {
			if (actions.get(i).getLabel().equals(label)) {
				return i;
			}
		}

		return -1;
	}

	private boolean checkEmpty(String[] items) {
		String[] tag = {"Label", "Temp", "Time"};

		for (int i = 0; i < 3; i++) {
			if (items[i].isEmpty()) {
				Toast.makeText(activity
						, "failed: " + tag[i] + " is empty."
						, Toast.LENGTH_SHORT).show();
				return true;
			}
		}

		return false;
	}

	private boolean checkGoto(String temp) {
		int pointingIndex = findActionIndex(temp);
		int gotoIndex = selectIndex;

		if (gotoIndex == -1)
			gotoIndex = actions.size();

		if (pointingIndex == -1) {
			Toast.makeText(activity
					, "failed: Action could not be found. (" + temp + ")"
					, Toast.LENGTH_SHORT).show();
			return true;
		}

		if (pointingIndex >= gotoIndex) {
			Toast.makeText(activity
					, "failed: You can not go back. (" + temp + ")"
					, Toast.LENGTH_SHORT).show();
			return true;
		}

		for (int i = pointingIndex + 1; i < actions.size(); i++) {
			if (actions.get(i).getLabel().equals("GOTO")) {
				if (i < gotoIndex || (gotoIndex != i && gotoIndex > findActionIndex(actions.get(i).getTemp()))) {
					Toast.makeText(activity
							, "failed: Goto has been overlaps."
							, Toast.LENGTH_SHORT).show();
					return true;
				}
			}
		}

		return false;
	}

	private void revision(int index, boolean negative) {
		if (actions.get(index).getLabel().equals("GOTO"))
			return;

		for (int i = index + 1; i < actions.size(); i++) {
			if (actions.get(i).getLabel().equals("GOTO")) {
				int temp = Integer.parseInt(actions.get(i).getTemp());
				temp += negative ? (index < temp - 1 ? -1 : 0) : (index < temp - 1 ? 1 : 0);
				actions.get(i).setTemp(Integer.toString(temp));
			} else {
				int label = Integer.parseInt(actions.get(i).getLabel());
				actions.get(i).setLabel(Integer.toString(label + (negative ? -1 : 1)));
			}
		}
	}

	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================

	final DialogInterface.OnClickListener add = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText etxtLabel = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Label);
			EditText etxtTemp = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Temp);
			EditText etxtTime = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Time);
			CheckBox cboxGoto = (CheckBox) view.findViewById(R.id.DialogActionEdit_CheckBox_GOTO);

			String sLabel = etxtLabel.getText().toString();
			String sTemp = etxtTemp.getText().toString();
			String sTime = etxtTime.getText().toString();
			boolean isGoto = cboxGoto.isChecked();
			int iLabel, iTemp, iTime;

			int currentIndex = findCurrentIndex(actions.size());

			if (checkEmpty(new String[]{sLabel, sTemp, sTime}))
				return;

			iLabel = (int) Double.parseDouble(sLabel);
			iTemp = (int) Double.parseDouble(sTemp);
			iTime = (int) Double.parseDouble(sTime);

			sLabel = Integer.toString(iLabel);
			sTemp = Integer.toString(iTemp);
			sTime = Integer.toString(iTime);

			if (isGoto) {
				if (iLabel < 2 || iLabel - 1 > currentIndex) {
					Toast.makeText(activity
							, "failed: Goto can not add to that location. (" + sLabel + ")"
							, Toast.LENGTH_SHORT).show();
					return;
				}

				if (checkGoto(sTemp))
					return;
			}

			if (currentIndex == iLabel - 1) {
				actions.add(new Action(isGoto ? "GOTO" : sLabel, sTemp, sTime));
			} else if (currentIndex > iLabel - 1 && currentIndex > 0) {
				int index = findActionIndex(sLabel);
				actions.add(index, new Action(isGoto ? "GOTO" : sLabel, sTemp, sTime));
				revision(index, false);
			} else {
				Toast.makeText(activity
						, "failed: Can not add to that location. (" + sLabel + ")"
						, Toast.LENGTH_SHORT).show();
				return;
			}

			handler.sendEmptyMessage(DIALOG_ACTION_EDIT);
		}
	};

	final DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText etxtTemp = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Temp);
			EditText etxtTime = (EditText) view.findViewById(R.id.DialogActionEdit_EditText_Time);
			CheckBox cboxGoto = (CheckBox) view.findViewById(R.id.DialogActionEdit_CheckBox_GOTO);

			String sLabel = actions.get(selectIndex).getLabel();
			String sTemp = etxtTemp.getText().toString();
			String sTime = etxtTime.getText().toString();
			boolean isGoto = cboxGoto.isChecked();
			int iTemp, iTime;

			if (checkEmpty(new String[]{sLabel, sTemp, sTime}))
				return;

			iTemp = (int) Double.parseDouble(sTemp);
			iTime = (int) Double.parseDouble(sTime);

			sTemp = Integer.toString(iTemp);
			sTime = Integer.toString(iTime);

			if (isGoto) {
				if (checkGoto(sTemp))
					return;
				if (!sLabel.equals("GOTO"))
					revision(selectIndex, true);
			} else {
				if (sLabel.equals("GOTO")) {
					sLabel = Integer.toString(Integer.parseInt(actions.get(selectIndex - 1).getLabel()) + 1);
					actions.get(selectIndex).setLabel(sLabel);
					revision(selectIndex, false);
				}
			}

			actions.set(selectIndex, new Action(isGoto ? "GOTO" : sLabel, sTemp, sTime));

			handler.sendEmptyMessage(DIALOG_ACTION_EDIT);
		}
	};

	final DialogInterface.OnClickListener delete = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

			if (selectIndex + 1 < actions.size()) {
				Action action = actions.get(selectIndex + 1);
				if (action.getLabel().equals("GOTO")) {
					if (action.getTemp().equals(actions.get(selectIndex).getLabel())) {
						actions.remove(selectIndex + 1);
					}
				}
			}

			revision(selectIndex, true);
			actions.remove(selectIndex);

			handler.sendEmptyMessage(DIALOG_ACTION_EDIT);
		}
	};
}
