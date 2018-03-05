package com.netonboard.netonboard.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netonboard.netonboard.Object.Claim;
import com.netonboard.netonboard.Object.Late;
import com.netonboard.netonboard.R;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by Netonboard on 26/2/2018.
 */

public class LateAdapter extends TableDataAdapter<Late> {
    final static int TEXT_SIZE = 14;

    public LateAdapter(Context context, List<Late> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Late late = getRowData(rowIndex);
        View renderview = null;

        switch (columnIndex) {
            case 0:
                renderview = renderString(late.getDate());
                break;
            case 1:
                renderview = renderString(late.getMinute());
                break;

        }
        return renderview;
    }

    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setTextColor(getResources().getColor(R.color.colorBlackText));
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }
}
