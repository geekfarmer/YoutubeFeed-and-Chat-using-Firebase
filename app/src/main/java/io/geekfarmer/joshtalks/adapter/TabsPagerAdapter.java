package io.geekfarmer.joshtalks.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import io.geekfarmer.joshtalks.chat;
import io.geekfarmer.joshtalks.Youtubefeed;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {

	private static int TAB_COUNT = 2;
	private Context mcontext;

	public TabsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mcontext = context;
	}



	@Override
	public Fragment getItem(int position) {

		switch (position) {
			case 0:
				return new chat();
			case 1:
				return new Youtubefeed();
		}
		return null;
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return chat.TITLE;

			case 1:
				return Youtubefeed.TITLE;
		}
		return super.getPageTitle(position);
	}
}