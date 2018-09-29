package com.example.jovel.learnux_kotlin.activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.widget.ImageView
import android.widget.TextView
import com.example.jovel.learnux_kotlin.R
import com.example.jovel.learnux_kotlin.adapter.VideoAdapter
import com.example.jovel.learnux_kotlin.fragment.PlaybackFragment
import com.example.jovel.learnux_kotlin.model.Video
import com.example.jovel.learnux_kotlin.publicants.Constants
import com.example.jovel.learnux_kotlin.utils.OnVideoClickListener
import com.example.jovel.learnux_kotlin.utils.OnVideoExitListener
import kotlinx.android.synthetic.main.app_bar_main.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v4.widget.DrawerLayout
import android.support.v4.view.GravityCompat
import android.net.ConnectivityManager
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.View
import com.example.jovel.learnux_kotlin.publicants.Constants.Companion.ALL_VIDEOS
import com.google.firebase.database.*

const val VIDEO_ARG = "id"

class MainActivity : AppCompatActivity(), OnVideoClickListener, OnVideoExitListener {

    private var mDatabase: DatabaseReference? = null
    private var mVideos: MutableList<Video>? = null
    private var mVideoAdapter: VideoAdapter? = null
    private var mPlaybackFragment: PlaybackFragment? = null
    private var mEmptyList: ImageView? = null
    private var mEmptyListText: TextView? = null

    private var mBasics: Boolean = false
    private var mNetworking: Boolean = false
    private var mSecurity: Boolean = false
    private var mStorage: Boolean = false
    private var mSystem: Boolean = false

    private var mBasicsSwitch: SwitchCompat? = null
    private var mNetworkingSwitch: SwitchCompat? = null
    private var mSecuritySwitch: SwitchCompat? = null
    private var mStorageSwitch: SwitchCompat? = null
    private var mSystemSwitch: SwitchCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mPlaybackFragment = PlaybackFragment()
        val args = Bundle()
        mPlaybackFragment!!.arguments = args

        mEmptyList = findViewById(R.id.main_empty_list)
        mEmptyList!!.setImageResource(R.drawable.ic_empty_list)

        mEmptyListText = findViewById(R.id.main_empty_list_text)
        mEmptyListText!!.setText(R.string.text_empty_list_message)

