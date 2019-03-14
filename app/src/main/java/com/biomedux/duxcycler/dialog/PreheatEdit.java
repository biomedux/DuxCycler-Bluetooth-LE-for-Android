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
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.biomedux.duxcycler.R;

public class PreheatEdit implements DialogMessage {

    // ============================================================
    // Constants
    // ============================================================

    // ============================================================
    // Fields
    // ============================================================

    private static PreheatEdit instance = null;

    private Activity activity;
    private View view;

    private Handler handler;

    // ============================================================
    // Constructors
    // ============================================================

    // ============================================================
    // Getter & Setter
    // ============================================================

    public static PreheatEdit getInstance() {
        if (instance == null)
            instance = new PreheatEdit();
        return instance;
    }

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    // ============================================================
    // Methods
    // ============================================================

    public void initial(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    public void showEditDialog(String preheat) {

        view = activity.getLayoutInflater().inflate(R.layout.dialog_simple_edit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        EditText etxtTitle = (EditText) view.findViewById(R.id.DialogSimpleEdit_EditText);
        etxtTitle.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
        etxtTitle.setInputType(InputType.TYPE_CLASS_NUMBER);
        etxtTitle.setSingleLine();
        etxtTitle.setText(preheat);

        builder.setTitle("Preheat Edit");
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
            handler.obtainMessage(DIALOG_PREHEAT_EDIT, etxtTitle.getText().toString()).sendToTarget();
        }
    };
}
