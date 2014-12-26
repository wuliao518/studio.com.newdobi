package com.dobi.adapter;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.dobi.R;
import com.dobi.common.ConstValue;
import com.dobi.ui.OneImage;
import com.readystatesoftware.viewbadger.BadgeView;

public class PopAdapter extends BaseAdapter {
	private ArrayList<Bitmap> mBitmapList;
	private LayoutInflater mInflater;
	private Context context;
	private OneImage image;
	//private int index = 1;
	private int prePosition = -1;
	private BadgeView bv;
	public boolean isAdd = true;

	public PopAdapter(Context context, ArrayList<Bitmap> mBitmapList,
			OneImage image) {
		this.mBitmapList = mBitmapList;
		this.context = context;
		this.image = image;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mBitmapList.size();
	}
	public void setBadgeViewNull(){
		prePosition=-1;
		if(bv!=null){
			bv.hide();
		}
	}
	@Override
	public Object getItem(int position) {
		return mBitmapList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_person, null);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.item_image);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					100);
			viewHolder.image.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Bitmap bitmap = mBitmapList.get(position);
		viewHolder.image.setImageBitmap(bitmap);
		viewHolder.image.setOnClickListener(new PopOnclickListener(position));
		viewHolder.image.setOnLongClickListener(new PopOnLongClickListener(
				position));
		return convertView;
	}

	private static class ViewHolder {
		ImageView image;
	}

	private class PopOnLongClickListener implements OnLongClickListener {
		private int position;

		public PopOnLongClickListener(int position) {
			this.position = position;
		}

		@Override
		public boolean onLongClick(View v) {
			if (position == mBitmapList.size() - 1 && isAdd) {
				return false;
			}
			if (bv == null) {
				bv = new BadgeView(context, v);
				bv.setBackgroundResource(R.drawable.delete_person);
				bv.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				bv.show();
				prePosition = position;
			} else {
				if(bv!=null){
					bv.hide();
				}
				if (prePosition == position) {
					prePosition = position;
				} else {
					bv = new BadgeView(context, v);
					bv.setBackgroundResource(R.drawable.delete_person);
					bv.show();
					prePosition = position;
				}

			}
			return false;
		}

	}

	private class PopOnclickListener implements OnClickListener {
		int position;
		String rootPath = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH;

		public PopOnclickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (position == (mBitmapList.size() - 1)) {// 点击最后一个
				if (isAdd) {
					if (mBitmapList.size() == 1) {
						
						image.initView((Activity) context, 0, 0);
						mBitmapList.remove(mBitmapList.size() - 1);
						mBitmapList.add(BitmapFactory.decodeFile(rootPath
								+ "person" + 0 + "png"));
						mBitmapList.add(BitmapFactory.decodeResource(
								context.getResources(), R.drawable.add));
						isAdd = true;
						PopAdapter.this.notifyDataSetChanged();
					} else {
						
						image.initView((Activity) context, 0, 0);
						mBitmapList.remove(mBitmapList.size() - 1);
						mBitmapList.add(BitmapFactory.decodeFile(rootPath
								+ "person" + 1 + "png"));
						isAdd = false;
						PopAdapter.this.notifyDataSetChanged();
					}

				} else {
					if (prePosition == position) {
						
					} else {
						
					}
				}
				image.invalidate();
			} else {// 非点击最后一个
				if (prePosition == position) {
					
				} else {
					
				}
				image.invalidate();
			}

		}
	}

}