        mVideos = ArrayList()

        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.ALL_VIDEOS)
        getVideosFromFireDB()

        mVideoAdapter = VideoAdapter(mVideos as ArrayList<Video>, this)

        val mRecyclerView: RecyclerView = findViewById(R.id.recyclerview_videos)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mVideoAdapter

        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        armFilterSwitches(navigationView)
    }

    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)

            if (mPlaybackFragment!!.isVisible) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                onVideoExit()
            }
        } else {
            if (mPlaybackFragment!!.isVisible) {
                onVideoExit()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onVideoClick(video: Video) {
        mPlaybackFragment!!.arguments!!.putString(VIDEO_ARG, video.id)

        val ft = supportFragmentManager.beginTransaction()

        if (!mPlaybackFragment!!.isAdded) {
            ft.add(R.id.frag_video_container, mPlaybackFragment).commit()
        } else {
            ft.show(mPlaybackFragment).commit()
            mPlaybackFragment!!.playVideo()
        }
    }

    override fun onVideoExit() {
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(mPlaybackFragment).commit()
        mPlaybackFragment!!.pauseVideo()
    }

    private fun hasNetworkConnection(): Boolean {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo

        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    private fun attemptVideoCollection() {
        val runnable = Runnable { getVideosFromFireDB() }
        val handler = Handler()
        handler.postDelayed(runnable, 5000)
    }

    private fun showNoNetworkConnectionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_no_connection)
                .setMessage(R.string.dialog_message_no_connection)
                .setNeutralButton(R.string.dialog_button_ok) { _, _ -> attemptVideoCollection() }
                .show()
    }

    private fun armFilterSwitches(navigationView: NavigationView) {

        mBasicsSwitch = navigationView.menu.getItem(0).actionView.findViewById(R.id.drawer_switch)
        mBasicsSwitch!!.setChecked(mBasics)
        mBasicsSwitch!!.setOnCheckedChangeListener { _, isChecked ->
            mBasics = isChecked
            mBasicsSwitch!!.setChecked(mBasics)
            getVideosFromFireDB()
        }

        mNetworkingSwitch = navigationView.menu.getItem(1).actionView.findViewById(R.id.drawer_switch)
        mNetworkingSwitch!!.setChecked(mNetworking)
        mNetworkingSwitch!!.setOnCheckedChangeListener{_, isChecked ->
            mNetworking = isChecked
            mNetworkingSwitch!!.setChecked(mNetworking)
            getVideosFromFireDB()
        }

        mSecuritySwitch = navigationView.menu.getItem(2).actionView.findViewById(R.id.drawer_switch)
        mSecuritySwitch!!.setChecked(mSecurity)
        mSecuritySwitch!!.setOnCheckedChangeListener{ _, isChecked ->
            mSecurity = isChecked
            mSecuritySwitch!!.setChecked(mSecurity)
            getVideosFromFireDB()
        }

        mStorageSwitch = navigationView.menu.getItem(3).actionView.findViewById(R.id.drawer_switch)
        mStorageSwitch!!.setChecked(mStorage)
        mStorageSwitch!!.setOnCheckedChangeListener{ _, isChecked ->
            mStorage = isChecked
            mStorageSwitch!!.setChecked(mStorage)
            getVideosFromFireDB()
        }

        mSystemSwitch = navigationView.menu.getItem(4).actionView.findViewById(R.id.drawer_switch)
        mSystemSwitch!!.setChecked(mSystem)
        mSystemSwitch!!.setOnCheckedChangeListener { _, isChecked ->
            mSystem = isChecked
            mSystemSwitch!!.setChecked(mSystem)
            getVideosFromFireDB()
        }
    }

    private fun prepVideos(videos: List<Video>): List<Video> {

        if (!mBasics && !mNetworking && !mSecurity && !mStorage && !mSystem) {
            return videos
        }

        val filteredContent: MutableList<Video> = ArrayList()
        for (filteredVideo in videos){
            val category: String = filteredVideo.category

            when(category){
                Constants.BASICS_CATEGORY -> {
                    if(mBasics)
                        filteredContent.add(filteredVideo)
                }
                Constants.NETWORKING_CATEGORY -> {
                    if(mNetworking)
                        filteredContent.add(filteredVideo)
                }
                Constants.SECURITY_CATEGORY -> {
                    if(mSecurity)
                        filteredContent.add(filteredVideo)
                }
                Constants.STORAGE_CATEGORY -> {
                    if(mStorage)
                        filteredContent.add(filteredVideo)
                }
                Constants.SYSTEM_CATEGORY -> {
                    if(mSystem)
                        filteredContent.add(filteredVideo)
                }
            }
        }

        return filteredContent
    }

    private fun createVideoSet(iterable: Iterable<DataSnapshot>): MutableList<Video>{
        val videos: MutableList<Video> = ArrayList()

        for (item in iterable)
            videos.add(item.getValue(Video::class.java)!!)

        return videos
    }

    private fun getVideosFromFireDB(){
        if (!hasNetworkConnection()){
            showNoNetworkConnectionDialog()
            mEmptyList!!.visibility = View.VISIBLE
            mEmptyListText!!.visibility = View.VISIBLE
        }else{
            mEmptyList!!.visibility = View.INVISIBLE
            mEmptyListText!!.visibility = View.INVISIBLE
        }

        mDatabase!!.child(ALL_VIDEOS)
        mDatabase!!.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fireVideos : MutableList<Video> = createVideoSet(dataSnapshot.children)

                mVideos!!.clear()
                mVideos!!.addAll(prepVideos(fireVideos))

                mVideoAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

}