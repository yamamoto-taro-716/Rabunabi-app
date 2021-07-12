package rabunabi.freechat.com.view.home.ui.search;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rabunabi.freechat.com.R;
import rabunabi.freechat.com.customview.CustomListPopupWindow;
import rabunabi.freechat.com.utils.FileUtils;
import rabunabi.freechat.com.utils.GsonUtils;

public class SearchDialog1 extends Dialog {
    private EditText edtNickname;
    private RadioButton radioGenderNone;
    private RadioButton radioGenderMale;
    private RadioButton radioGenderFemale;
    private RadioButton radioNoneAvatar;
    private RadioButton radioAvatar;

    private RadioButton radioUndefine;
    private RadioButton radioFindFriend;
    private RadioButton radioFindLove;
    private RadioButton radioFindHobby;
    private RadioButton radioFindKillingTime;
    private RadioButton radioFindSecret;

    private RadioButton radioMarriageUndefine;
    private RadioButton radioMariageSecret;
    private RadioButton radioUnmarried;
    private RadioButton radioMarried;

    private TextView textViewArea;
    private ImageView btnSearch;
    private TextView edtAgeFrom;
    private TextView edtAgeTo;
    private ImageView imvClose;
    private String mGender = "";
    private String mHasAvatar = "";
    private String mNickname = "";
    private String mLocation = "";
    private String mAgeFrom = "";
    private String mAgeTo = "";
    private String mPurpose = "";
    private String mMarriage = "";
    private String mGenderUser = "";
    private OnSearchUserListener mListener;

    protected List<String> getAge() {
        List<String> arr = new ArrayList<String>();
        arr.add("指定なし");
        for (int i = 18; i <= 80; i++) {
            arr.add("" + i);
        }
        return arr;
    }

    public SearchDialog1(@NonNull Context context, OnSearchUserListener listener) {
        super(context, R.style.DialogTheme);
        mListener = listener;
    }

    public SearchDialog1(@NonNull Context context, String nickname, String gender,
                         String location, String ageFrom, String ageTo, String hasAvatar, String purpose, String marriage,
                         String genderUser,
                         OnSearchUserListener listener) {
        super(context, R.style.DialogTheme);
        mListener = listener;
        this.mNickname = nickname;
        this.mGender = gender;
        this.mLocation = location;
        this.mAgeFrom = ageFrom;
        this.mAgeTo = ageTo;
        this.mHasAvatar = hasAvatar;
        this.mPurpose = purpose;
        this.mMarriage = marriage;
        this.mGenderUser = genderUser;

        System.out.println("DIEP @@@@@ mGenderUser " + mGenderUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search);
        findView();
        bindData();
        initListener();
    }

