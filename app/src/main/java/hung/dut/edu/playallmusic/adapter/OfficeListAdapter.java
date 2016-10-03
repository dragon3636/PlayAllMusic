package hung.dut.edu.playallmusic.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hung.dut.edu.playallmusic.MainActivity;
import hung.dut.edu.playallmusic.R;

/**
 * Created by Administrator on 27/09/2016.
 */
public class OfficeListAdapter extends ArrayAdapter<HashMap<String,String>> {
    List<HashMap<String, String>> mSongList;
    Context mContext;
    LayoutInflater mLayoutInflater;

    public OfficeListAdapter(Context context, List<HashMap<String, String>> listsong) {
        super(context, 0, listsong);
        this.mContext =context;
        this.mSongList =listsong;
        mLayoutInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        if (mSongList == null || mSongList.isEmpty()) {
            return 0;
        }
        return mSongList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final HashMap<String, String> song = mSongList.get(position);
        final ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_list, parent, false);
            // Lookup view for data population
            viewHolder = new ViewHolder();
            viewHolder.tvname = (TextView) convertView.findViewById(R.id.txtname);
            viewHolder.tvtime = (TextView) convertView.findViewById(R.id.txtcasi);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.tvname.setText(song.get("songTitle"));
        viewHolder.tvtime.setText(song.get("songPath"));
        // Return the completed view to render on screen
        /*viewHolder.btvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemsListner.onClick(word.getCode());
            }
        });*/
      /*  viewHolder.containerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemsListner.onItemClick(word);
                Toast.makeText(mContext, " go to  Detai Word :"+ word.getCode(), Toast.LENGTH_SHORT).show();
            }
        });*/
        return convertView;
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ViewHolder {
        TextView tvname;
        TextView tvtime;
    }
}
