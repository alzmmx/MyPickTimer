package com.mx.picktimer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.picktimer.view.NumberPicker;

/**
 * Created by maxiao on 2017/7/10.
 */

public class PickValueView extends LinearLayout implements NumberPicker.OnValueChangeListener {
    private Context mContext;
    /**
     * 组件 标题、单位、滚轮
     */
    private TextView mTitleLeft, mTitleRight;
    private TextView mUnitLeft, mUnitRight;
    private MyNumberPicker mNpLeft, mNpRight;
    /**
     * 数据个数  1列 or 2列
     */
    private int mViewCount = 1;
    /**
     * 一组数据长度
     */
    private final int DATA_SIZE = 3;

    /**
     * 需要设置的值与默认值
     */
    private Object[] mLeftValues;
    private Object[] mRightValues;
    private Object mDefaultLeftValue;
    private Object mDefaultRightValue;
    /**
     * 当前正在显示的值
     */
    private Object[] mShowingLeft = new Object[DATA_SIZE];
    private Object[] mShowingRight = new Object[DATA_SIZE];

    /**
     * 步长
     */
    private int mLeftStep = 1;
    private int mRightStep = 1;
    /**
     * 回调接口对象
     */
    private onSelectedChangeListener mSelectedChangeListener;

    public PickValueView(Context context) {
        super(context);
        this.mContext = context;
        generateView();
    }

