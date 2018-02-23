package com.netonboard.netonboard.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netonboard.netonboard.Object.UnpaidLeave;
import com.netonboard.netonboard.R;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;


/**
 * Created by yipfeilee on 14/02/2018.
 */

public class UnpaidLeaveAdapter extends TableDataAdapter<UnpaidLeave> {

    final static int TEXT_SIZE = 14;

    public UnpaidLeaveAdapter(Context context, List<UnpaidLeave> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        UnpaidLeave unpaidLeave = getRowData(rowIndex);
        View renderview = null;

        switch (columnIndex) {
            case 0:
                renderview = renderString(unpaidLeave.getS_date_apply());
                break;
            case 1:
                renderview = renderString(unpaidLeave.getS_leave_remark());
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
