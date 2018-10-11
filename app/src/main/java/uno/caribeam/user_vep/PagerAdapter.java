package uno.caribeam.user_vep;

/**
 * Created by MIB on 29/02/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MapTab tab1 = new MapTab();
                return tab1;
            /*case 1:
                Taxitab tab2 = new Taxitab();
                return tab2;*/
            case 1:
                Ratetab tab2 = new Ratetab();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}