    public PickValueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        generateView();
    }

    public PickValueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        generateView();
    }

    /**
     * 生成视图
     */
    private void generateView() {
        //标题
        LinearLayout titleLayout = new LinearLayout(mContext);
        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(0, 0, 0, dip2px(12));
        titleLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.setOrientation(HORIZONTAL);
        mTitleLeft = new TextView(mContext);
        mTitleRight = new TextView(mContext);

        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        TextView[] titles = new TextView[]{mTitleLeft, mTitleRight};
        for (int i = 0; i < titles.length; i++) {
            titles[i].setLayoutParams(params);
            titles[i].setGravity(Gravity.CENTER);
            titles[i].setTextColor(Color.parseColor("#3434EE"));
        }
        titleLayout.addView(mTitleLeft);
        titleLayout.addView(mTitleRight);
        //内容
        LinearLayout contentLayout = new LinearLayout(mContext);
        contentLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.setOrientation(HORIZONTAL);
        contentLayout.setGravity(Gravity.CENTER);
        mNpLeft = new MyNumberPicker(mContext);
        mNpRight = new MyNumberPicker(mContext);
        mUnitLeft = new TextView(mContext);
        mUnitRight = new TextView(mContext);

        MyNumberPicker[] nps = new MyNumberPicker[]{mNpLeft, mNpRight};
        for (int i = 0; i < nps.length; i++) {
            nps[i].setLayoutParams(params);
            nps[i].setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            nps[i].setOnValueChangedListener(this);
        }

        contentLayout.addView(mNpLeft);
        contentLayout.addView(mUnitLeft);
        contentLayout.addView(mNpRight);
        contentLayout.addView(mUnitRight);

        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setOrientation(VERTICAL);
        this.addView(titleLayout);
        this.addView(contentLayout);
    }

    /**
     * 初始化数据和值
     */
    private void initViewAndPicker() {
        if (mViewCount == 1) {
            this.mNpRight.setVisibility(GONE);
            this.mUnitRight.setVisibility(GONE);
        }

        //初始化数组值
        if (mLeftValues != null && mLeftValues.length != 0) {
            if (mLeftValues.length < DATA_SIZE) {
                for (int i = 0; i < mLeftValues.length; i++) {
                    mShowingLeft[i] = mLeftValues[i];
                }
                for (int i = mLeftValues.length; i < DATA_SIZE; i++) {
                    mShowingLeft[i] = -9999;
                }
            } else {
                for (int i = 0; i < DATA_SIZE; i++) {
                    mShowingLeft[i] = mLeftValues[i];
                }
            }
            mNpLeft.setMinValue(0);
            mNpLeft.setMaxValue(DATA_SIZE - 1);
            if (mDefaultLeftValue != null)
                updateLeftView(mDefaultLeftValue);
            else
                updateLeftView(mShowingLeft[0]);
        }

        /**
         * 右侧控件
         */
        if (mViewCount == 2) {
            if (mRightValues != null && mRightValues.length != 0) {
                if (mRightValues.length < DATA_SIZE) {
                    for (int i = 0; i < mRightValues.length; i++) {
                        mShowingRight[i] = mRightValues[i];
                    }
                    for (int i = mRightValues.length; i < DATA_SIZE; i++) {
                        mShowingRight[i] = -9999;
                    }
                } else {
                    for (int i = 0; i < DATA_SIZE; i++) {
                        mShowingRight[i] = mRightValues[i];
                    }
                }
                mNpRight.setMinValue(0);
                mNpRight.setMaxValue(DATA_SIZE - 1);
                if (mDefaultRightValue != null)
                    updateRightView(mDefaultRightValue);
                else
                    updateRightView(mShowingRight[0]);
            }
        }


    }

    private void updateLeftView(Object value) {
        updateValue(value, 0,"时");
    }


    private void updateRightView(Object value) {
        updateValue(value, 1,"分");
    }

    /**
     * 更新滚轮视图
     *
     * @param value
     * @param index
     */
    private void updateValue(Object value, int index,String label) {
        String showStr[] = new String[DATA_SIZE];
        MyNumberPicker picker;
        Object[] showingValue;
        Object[] values;
        int step;
        if (index == 0) {
            picker = mNpLeft;
            showingValue = mShowingLeft;
            values = mLeftValues;
            step = mLeftStep;
        } else {
            picker = mNpRight;
            showingValue = mShowingRight;
            values = mRightValues;
            step = mRightStep;
        }

        if (values instanceof Integer[]) {
            for (int i = 0; i < DATA_SIZE; i++) {
                showingValue[i] = (int) value - step * (DATA_SIZE / 2 - i);
                int offset = (int) values[values.length - 1] - (int) values[0] + step;
                if ((int) showingValue[i] < (int) values[0]) {
                    showingValue[i] = (int) showingValue[i] + offset;
                }
                if ((int) showingValue[i] > (int) values[values.length - 1]) {
                    showingValue[i] = (int) showingValue[i] - offset;
                }
                showStr[i] = "" + showingValue[i];
            }
        } else {
            int strIndex = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(value)) {
                    strIndex = i;
                    break;
                }
            }
            for (int i = 0; i < DATA_SIZE; i++) {
                int temp = strIndex - (DATA_SIZE / 2 - i);
                if (temp < 0) {
                    temp += values.length;
                }
                if (temp >= values.length) {
                    temp -= values.length;
                }
                showingValue[i] = values[temp];
                showStr[i] = (String) values[temp];
            }
        }
        picker.setLabel(label);
        picker.setDisplayedValues(showStr);
        picker.setValue(DATA_SIZE / 2);
        picker.postInvalidate();
    }


    /**
     * 设置数据--单列数据
     *
     * @param leftValues
     * @param mDefaultLeftValue
     */
    public void setValueData(Object[] leftValues, Object mDefaultLeftValue) {
        this.mViewCount = 1;
        this.mLeftValues = leftValues;
        this.mDefaultLeftValue = mDefaultLeftValue;

        initViewAndPicker();
    }

    /**
     * 设置数据--两列数据
     *
     * @param leftValues
     * @param mDefaultLeftValue
     * @param rightValues
     * @param defaultRightValue
     */
    public void setValueData(Object[] leftValues, Object mDefaultLeftValue, Object[] rightValues, Object defaultRightValue) {
        this.mViewCount = 2;
        this.mLeftValues = leftValues;
        this.mDefaultLeftValue = mDefaultLeftValue;

        this.mRightValues = rightValues;
        this.mDefaultRightValue = defaultRightValue;

        initViewAndPicker();
    }

    /**
     * 设置左边数据步长
     *
     * @param step
     */
    public void setLeftStep(int step) {
        this.mLeftStep = step;
        initViewAndPicker();
    }


    /**
     * 设置右边数据步长
     *
     * @param step
     */
    public void setRightStep(int step) {
        this.mRightStep = step;
        initViewAndPicker();
    }

    /**
     * 设置标题
     *
     * @param left
     * @param middle
     * @param right
     */
    public void setTitle(String left, String middle, String right) {
        if (left != null) {
            mTitleLeft.setVisibility(VISIBLE);
            mTitleLeft.setText(left);
        } else {
            mTitleLeft.setVisibility(GONE);
        }
        if (right != null) {
            mTitleRight.setVisibility(VISIBLE);
            mTitleRight.setText(right);
        } else {
            mTitleRight.setVisibility(GONE);
        }
        this.postInvalidate();
    }

    public void setUnitLeft(String unitLeft) {
        setUnit(unitLeft, 0);
    }

    public void setmUnitMiddle(String unitMiddle) {
        setUnit(unitMiddle, 1);
    }

    public void setUnitRight(String unitRight) {
        setUnit(unitRight, 2);
    }

    private void setUnit(String unit, int index) {
        TextView tvUnit;
        if (index == 0) {
            tvUnit = mUnitLeft;
        } else {
            tvUnit = mUnitRight;
        }
        if (unit != null) {
            tvUnit.setText(unit);
        } else {
            tvUnit.setText(" ");
        }
        initViewAndPicker();
    }

    /**
     * 设置回调
     *
     * @param listener
     */
    public void setOnSelectedChangeListener(onSelectedChangeListener listener) {
        this.mSelectedChangeListener = listener;
    }

    /**
     * dp转px
     *
     * @param dp
     * @return
     */
    private int dip2px(int dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (scale * dp + 0.5f);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal, EditText editText) {
        if (picker == mNpLeft) {
            updateLeftView(mShowingLeft[newVal]);
        } else if (picker == mNpRight) {
            updateRightView(mShowingRight[newVal]);
        }
        if (mSelectedChangeListener != null) {
            mSelectedChangeListener.onSelected(this, mShowingLeft[DATA_SIZE / 2], mShowingRight[DATA_SIZE / 2]);
        }
    }


    /**
     * 回调接口
     */
    public interface onSelectedChangeListener {
        void onSelected(PickValueView view, Object leftValue, Object rightValue);
    }
}

