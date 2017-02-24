package aztask.app.com.aztask;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

public class TabsAdapter extends FragmentPagerAdapter{
   int mNumOfTabs;
   AppCompatActivity mainActivity;
	 
	public TabsAdapter(AppCompatActivity mainActivity,FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mainActivity=mainActivity;
        this.mNumOfTabs = NumOfTabs;
    }

	@Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NearByTasksTab tab2 = new NearByTasksTab(mainActivity);
                return tab2;
            case 1:
                MyTasksTab tab1 = new MyTasksTab(mainActivity);
                return tab1;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
