package com.jgw.delingha.module.options_details.adapter;

import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ImageBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.databinding.ItemInfoDetailsType1Binding;
import com.jgw.delingha.databinding.ItemInfoDetailsType2Binding;
import com.jgw.delingha.databinding.ItemInfoDetailsType3Binding;
import com.jgw.delingha.databinding.ItemInfoDetailsType4Binding;
import com.jgw.delingha.databinding.ItemInfoDetailsType5Binding;
import com.jgw.delingha.databinding.ItemInfoDetailsType6Binding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XiongShaoWu
 * on 2019/9/12
 */
public class InfoDetailsRecyclerAdapter extends CustomRecyclerAdapter<InfoDetailsDemoBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_type1, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT2) {
            return new ContentType2ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_type2, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT3) {
            return new ContentType3ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_type3, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT4) {
            return new ContentType4ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_type4, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT5) {
            return new ContentType5ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_type5, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT6) {
            return new ContentType6ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_type6, parent, false));
        } else {
            return new EmptyViewHolder(DataBindingUtil.inflate(mLayoutInflater, R.layout.item_empty, parent, false));
        }
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(infoDetailsDemoBean);
            ((ContentType1ViewHolder) holder).mBindingView.rlInfoDetailsItemType1.setVisibility(!infoDetailsDemoBean.isShowUnit ? View.GONE : View.VISIBLE);
        } else if (holder instanceof ContentType2ViewHolder) {
            InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
            ((ContentType2ViewHolder) holder).mBindingView.setData(infoDetailsDemoBean);
        } else if (holder instanceof ContentType3ViewHolder) {
            InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
            ((ContentType3ViewHolder) holder).mBindingView.setData(infoDetailsDemoBean);
        } else if (holder instanceof ContentType4ViewHolder) {
            InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
            ((ContentType4ViewHolder) holder).mBindingView.setData(infoDetailsDemoBean);
        } else if (holder instanceof ContentType5ViewHolder) {
            InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
            ((ContentType5ViewHolder) holder).mBindingView.setData(infoDetailsDemoBean);

            ArrayList<ImageBean> strings = new ArrayList<>();
            InfoDetailsDemoBean.ValueBean value = infoDetailsDemoBean.value;
            if (value != null) {
                String valueStr = value.valueStr;
                if (!TextUtils.isEmpty(valueStr)) {
                    String[] split = valueStr.split(",");
                    for (String s : split) {
                        if (infoDetailsDemoBean.enable) {
                            Uri parse = Uri.parse(s);
                            strings.add(new ImageBean(parse));
                        } else {
                            strings.add(new ImageBean(s));
                        }
                    }
                }
            }
            ((ContentType5ViewHolder) holder).mImgsAdapter.setType(infoDetailsDemoBean.enable ? 0 : 1);
            ((ContentType5ViewHolder) holder).mImgsAdapter.setDataList(strings);
            ((ContentType5ViewHolder) holder).mImgsAdapter.setLimit(infoDetailsDemoBean.imageLimit);
            ((ContentType5ViewHolder) holder).mImgsAdapter.notifyDataSetChanged();
        } else if (holder instanceof ContentType6ViewHolder) {
            InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
            ((ContentType6ViewHolder) holder).mBindingView.setData(infoDetailsDemoBean);
        }
    }

    @Override
    public int getItemViewType(int position) {
        InfoDetailsDemoBean infoDetailsDemoBean = mList.get(position);
        int type;
        switch (infoDetailsDemoBean.itemType) {
            case 0:
                type = ITEM_TYPE_CONTENT1;
                break;
            case 1:
                type = ITEM_TYPE_CONTENT2;
                break;
            case 2:
                type = ITEM_TYPE_CONTENT3;
                break;
            case 3:
                type = ITEM_TYPE_CONTENT4;
                break;
            case 4:
                type = ITEM_TYPE_CONTENT5;
                break;
            case 5:
                type = ITEM_TYPE_CONTENT6;
                break;
            case 6:
                type = ITEM_TYPE_CONTENT7;
                break;
            case 7:
                type = ITEM_TYPE_CONTENT8;
                break;
            case 8:
                type = ITEM_TYPE_CONTENT9;
                break;
            default:
                type = ITEM_TYPE_CONTENT1;
        }
        return type;
    }

    public String searchValueStrByKey(String key) {
        String valueStr = "";
        for (InfoDetailsDemoBean bean : mList) {
            if (bean.key.equals(key)) {
                if (bean.value != null) {
                    valueStr = bean.value.valueStr;
                    break;
                }
            }
        }
        return valueStr;
    }

    public String searchValueIdByKey(String key) {
        String valueId = "";
        for (InfoDetailsDemoBean bean : mList) {
            if (bean.key.contains(key)) {
                if (bean.value != null) {
                    valueId = bean.value.valueId;
                    break;
                }
            }
        }
        return valueId;
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemInfoDetailsType1Binding> {
        private ContentType1ViewHolder(ItemInfoDetailsType1Binding binding) {
            super(binding);
        }
    }

    private class ContentType2ViewHolder extends ContentViewHolder<ItemInfoDetailsType2Binding> {
        private ContentType2ViewHolder(ItemInfoDetailsType2Binding binding) {
            super(binding);
            EditText editText = binding.etInfoDetailsItemType2;
            binding.etInfoDetailsItemType2.addTextChangedListener(getTextWatcher(editText));
        }

        final String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final Pattern pattern = Pattern.compile("[^" + allowedChars + "]");

        @NotNull
        private TextWatcher getTextWatcher(EditText editText) {
            return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    InfoDetailsDemoBean bean = mList.get(getAdapterPosition());
                    /**
                     * 正则表达式模式来匹配不允许的字符
                     */
                    if (bean.inputType == 3) {
                        String input = s.toString();
                        Matcher matcher = pattern.matcher(input);
                        if (matcher.find()) {
                            String filteredInput = matcher.replaceAll("");
                            if (!filteredInput.equals(input)) {
                                editText.setText(filteredInput);
                                editText.setSelection(filteredInput.length());
                            }
                        }
                    }
//                        LogUtils.xswShowLog(textKey);
                    String str = s.toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        return;
                    }
                    if (bean.isDouble) {
                        formatText(str, bean, editText);
                    }
                }
            };
        }
    }


    private class ContentType3ViewHolder extends ContentViewHolder<ItemInfoDetailsType3Binding> {
        private ContentType3ViewHolder(ItemInfoDetailsType3Binding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addView(mBindingView.tvInfoDetailsItemType3Value)
                    .addItemClickListener()
                    .submit();
        }

        @Override
        public void onItemClick(View view, int position) {
            super.onItemClick(view, position);
            if (mListener != null) {
                mListener.onItemClick(view, position);
            }
        }
    }

    private class ContentType4ViewHolder extends ContentViewHolder<ItemInfoDetailsType4Binding> {
        private ContentType4ViewHolder(ItemInfoDetailsType4Binding binding) {
            super(binding);
            EditText editText = binding.etInfoDetailsItemType4;
            editText.addTextChangedListener(getTextWatcher(editText));
        }

        @NotNull
        private TextWatcher getTextWatcher(EditText editText) {
            return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
//                        LogUtils.xswShowLog(textKey);
                    String str = s.toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        return;
                    }
                    InfoDetailsDemoBean bean = mList.get(getAdapterPosition());
                    if (bean.isDouble) {
                        formatText(str, bean, editText);
                    }
                }
            };
        }
    }

    private class ContentType5ViewHolder extends ContentViewHolder<ItemInfoDetailsType5Binding> {
        private InfoDetailsImgsRecyclerAdapter mImgsAdapter;

        private ContentType5ViewHolder(ItemInfoDetailsType5Binding binding) {
            super(binding);
            mImgsAdapter = new InfoDetailsImgsRecyclerAdapter();
            mImgsAdapter.setOnItemClickListener(this);
            binding.rvInfoDetailsType5.setGridVerticalLayoutManager(3);
            binding.rvInfoDetailsType5.setAdapter(mImgsAdapter);
        }

        @Override
        public void onItemClick(View view, int position) {
            super.onItemClick(view, position);
            if (mListener != null) {
                mListener.onItemClick(view, getAdapterPosition(), position);
            }
        }
    }

    private class ContentType6ViewHolder extends ContentViewHolder<ItemInfoDetailsType6Binding> {
        private ContentType6ViewHolder(ItemInfoDetailsType6Binding binding) {
            super(binding);
            EditText editText = binding.etInfoDetailsItemType6;
            editText.addTextChangedListener(getTextWatcher(editText));
        }

        @NotNull
        private TextWatcher getTextWatcher(EditText editText) {
            return new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
//                        LogUtils.xswShowLog(textKey);
                    String str = s.toString().trim();
                    if (TextUtils.isEmpty(str)) {
                        return;
                    }
                    InfoDetailsDemoBean bean = mList.get(getAdapterPosition());
                    if (bean.isDouble) {
                        formatText(str, bean, editText);
                    }
                }
            };
        }
    }

    private void formatText(String str, InfoDetailsDemoBean bean, EditText editText) {
        int integerLength = bean.integerLength;
        int doubleLength = bean.doubleLength;
        int pointIndex = str.indexOf(".");
        int zeroIndex = str.indexOf("0");
        if (pointIndex == -1) {
            //整数
            //多位数,删除首位0
            if (zeroIndex == 0 && str.length() > 1) {
                String substring = str.substring(1);
                editText.setText(substring);
                editText.setSelection(substring.length());
                return;
            }
            //限制整数位长度
            if (str.length() > integerLength) {
                String substring = str.substring(0, integerLength);
                editText.setText(substring);
                editText.setSelection(substring.length());
            }

        } else {
            //小数(录入了".")
            //多位数,首位0后不是"."则删除0
            if (zeroIndex == 0 && str.length() > 1 && !String.valueOf(str.charAt(1)).equals(".")) {
                String substring = str.substring(1);
                editText.setText(substring);
                editText.setSelection(substring.length());
                return;
            }
            if (pointIndex == 0) {
                //首位是"."则在前边拼0
                String text = "0" + str;
                editText.setText(text);
                editText.setSelection(text.length());
            } else {
                if (pointIndex != str.length() - 1) {
                    boolean isChange = false;
                    String[] split = str.split("\\.");
                    String integerStr = split[0];
                    String doubleStr = "";
                    StringBuilder sb = new StringBuilder();
                    //整数位长度限制
                    if (integerStr.length() > integerLength) {
                        integerStr = integerStr.substring(0, integerLength);
                        isChange = true;
                    }
                    if (split.length > 1) {
                        doubleStr = split[1];

                        //小数位长度限制
                        if (doubleStr.length() > doubleLength) {
                            doubleStr = doubleStr.substring(0, doubleLength);
                            isChange = true;
                        }

                    }
                    if (isChange) {
                        sb.append(integerStr);
                        sb.append(".");
                        sb.append(doubleStr);
                        String text = sb.toString();
                        editText.setText(text);
                        editText.setSelection(text.length());
                    }

                } else {
//                                "."为末尾，无需处理
                }

            }
        }
    }

    /**
     * 增加条目方法
     *
     * @param position 增加的位置
     * @param bean     条目的数据对象
     */
    public void add(int position, @NonNull InfoDetailsDemoBean bean) {
        mList.add(position, bean);
        notifyDataSetChanged();
    }

    /**
     * 删除条目的方法
     *
     * @param position 删除条目的下标
     */
    public void remove(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 删除条目的方法
     *
     * @param key 删除条目的key
     * @return 是否删除成功
     */
    public boolean remove(@NonNull String key) {
        int position = -1;
        for (int i = 0; i < mList.size(); i++) {
            InfoDetailsDemoBean bean = mList.get(i);
            if (TextUtils.equals(bean.key, key)) {
                position = i;
            }
        }
        if (position != -1) {
            mList.remove(position);
            notifyDataSetChanged();
        }
        return position != -1;
    }

    /**
     * 更新条目值的方法
     *
     * @param key  条目的key
     * @param bean 条目值的对象 用来更新数据
     * @return 是否更新成功
     */
    public boolean update(@NonNull String key,
                          @NonNull InfoDetailsDemoBean.ValueBean bean) {
        boolean b = false;
        for (int i = 0; i < mList.size(); i++) {
            InfoDetailsDemoBean current = mList.get(i);
            if (TextUtils.equals(key, current.key)) {
                current.value = bean;
                b = true;
                notifyItemChanged(i);
                break;
            }
        }
        return b;
    }

    /**
     * 更新配方item单位的方法 仅针对配方
     *
     * @param key  条目的key
     * @param unit 条目值的对象 用来更新数据
     * @return 是否更新成功
     */
    public boolean updateUnit(@NonNull String key,
                              @NonNull String unit) {
        boolean b = false;
        for (int i = 0; i < mList.size(); i++) {
            InfoDetailsDemoBean current = mList.get(i);
            if (TextUtils.equals(key, current.key)) {
                current.specificationUnitValue = unit;
                current.unit = unit;
                b = true;
                notifyItemChanged(i);
                break;
            }
        }
        return b;
    }

    /**
     * 获取条目数据
     *
     * @param key 条目key
     * @return 值的对象
     */
    public InfoDetailsDemoBean.ValueBean query(@NonNull String key) {
        InfoDetailsDemoBean.ValueBean value = null;
        for (int i = 0; i < mList.size(); i++) {
            InfoDetailsDemoBean bean = mList.get(i);
            if (TextUtils.equals(key, bean.key)) {
                value = bean.value;
                break;
            }
        }
        return value;
    }

    /**
     * 获取条目数据
     *
     * @param key 条目key
     * @return 值的对象
     */
    public InfoDetailsDemoBean queryItem(@NonNull String key) {
        InfoDetailsDemoBean item = null;
        for (int i = 0; i < mList.size(); i++) {
            InfoDetailsDemoBean bean = mList.get(i);
            if (TextUtils.equals(key, bean.key)) {
                item = bean;
                break;
            }
        }
        return item;
    }

    /**
     * 获取条目数据
     *
     * @param position 条目下标
     * @return 值的对象
     */
    public InfoDetailsDemoBean.ValueBean query(int position) {
        InfoDetailsDemoBean.ValueBean value = null;
        if (mList.size() > position) {
            InfoDetailsDemoBean bean = mList.get(position);
            value = bean.value;
        }
        return value;
    }


}
