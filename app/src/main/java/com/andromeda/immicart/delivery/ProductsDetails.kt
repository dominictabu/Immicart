package com.andromeda.immicart.delivery

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.andromeda.immicart.R


class TestFragmentAdapter(fm: FragmentManager, protected var banners: ArrayList<Images>) : FragmentPagerAdapter(fm) {


    private val mCount = 8

    override fun getItem(position: Int): Fragment {
        Log.d(TAG, "getItem()")
        return BannerFragment.newInstance(banners[position].url)
    }

    override fun getCount(): Int {
        return banners.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TestFragmentAdapter.Titles[position % CONTENT.size]
    }

    companion object {
        protected val CONTENT =
            intArrayOf(R.drawable.breadimage, R.drawable.aerial_soap, R.drawable.babycare600by450)
        protected val Titles = arrayOf("one", "2", "3", "4")
        private val TAG = "TestFragmentAdapter"
    }

}

