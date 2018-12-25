package com.visualcheckbook.visualcheckbook.BooksSQLiteDataBase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visualcheckbook.visualcheckbook.R;

import java.util.ArrayList;

/**
 * Created by Parsania Hardik on 26-Apr-17.
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BookModel> bookModelArrayList;

    public CustomAdapter(Context context, ArrayList<BookModel> bookModelArrayList) {

        this.context = context;
        this.bookModelArrayList = bookModelArrayList;
    }


    @Override
    public int getCount() {
        return bookModelArrayList.size();
    }

    @Override
    public BookModel getItem(int position) {
        return bookModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.tvname = (TextView) convertView.findViewById(R.id.isbn);
            holder.tvhobby = (TextView) convertView.findViewById(R.id.name);
            holder.tvcity = (TextView) convertView.findViewById(R.id.author);


            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvname.setText("ISBN: "+ bookModelArrayList.get(position).getIsbn());
        holder.tvhobby.setText("Name: "+bookModelArrayList.get(position).getName());
        holder.tvcity.setText("Author: "+bookModelArrayList.get(position).getAuthor());

        return convertView;
    }

    private class ViewHolder {

        protected TextView tvname, tvhobby, tvcity;
    }

}