    private void bindData() {
        if (!TextUtils.isEmpty(mNickname)) {
            edtNickname.setText(mNickname);
        }
        // mGenderUser = 1  nam
        // mGenderUser = 2 nu
        if (!TextUtils.isEmpty(mGender)) {

            if (mGenderUser.equalsIgnoreCase("1")) {
                radioGenderFemale.setChecked(true);
                mGender = "女性";
                // ẩn người chọn ngươi cùng giới
                //  radioGenderMale.setVisibility(View.GONE);
            } else {
                radioGenderMale.setChecked(true);
                mGender = "男性";
                // ẩn người chọn ngươi cùng giới
                // radioGenderFemale.setVisibility(View.GONE);
            }
        } else {
            if (mGender.equalsIgnoreCase("男性")) {
                radioGenderMale.setChecked(true);
                mGender = "男性";
                // ẩn người chọn ngươi cùng giới
                //radioGenderFemale.setVisibility(View.GONE);
            } else if (mGender.equalsIgnoreCase("女性")) {
                radioGenderFemale.setChecked(true);
                mGender = "女性";
                // ẩn người chọn ngươi cùng giới
                //radioGenderMale.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(mLocation)) {
            textViewArea.setText(mLocation);
        }
        if (!TextUtils.isEmpty(mAgeFrom)) {
            edtAgeFrom.setText(mAgeFrom);
        }
        if (!TextUtils.isEmpty(mAgeTo)) {
            edtAgeTo.setText(mAgeTo);
        }

        if (TextUtils.isEmpty(mHasAvatar)) {
        } else if (mHasAvatar.equalsIgnoreCase("0")) {
            radioNoneAvatar.setChecked(true);
        } else if (mHasAvatar.equalsIgnoreCase("1")) {
            radioAvatar.setChecked(true);
        }

        switch (mPurpose) {
            case "指定なし":
                radioUndefine.setChecked(true);
                break;
            case "友達探し":
                radioFindFriend.setChecked(true);
                break;
            case "恋人探し":
                radioFindLove.setChecked(true);
                break;
            case "趣味友探し":
                radioFindHobby.setChecked(true);
                break;
            case "暇つぶし":
                radioFindKillingTime.setChecked(true);
                break;
            case "ヒミツ":
                radioFindSecret.setChecked(true);
                break;
        }
        switch (mMarriage) {
            case "指定なし":
                radioMarriageUndefine.setChecked(true);
                break;
            case "ヒミツ":
                radioMariageSecret.setChecked(true);
                break;
            case "未婚":
                radioUnmarried.setChecked(true);
                break;
            case "既婚":
                radioMarried.setChecked(true);
                break;
        }
    }

    private void findView() {
        edtNickname = findViewById(R.id.edtNickname);
        radioGenderNone = findViewById(R.id.radioNone);
        radioGenderMale = findViewById(R.id.radioMale);
        radioGenderFemale = findViewById(R.id.radioFemale);
        radioNoneAvatar = findViewById(R.id.radioNoneAvatar);
        radioAvatar = findViewById(R.id.radioAvatar);
        textViewArea = findViewById(R.id.textViewArea);
        edtAgeFrom = findViewById(R.id.edtAgeFrom);
        edtAgeTo = findViewById(R.id.edtAgeTo);

        radioUndefine = findViewById(R.id.radioUndefine);
        radioFindFriend = findViewById(R.id.radioFindFriend);
        radioFindLove = findViewById(R.id.radioFindLove);
        radioFindHobby = findViewById(R.id.radiofFindHobby);
        radioFindKillingTime = findViewById(R.id.radioFindKillingTime);
        radioFindSecret = findViewById(R.id.radioFindSecret);

        radioMarriageUndefine = findViewById(R.id.radioMarriageUndefine);
        radioMariageSecret = findViewById(R.id.radioMariageSecret);
        radioUnmarried = findViewById(R.id.radioUnmarried);
        radioMarried = findViewById(R.id.radioMarried);

        imvClose = findViewById(R.id.imvClose);
        btnSearch = findViewById(R.id.btnSearch);
    }

    private void initListener() {
        btnSearch.setOnClickListener(v -> {
            if (mListener != null) {
                mAgeFrom = edtAgeFrom.getText().toString();
                mAgeTo = edtAgeTo.getText().toString();
                if (mAgeFrom.equals("指定なし")) {
                    mAgeFrom = "";
                }
                if (mAgeTo.equals("指定なし")) {
                    mAgeTo = "";
                }
                if (mPurpose.equals("指定なし")) mPurpose = "";
                if (mMarriage.equals("指定なし")) mMarriage = "";
                mNickname = edtNickname.getText().toString().trim();
                mLocation = textViewArea.getText().toString();
                mListener.onDataSelected(mNickname,
                        mGender,
                        mLocation,
                        mAgeFrom,
                        mAgeTo,
                        mHasAvatar,
                        mPurpose,
                        mMarriage);
            }
            dismiss();
        });
        imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        textViewArea.setOnClickListener(v -> {
            showCityPicker(textViewArea);
        });

        radioGenderNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mGender = "";
            }
        });
        radioGenderMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mGender = "男性";
            }
        });
        radioGenderFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mGender = "女性";
            }
        });

        radioUndefine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPurpose = "指定なし";
                uncheckGroupPurpose(2);
            }
        });

        radioFindFriend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPurpose = "友達探し";
                uncheckGroupPurpose(2);
            }
        });

        radioFindLove.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPurpose = "恋人探し";
                uncheckGroupPurpose(2);
            }
        });

        radioFindHobby.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPurpose = "趣味友探し";
                uncheckGroupPurpose(1);
            }
        });

        radioFindKillingTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPurpose = "暇つぶし";
                uncheckGroupPurpose(1);
            }
        });

        radioFindSecret.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPurpose = "ヒミツ";
                uncheckGroupPurpose(1);
            }
        });

        radioMarriageUndefine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMarriage = "指定なし";
                uncheckGroupMarriage(2);
            }
        });

        radioMariageSecret.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMarriage = "ヒミツ";
                uncheckGroupMarriage(2);
            }
        });

        radioUnmarried.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMarriage = "未婚";
                uncheckGroupMarriage(1);
            }
        });

        radioMarried.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mMarriage = "既婚";
                uncheckGroupMarriage(1);
            }
        });

        radioNoneAvatar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mHasAvatar = "0";
            }
        });

        radioAvatar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mHasAvatar = "1";
            }
        });


        edtAgeFrom.setOnClickListener(v -> {
            showAge(edtAgeFrom);
        });

        edtAgeTo.setOnClickListener(v -> {
            showAge(edtAgeTo);
        });


    }

    private void uncheckGroupMarriage(int groupNumber) {
        if (groupNumber == 1) {
            radioMarriageUndefine.setChecked(false);
            radioMariageSecret.setChecked(false);
        } else {
            radioUnmarried.setChecked(false);
            radioMarried.setChecked(false);
        }
    }

    public void uncheckGroupPurpose(int groupNumber) {
        if (groupNumber == 1) {
            radioUndefine.setChecked(false);
            radioFindFriend.setChecked(false);
            radioFindLove.setChecked(false);
        } else {
            radioFindHobby.setChecked(false);
            radioFindKillingTime.setChecked(false);
            radioFindSecret.setChecked(false);
        }
    }

    public void showCityPicker(TextView tvDisplaySource) {
        String jsonCity = FileUtils.loadJSONFromAsset(getContext(),
                getContext().getString(R.string.list_city_jp));
        List<String> listNation =
                GsonUtils.String2ListObject(jsonCity, String[].class);
        String nationSelected = tvDisplaySource.getText().toString();
        CustomSpinnerAdapter spinnerAdapter =
                new CustomSpinnerAdapter(getContext(), R.layout.spinner_item, listNation,
                        tvDisplaySource);
        CustomListPopupWindow listPopupWindow =
                new CustomListPopupWindow(getContext(), tvDisplaySource, spinnerAdapter, nationSelected);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvDisplaySource.setText(
                        ((String) parent.getAdapter().getItem(position)));
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();
    }

    public void showAge(TextView tvDisplaySource) {
        List<String> listNation =
                getAge();
        String nationSelected = tvDisplaySource.getText().toString();
        CustomSpinnerAdapter spinnerAdapter =
                new CustomSpinnerAdapter(getContext(), R.layout.spinner_item, listNation,
                        tvDisplaySource);
        CustomListPopupWindow listPopupWindow =
                new CustomListPopupWindow(getContext(), tvDisplaySource, spinnerAdapter, nationSelected);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvDisplaySource.setText(
                        ((String) parent.getAdapter().getItem(position)));
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();
    }
}
