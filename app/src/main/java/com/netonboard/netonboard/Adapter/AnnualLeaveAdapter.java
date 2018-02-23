package com.netonboard.netonboard.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netonboard.netonboard.Object.AnnualLeave;
import com.netonboard.netonboard.R;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by yipfeilee on 14/02/2018.
 */

public class AnnualLeaveAdapter extends TableDataAdapter<AnnualLeave> {
    final static int TEXT_SIZE = 14;

    public AnnualLeaveAdapter(Context context, List<AnnualLeave> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        AnnualLeave annualLeave = getRowData(rowIndex);
        View renderview = null;

        switch (columnIndex) {
            case 0:
                renderview = renderString(annualLeave.getS_date_apply());
                break;
            case 1:
                renderview = renderString(annualLeave.getS_leave_remark());
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
