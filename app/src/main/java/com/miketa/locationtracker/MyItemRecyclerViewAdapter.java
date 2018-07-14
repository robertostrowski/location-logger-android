package com.miketa.locationtracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.miketa.locationtracker.RouteFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Routes} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Routes> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<Routes> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
       // holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UserTask mUserTask = new UserTask(3, 0, holder.mDeleteButton.getContext(), Integer.parseInt(mValues.get(holder.getAdapterPosition()).id), null, null);
                mUserTask.execute((Void) null);
            }
        });
        holder.mShowInMapImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(null != mListener)
                {
                    mListener.onListFragmentInteraction(Integer.parseInt(mValues.get(holder.getAdapterPosition()).id));
                }
            }
        });
        holder.mNavigateImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(null != mListener)
                {
                    mListener.onNavigateImageButtonInteraction(Integer.parseInt(mValues.get(holder.getAdapterPosition()).id));
                }
            }
        });
        holder.mChangeName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(null != mListener)
                {
                    mListener.onChangeNameButtonInteraction(Integer.parseInt(mValues.get(holder.getAdapterPosition()).id), mValues.get(holder.getAdapterPosition()).wholeJson);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final Button mDeleteButton;
        public final Button mChangeName;
        public final ImageButton mShowInMapImageButton;
        public final ImageButton mNavigateImageButton;
        public Routes mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            mDeleteButton = (Button) view.findViewById(R.id.delete_route_button);
            mShowInMapImageButton = (ImageButton) view.findViewById(R.id.show_in_map_imagebutton);
            mNavigateImageButton = (ImageButton) view.findViewById(R.id.navigate_imageButton);
            mChangeName = (Button) view.findViewById(R.id.change_route_name_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
