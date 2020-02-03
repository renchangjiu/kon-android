package com.htt.kon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/03 21:00
 */
public class LocalMusicFragment extends Fragment {
    public static final String FLAG_SINGLE = "single";
    public static final String FLAG_ARTIST = "artist";
    public static final String FLAG_ALBUM = "album";
    public static final String FLAG_DIR = "dir";

    private LocalMusicActivity activity;

    private String flag;

    @BindView(R.id.flm_listView)
    ListView listView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
        this.flag = getArguments().getString("flag");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        ButterKnife.bind(this, view);

        switch (this.flag) {
            case FLAG_SINGLE:
                View headerView = inflater.inflate(R.layout.list_header_view, container, false);
                this.listView.addHeaderView(headerView);
                break;
            case FLAG_ARTIST:
                break;
            case FLAG_ALBUM:
                break;
            case FLAG_DIR:
                break;
            default:
        }
        return view;
    }

    private LocalMusicFragment() {
        super();
    }

    public static LocalMusicFragment getInstance(String flag) {
        LocalMusicFragment instance = new LocalMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("flag", flag);
        instance.setArguments(bundle);
        return instance;
    }

    private class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
