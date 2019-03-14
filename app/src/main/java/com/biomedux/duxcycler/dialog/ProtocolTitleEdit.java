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
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import com.biomedux.duxcycler.R;
import com.biomedux.duxcycler.beans.Protocol;

public class ProtocolTitleEdit implements DialogMessage {

	// ============================================================
	// Constants
	// ============================================================

	// ============================================================
	// Fields
	// ============================================================

	private static ProtocolTitleEdit instance = null;

	private Activity activity;
	private View view;

	private ArrayList<Protocol> protocols;
	private int targetProtocolIndex;

	private Handler handler;

	private int editMode;

	// ============================================================
	// Constructors
	// ============================================================

	// ============================================================
	// Getter & Setter
	// ============================================================

	public static ProtocolTitleEdit getInstance() {
		if (instance == null)
			instance = new ProtocolTitleEdit();
		return instance;
	}

	// ============================================================
	// Methods for/from SuperClass/Interfaces
	// ============================================================

	// ============================================================
	// Methods
	// ============================================================

	public void initial(Activity activity, ArrayList<Protocol> protocols, Handler handler) {
		this.activity = activity;
		this.protocols = protocols;
		this.handler = handler;
	}

	public void showEditDialog(int targetProtocolIndex, boolean editMode) {
		this.targetProtocolIndex = targetProtocolIndex;
		this.editMode = editMode ? 1 : 0;

		view = activity.getLayoutInflater().inflate(R.layout.dialog_simple_edit, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		EditText etxtTitle = (EditText) view.findViewById(R.id.DialogSimpleEdit_EditText);
		if (editMode) {
			String title = "Untitled-1";
			int titleCount = 2;

			for (int i = 0; i < protocols.size(); i++) {
				if (protocols.get(i).getTitle().equals(title)) {
					title = "Untitled-" + titleCount++;
					i = 0;
				}
			}
			etxtTitle.setText(title);
		} else {
			etxtTitle.setText(protocols.get(targetProtocolIndex).getTitle());
		}

		builder.setTitle("Protocol Title Edit");
		builder.setView(view);
		builder.setPositiveButton("Ok", ok);
		builder.setNegativeButton("Cancel", null);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	// ============================================================
	// Inner and Anonymous Classes
	// ============================================================

	final DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText etxtTitle = (EditText) view.findViewById(R.id.DialogSimpleEdit_EditText);
			handler.obtainMessage(DIALOG_PROTOCOL_TITLE_EDIT, targetProtocolIndex, editMode, etxtTitle.getText().toString()).sendToTarget();
		}
	};
}
