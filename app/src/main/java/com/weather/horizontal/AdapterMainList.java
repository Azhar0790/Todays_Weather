package com.weather.horizontal;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Sarps.Utility.ImageLoader;
import com.Sarps.Weather.R;
import com.squareup.picasso.Picasso;


public class AdapterMainList extends BaseAdapter
{
	private Context context;
	private LayoutInflater inflater;
	//Basic Variables
	ArrayList<HashMap<String, String>> arrList;
	public AdapterMainList(Context context,ArrayList<HashMap<String, String>> arrList)
	{
		super();
		this.context = context;
		this.arrList = arrList;
		inflater = LayoutInflater.from(this.context);
	}
	@Override
	public int getCount()
	{
		return arrList.size();
	}
	@Override
	public Object getItem(int index)
	{
		return arrList.get(index);
	}
	@Override
	public long getItemId(int index)
	{
		return index;
	}
	@Override
	public View getView(int index, View convertView, ViewGroup viewGroup)
	{
		ViewHolder holder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.horlist_item, null);
			holder = new ViewHolder();
			holder.day = (TextView) convertView.findViewById(R.id.tv_day);
			holder.tv_farade = (TextView) convertView.findViewById(R.id.tv_farade);
			holder.tv_celcius = (TextView) convertView.findViewById(R.id.tv_celcius);
			holder.iv_image=(ImageView) convertView.findViewById(R.id.iv_image);
			holder.load_image=new ImageLoader(context);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		Log.e("arrList", "" + arrList);

		holder.day.setText(arrList.get(index).get("weekday"));
		holder.tv_farade.setText("F - "+arrList.get(index).get("fahrenheit")+"°C");
		holder.tv_celcius.setText("C - "+arrList.get(index).get("celsius")+"°C");

//		holder.load_image.DisplayImage(arrList.get(index).get("icon_url"), holder.iv_image);

//		if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/nt_partlycloudy.gif")) {
//			holder.iv_image.setImageResource(R.drawable.partly_cloud);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/clear.gif")) {
//			holder.iv_image.setImageResource(R.drawable.sun);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/partlycloudy.gif")) {
//			holder.iv_image.setImageResource(R.drawable.partly_cloud);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/nt_clear.gif")) {
//			holder.iv_image.setImageResource(R.drawable.sun);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/nt_mostlycloudy.gif")) {
//			holder.iv_image.setImageResource(R.drawable.partly_cloud);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/nt_snow.gif")) {
//			holder.iv_image.setImageResource(R.drawable.partly_cloud);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/chancetstorms.gif")) {
//			holder.iv_image.setImageResource(R.drawable.storm);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/nt_chancetstorms.gif")) {
//			holder.iv_image.setImageResource(R.drawable.storm);
//		} else if (arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/snow.gif")) {
//			holder.iv_image.setImageResource(R.drawable.snow);
//		}else if(arrList.get(index).get("icon_url").equals("http://icons.wxug.com/i/c/k/chancerain.gif")){
//			holder.iv_image.setImageResource(R.drawable.rainy_cloud);
//		}
		Picasso.with(context).load(arrList.get(index).get("icon_url")).into(holder.iv_image);
		return convertView;
	}
	class ViewHolder
	{
		TextView tv,day,tv_farade,tv_celcius;
		ImageView iv_image;
		ImageLoader load_image;
	}
}
