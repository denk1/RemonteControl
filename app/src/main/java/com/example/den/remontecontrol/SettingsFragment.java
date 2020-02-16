package com.example.den.remontecontrol;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private EditTextPreference editTextPreferenceIp = null;
    private EditTextPreference editTextPreferencePort = null;
    private EditTextPreference editTextPreferenceIpVideo = null;
    private EditTextPreference getEditTextPreferencePortVideo = null;
    private DropDownPreference dropDownPreferenceTypeConn = null;
    private EditTextPreference editTextPreferenceIpControl = null;
    private EditTextPreference editTextPreferencePortControl = null;
    private DropDownPreference dropDownPreferenceSign1 = null;
    private DropDownPreference dropDownPreferenceSign2 = null;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String oldText;
    SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }
    */
    @Override
    public void onCreatePreferences(Bundle
                                            savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        createListener();
        editTextPreferenceIp = (EditTextPreference)findPreference(SettinsActivity.IP_ADDRESS_TEXT);
        editTextPreferencePort = (EditTextPreference)findPreference(SettinsActivity.PORT_TEXT);
        editTextPreferenceIpVideo = (EditTextPreference)findPreference(SettinsActivity.IP_ADDRESS_VIDEO_TEXT);
        getEditTextPreferencePortVideo = (EditTextPreference)findPreference(SettinsActivity.PORT_VIDEO_TEXT);
        dropDownPreferenceTypeConn = (DropDownPreference)findPreference(SettinsActivity.TYPE_OF_CONNECTION);
        editTextPreferenceIpControl = (EditTextPreference)findPreference(SettinsActivity.IP_ADDRESS_CONTROL);
        editTextPreferencePortControl = (EditTextPreference)findPreference(SettinsActivity.PORT_CONTROL);
        dropDownPreferenceSign1 = (DropDownPreference)findPreference(SettinsActivity.SIGN_1);
        dropDownPreferenceSign2 = (DropDownPreference)findPreference(SettinsActivity.SIGN_2);

        oldText = editTextPreferenceIp.getText();
        sharedPreferences = getPreferenceManager().getSharedPreferences();
        String valueIp = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_TEXT, "NULL");
        String valuePort = sharedPreferences.getString(SettinsActivity.PORT_TEXT, "NULL");
        String valueIpVideo = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_VIDEO_TEXT, "NULL");
        String valuePortVideo = sharedPreferences.getString(SettinsActivity.PORT_VIDEO_TEXT, "NULL");
        String valueTypeOfConn = sharedPreferences.getString(SettinsActivity.TYPE_OF_CONNECTION, "NULL");
        String valueIpControl = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_CONTROL, "NULL");
        String valuePortControl = sharedPreferences.getString(SettinsActivity.PORT_CONTROL, "NULL");
        String valueSign1 = sharedPreferences.getString(SettinsActivity.SIGN_1, "NULL");
        String valueSign2 = sharedPreferences.getString(SettinsActivity.SIGN_2, "NULL");
        editTextPreferenceIp.setSummary(valueIp);
        editTextPreferencePort.setSummary(valuePort);
        editTextPreferenceIpVideo.setSummary(valueIpVideo);
        getEditTextPreferencePortVideo.setSummary(valuePortVideo);
        dropDownPreferenceTypeConn.setSummary(valueTypeOfConn);
        editTextPreferenceIpControl.setSummary(valueIpControl);
        editTextPreferencePortControl.setSummary(valuePortControl);
        dropDownPreferenceSign1.setSummary(valueSign1);
        dropDownPreferenceSign2.setSummary(valueSign2);
    }

    private void createListener() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String value = sharedPreferences.getString(key, "NULL");
                if(key.equals(SettinsActivity.IP_ADDRESS_TEXT)) {
                    oldText = value;
                    editTextPreferenceIp.setSummary(value);
                }
                else if(key.equals(SettinsActivity.PORT_TEXT)){
                    editTextPreferencePort.setSummary(value);
                }
                else if(key.equals(SettinsActivity.IP_ADDRESS_VIDEO_TEXT)) {
                    editTextPreferenceIpVideo.setSummary(value);
                }
                else if(key.equals(SettinsActivity.PORT_VIDEO_TEXT)) {
                    getEditTextPreferencePortVideo.setSummary(value);
                }
                else if(key.equals(SettinsActivity.TYPE_OF_CONNECTION)) {
                    dropDownPreferenceTypeConn.setSummary(value);
                }
                else if(key.equals(SettinsActivity.IP_ADDRESS_CONTROL)) {
                    editTextPreferenceIpControl.setSummary(value);
                }
                else if(key.equals(SettinsActivity.PORT_CONTROL)) {
                    editTextPreferencePortControl.setSummary(value);
                }
                else if(key.equals(SettinsActivity.SIGN_1)) {
                    dropDownPreferenceSign1.setSummary(value);
                }
                else if(key.equals(SettinsActivity.SIGN_2)) {
                    dropDownPreferenceSign2.setSummary(value);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(listener);
    }

    public static class CircleView extends View {
        int backgroundColor;
        int innerCircleColor;
        int outerCircleColor;
        final int RADIUS_INNER = 100;
        final int RADIUS_OUTTER = 300;
        int [] convert = new int[256];
        PointF mPoint = null;

        void initConvert() {
            for(int i = 0; i < 128; i++) {

                convert[i] = -i;
                convert[i + 127 + 1] = 128 - i;

            }
        }

        public CircleView(Context context, AttributeSet attr) {
            super(context, attr);
            mPoint = new PointF(0,0);
            initConvert();
            TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.CircleView, 0 , 0);
            try {
                backgroundColor = a.getColor(R.styleable.CircleView_backgroundColor, Color.WHITE);
                innerCircleColor = a.getColor(R.styleable.CircleView_innerCircleColor, Color.parseColor("#CD5C5C"));
                outerCircleColor = a.getColor(R.styleable.CircleView_outerCircleColor, Color.parseColor("#5CCD5C"));

            } finally {
                a.recycle();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int x = getWidth();
            int y = getHeight();
            int radiusInner = 100;
            int radiusOuter = 300;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(backgroundColor);
            canvas.drawPaint(paint);
            paint.setColor(outerCircleColor);
            canvas.drawCircle(x/2, y/2, radiusOuter, paint);
            paint.setColor(innerCircleColor);
            canvas.drawCircle((float) x /2 + convert[(int)mPoint.x + 128], (float) y/2 + convert[(int)mPoint.y + 128], radiusInner, paint);
        }

        public void setCurrPointPos(PointF mPoint) {
            this.mPoint = mPoint;
        }

        public void setCurrX(float v) {
            mPoint.x = v;
        }

        public void setCurrY(float v) {
            mPoint.y = v;
        }

        public int getRadiusInner() {
            return RADIUS_INNER;
        }

        public int getRadiusOuter() {
            return RADIUS_OUTTER;
        }

        public int[] getConvert() {
            return convert;
        }
    }
}
