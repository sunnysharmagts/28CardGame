/*package com.mysterio.cardgame;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mysterio.mycardgame.R;

public class ScoreAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	//private List<ArrayList<Integer>> mScoreIconList;
	
	public ScoreAdapter(final Context context, final List<ArrayList<Integer>> scoreIconList){
		mLayoutInflater = LayoutInflater.from(context);
		//mScoreIconList = scoreIconList;
	}
	
	@Override
	public int getCount() {
		return mScoreIconList.size();
	}

	@Override
	public Integer getItem(int position) {
		return mScoreIconList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.score_grid, parent, false);
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.imageView);
			viewHolder.mImageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mImageView.setImageResource(mScoreIconList.get(position));
		viewHolder.mImageView1.setImageResource(mScoreIconList.get(position));
		viewHolder.mImageView2.setImageResource(mScoreIconList.get(position));
		final int length = list.size();
		return convertView;
	}
	
	private class ViewHolder{
		private ImageView mImageView;
		private ImageView mImageView1;
	}
	
}
*/