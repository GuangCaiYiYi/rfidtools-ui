package com.rfidresearchgroup.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.rfidresearchgroup.javabean.DevBean;
import com.rfidresearchgroup.common.util.ViewUtil;
import com.rfidresearchgroup.rfidtools.R;

public class DevArrayAdapter extends AbsResIdArrayAdapter<DevBean> {

    public DevArrayAdapter(Context context, int resource, List<DevBean> objects) {
        super(context, resource, objects);

        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ViewUtil.inflate(getContext(), resourceId);
        }
        //取出数据!
        DevBean bean = getItem(position);
        if (bean != null) {
            //得到视图实例!
            TextView name = convertView.findViewById(R.id.txtDevName);
            TextView address = convertView.findViewById(R.id.txtDevAddress);

            String rawName = bean.getDevName() == null ? "" : bean.getDevName().trim();
            String rawAddr = bean.getMacAddress() == null ? "" : bean.getMacAddress().trim();

            String title = rawName;
            String subtitle = rawAddr;

            // "OTGToUartSerial(OTG转串口)" -> 标题用括号里的中文名，副标题用原始英文名
            int lp = rawName.indexOf('(');
            int rp = rawName.lastIndexOf(')');
            if (lp > 0 && rp > lp) {
                String inside = rawName.substring(lp + 1, rp).trim();
                String outside = rawName.substring(0, lp).trim();
                if (!inside.isEmpty()) {
                    title = inside;
                    subtitle = outside;
                }
            }

            name.setText(title);

            // USB / OTG 设备的地址是全 0 占位，没有意义，直接隐藏这一行
            if (subtitle.isEmpty() || isPlaceholderAddress(subtitle)) {
                address.setVisibility(View.GONE);
            } else {
                address.setText(subtitle);
                address.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    /**
     * 00:00:00:00:00:00 / 000000000000 这类占位地址
     */
    private boolean isPlaceholderAddress(String addr) {
        String digits = addr.replace(":", "").replace("-", "").trim();
        if (digits.isEmpty()) {
            return true;
        }
        for (int i = 0; i < digits.length(); i++) {
            char c = digits.charAt(i);
            if (c != '0') {
                return false;
            }
        }
        return true;
    }
}
