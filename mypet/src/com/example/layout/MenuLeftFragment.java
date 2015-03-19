package com.example.layout;

import com.example.mypet.GirlActivity;
import com.example.mypet.MainActivity;
import com.example.mypet.R;
import com.example.mypet.WifiActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MenuLeftFragment extends Fragment
{
	
	private RelativeLayout mainView;
	
	private RelativeLayout first_robot;
	private RelativeLayout second_girl;
	private RelativeLayout third_wifi;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mainView =  (RelativeLayout) inflater.inflate(R.layout.layout_menu, container, false);
		
		initView();
		addListerner();
		
		return mainView;
	}
	
	private void addListerner() {
		first_robot.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				second_girl.setBackgroundColor(0);
				second_girl.getBackground().setAlpha(0);
				third_wifi.setBackgroundColor(0);
				third_wifi.getBackground().setAlpha(0);
				first_robot.setBackgroundColor(Color.BLUE);
				first_robot.getBackground().setAlpha(255);
				first_robot.setClickable(false);
				second_girl.setClickable(true);
				third_wifi.setClickable(true);
				
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
			}
		});
		
		second_girl.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				first_robot.setBackgroundColor(0);
				first_robot.getBackground().setAlpha(0);
				third_wifi.setBackgroundColor(0);
				third_wifi.getBackground().setAlpha(0);
				second_girl.setBackgroundColor(Color.BLUE);
				second_girl.getBackground().setAlpha(255);
				first_robot.setClickable(true);
				second_girl.setClickable(false);
				third_wifi.setClickable(true);
				Intent intent = new Intent(getActivity(), GirlActivity.class);
				startActivity(intent);
			}
		});
		third_wifi.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				second_girl.setBackgroundColor(0);
				second_girl.getBackground().setAlpha(0);
				first_robot.setBackgroundColor(0);
				first_robot.getBackground().setAlpha(0);
				third_wifi.setBackgroundColor(Color.BLUE);
				third_wifi.getBackground().setAlpha(255);
				first_robot.setClickable(true);
				second_girl.setClickable(true);
				third_wifi.setClickable(false);
				Intent intent = new Intent(getActivity(), WifiActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initView(){
		first_robot = (RelativeLayout) mainView.findViewById(R.id.first_robot);
		second_girl = (RelativeLayout) mainView.findViewById(R.id.second_girl);
		third_wifi = (RelativeLayout) mainView.findViewById(R.id.third_wifi);
	}
}
