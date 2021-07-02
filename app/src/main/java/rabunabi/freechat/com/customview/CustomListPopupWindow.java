package rabunabi.freechat.com.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import rabunabi.freechat.com.R;
import rabunabi.freechat.com.view.home.ui.search.CustomSpinnerAdapter;

public class CustomListPopupWindow extends ListPopupWindow {
    private Context context;
    private TextView tvDisplaySource;
    private CustomSpinnerAdapter spinnerAdapter;
    private String dataSelected;

    public CustomListPopupWindow(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomListPopupWindow(Context context, TextView tvDisplaySource, CustomSpinnerAdapter spinnerAdapter, String dataSelected) {
        super(context);
        this.context = context;
        this.tvDisplaySource = tvDisplaySource;
        this.spinnerAdapter = spinnerAdapter;
        this.dataSelected = dataSelected;
        inIt();
    }

    public CustomListPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomListPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public CustomListPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

    }

    private void inIt() {
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.backgroundColor)));
        setAnchorView(tvDisplaySource);
        setAdapter(spinnerAdapter);
        setModal(true);

        int index = 0;
        for (int i = 0; i < spinnerAdapter.mList.size(); i++) {
            if (spinnerAdapter.mList.get(i).equals(dataSelected)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            setSelection(index);
        }
    }


}
