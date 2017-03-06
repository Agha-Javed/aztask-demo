package aztask.app.com.aztask.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class TabsAdapter extends FragmentPagerAdapter{
   private int mNumOfTabs;
   private AppCompatActivity mainActivity;
   private Bundle bundle;
	 
	public TabsAdapter(AppCompatActivity mainActivity,FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mainActivity=mainActivity;
        this.mNumOfTabs = NumOfTabs;
    }

	@Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NearByTasksTab tab0 = new NearByTasksTab(mainActivity);
                return tab0;
            case 1:
                AssignedTaskTab tab1 = new AssignedTaskTab();
                if(bundle!=null){
                    tab1.setArguments(bundle);
                }
                return tab1;
            case 2:
                MyTasksTab tab2 = new MyTasksTab(mainActivity);
                if(tab2!=null){
                    tab2.setArguments(bundle);
                }
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void setArgumentsDataForFragments(Bundle bundle){
        this.bundle=bundle;
    }
}
