package com.rabunabi.friends.view.home.ui.search;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.rabunabi.friends.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private TextView tvSource;
    public List<String> mList;

    public CustomSpinnerAdapter(@NonNull Context context, int resource,
                                @NonNull List<String> objects, TextView tvSource) {
        super(context, resource, objects);
        this.tvSource = tvSource;
        mList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(getItem(position));
        view.setTextColor(ContextCompat.getColor(getContext(),
                getItem(position).equalsIgnoreCase(tvSource.getText().toString())
                        ? R.color.colorPrimary : R.color.black));
        return view;
    }
}
