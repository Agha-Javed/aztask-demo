package aztask.app.com.aztask.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.AZTaskContract;
import aztask.app.com.aztask.service.TaskNotificationService;
import aztask.app.com.aztask.util.Util;

import static aztask.app.com.aztask.R.id.msg;

/**
 * Created by javed.ahmed on 4/25/2017.
 */

public class CursorTaskAdapter  extends CursorRecyclerViewAdapter<CursorTaskAdapter.TaskViewHolder> {

    private int attachedTab;

    public CursorTaskAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }
    public CursorTaskAdapter(int attachedTab,Context context, Cursor cursor){
        super(context,cursor);
        this.attachedTab =attachedTab;
    }

    @Override
    public CursorTaskAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
        CursorTaskAdapter.TaskViewHolder holder = new CursorTaskAdapter.TaskViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(CursorTaskAdapter.TaskViewHolder holder, Cursor cursor) {
        holder.titleTextView.setText(cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_DESC)));//list.get(position).getTaskDesc());

        holder.titleTextView.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_ID))));//list.get(position).getTaskId()));



//        holder.tvTaskDate.setText(list.get(position).getTaskTime());
        holder.tvTaskDate.setText(Util.getFormattedDate(cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_TIME))));//list.get(position).getTaskTime()));



        String taskBudget=cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET));
        StringTokenizer tokens =(taskBudget!=null) ? new StringTokenizer(taskBudget, ":") : null;
        String minBudget =(tokens!=null) ? tokens.nextToken() :"0";
        String maxBudget =(tokens!=null) ? tokens.nextToken() :"0";

        String taskLocation=cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_LOCATION));//list.get(position).getTaskLocation();



        String taskMetaData="Budget:"+minBudget+"RM to "+maxBudget+"RM";
        taskMetaData+="\nLocation:"+Util.shortenText(taskLocation);

        holder.tvTaskMetaData.setText(taskMetaData);

        holder.coverImageView.setImageResource(R.drawable.app_logo);
        holder.coverImageView.setTag(R.drawable.app_logo);


                String taskOwnerContact=cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT));
        if(taskOwnerContact!=null){
            holder.ivCall.setTag(taskOwnerContact);
            //holder.ivMsg.setTag(list.get(position).getTaskOwnerContact());
            holder.ivMsg.setTag(R.id.tvUserName,cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_NAME)));


            holder.ivMsg.setTag(R.id.phone,taskOwnerContact);


            holder.tvUserName.setText("created by:"+cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_NAME)));


            holder.ivCall.setVisibility(View.VISIBLE);
            holder.ivMsg.setVisibility(View.VISIBLE);
            holder.tvUserName.setVisibility(View.VISIBLE);
        }else{
            holder.ivCall.setVisibility(View.INVISIBLE);
            holder.ivMsg.setVisibility(View.INVISIBLE);
            holder.tvUserName.setVisibility(View.INVISIBLE);
        }

        String isTaskLiked= cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_LIKED));
        if(isTaskLiked!=null && isTaskLiked.length()>0 && isTaskLiked.equalsIgnoreCase("true")){
            holder.likeImageView.setTag(R.drawable.ic_liked);// : R.drawable.ic_like);
            holder.likeImageView.setImageResource(R.drawable.ic_liked);
            int color = Color.parseColor("#FF0080");
            holder.likeImageView.setColorFilter(color);
        }else{
            holder.likeImageView.setTag(R.drawable.ic_like);
            holder.likeImageView.setImageResource(R.drawable.ic_like);

        }

        holder.itemView.setTag(cursor.getString(cursor.getColumnIndex(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_ID)));

    }



    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView tvTaskDate;
        TextView tvTaskMetaData;
        TextView tvUserName;


        ImageView ivMsg;
        ImageView ivCall;

        ImageView coverImageView;
        ImageView likeImageView;

        // ImageView shareImageView;

        TaskViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            tvTaskDate= (TextView) v.findViewById(R.id.taskDate);
            tvTaskMetaData= (TextView) v.findViewById(R.id.tvTaskMetaData);

            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);

            tvUserName = (TextView) v.findViewById(R.id.tvUserName);

            ivMsg= (ImageView) v.findViewById(msg);
            ivMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi "+ivMsg.getTag(R.id.tvUserName)+",");
                    sendIntent.putExtra("address",""+ivMsg.getTag(R.id.phone));
                    sendIntent.setType("text/plain");
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.getAppContext().startActivity(sendIntent);
                }
            });

            ivCall= (ImageView) v.findViewById(R.id.phone);
            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isUserRegistered()) {
                        Toast.makeText(MainActivity.getAppContext(), " Please register first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+ivCall.getTag()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.getAppContext().startActivity(intent);
                }
            });


            // shareImageView = (ImageView) v.findViewById(shareImageView);

            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int id = (Integer) likeImageView.getTag();

                    if (!MainActivity.isUserRegistered()) {
                        Toast.makeText(MainActivity.getAppContext(), " Please register first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (id == R.drawable.ic_like) {

                        likeImageView.setTag(R.drawable.ic_liked);
                        likeImageView.setImageResource(R.drawable.ic_liked);
                        int color = Color.parseColor("#FF0080");
                        likeImageView.setColorFilter(color);

                        Intent itent = new Intent(MainActivity.getAppContext(), TaskNotificationService.class);
                        itent.putExtra("action", "likeTask");
                        itent.putExtra("taskId", titleTextView.getId());
                        itent.putExtra("userId", MainActivity.getUserId());
                        itent.putExtra("attachedTab", attachedTab);

                        MainActivity.getAppContext().startService(itent);
                    } else {

                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);

                        Intent itent = new Intent(MainActivity.getAppContext(), TaskNotificationService.class);
                        itent.putExtra("action", "unLikeTask");
                        itent.putExtra("taskId", titleTextView.getId());
                        itent.putExtra("userId", MainActivity.getUserId());
                        itent.putExtra("attachedTab", attachedTab);

                        MainActivity.getAppContext().startService(itent);

                    }

                }
            });

        }
    }


}
