// ============================================================
// FileName		: ListAdapter.java
// Author		: JaeHong Min
// Date			: 2017.07.04
// ============================================================

package com.biomedux.duxcycler.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.biomedux.duxcycler.R;
import com.biomedux.duxcycler.beans.Protocol;

public class ListAdapter extends ArrayAdapter<Protocol> {

    // ============================================================
    // Constants
    // ============================================================

    // ============================================================
    // Fields
    // ============================================================

    private ArrayList<Protocol> protocols;

    // ============================================================
    // Constructors
    // ============================================================

    public ListAdapter(Context context, int textViewResourceId, ArrayList<Protocol> protocols) {
        super(context, textViewResourceId, protocols);
        this.protocols = protocols;
    }

    // ============================================================
    // Getter & Setter
    // ============================================================

    // ============================================================
    // Methods for/from SuperClass/Interfaces
    // ============================================================

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.adapter_protocol, null);
        }

        String title = protocols.get(position).getTitle();

        if (title != null) {
            TextView txtItem = (TextView) view.findViewById(R.id.ProtocolAdapter_TextView);
            txtItem.setText(title);
        }

        return view;
    }

    // ============================================================
    // Methods
    // ============================================================

    // ============================================================
    // Inner and Anonymous Classes
    // ============================================================

}
