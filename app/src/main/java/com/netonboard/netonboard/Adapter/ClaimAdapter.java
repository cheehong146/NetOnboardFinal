package com.netonboard.netonboard.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netonboard.netonboard.Object.Claim;
import com.netonboard.netonboard.R;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by yipfeilee on 21/02/2018.
 */

public class ClaimAdapter extends TableDataAdapter<Claim> {
    final static int TEXT_SIZE = 14;

    public ClaimAdapter(Context context, List<Claim> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Claim claim = getRowData(rowIndex);
        View renderview = null;

        switch (columnIndex) {
            case 0:
                renderview = renderString(claim.getD_submit());
                break;
            case 1:
                renderview = renderString(claim.getS_particular());
                break;
            case 2:
                renderview = renderString(claim.getF_total());
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
