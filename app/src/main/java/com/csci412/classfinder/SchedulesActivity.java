package com.csci412.classfinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SchedulesActivity extends AppCompatActivity {

    private String currEditText = null;
    public static CustomItems.ScheduleItem selectedSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);
        /*
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;
        SimpleItemRecyclerViewAdapter adapt = new SimpleItemRecyclerViewAdapter(this, CustomItems.SCHEDULES);
        recyclerView.setAdapter(adapt);


        Button newSchedBtn = findViewById(R.id.newScheduleButton);
        EditText newSchedET = findViewById(R.id.scheduleEditText);

        newSchedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currEditText != null) {
                    CustomItems.addSchedule(currEditText);
                }
            }
        });

        newSchedET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currEditText = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        */
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final MainActivity mParentActivity;
        private final List<CustomItems.ScheduleItem> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomItems.ScheduleItem item = (CustomItems.ScheduleItem) view.getTag();
                selectedSchedule = item;

                Context context = view.getContext();
                Intent intent = new Intent(context, SchedViewActivity.class);
                intent.putExtra(SchedViewFragment.ARG_ITEM_ID, item.name);

                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(MainActivity parent,
                                      List<CustomItems.ScheduleItem> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).name);
            holder.mContentView.setText("");

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setOnDragListener((view, event) -> {
                if(event.getAction() == DragEvent.ACTION_DROP){
                    Course x = (Course)event.getClipData().getItemAt(0).getIntent().getSerializableExtra("course");
                    mValues.get(position).classes.add(x);
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
