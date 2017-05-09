package aztask.app.com.aztask.ui;

import java.util.List;
import java.util.StringTokenizer;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.service.TaskNotificationService;
import aztask.app.com.aztask.util.Util;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static aztask.app.com.aztask.R.id.btnCall;
import static aztask.app.com.aztask.R.id.msg;
import static aztask.app.com.aztask.R.id.tvUserName;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {


    private List<TaskCard> list;

    public TaskAdapter() { }
    public TaskAdapter(List<TaskCard> Data) {
        list = Data;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
        TaskViewHolder holder = new TaskViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {
        holder.titleTextView.setText(list.get(position).getTaskDesc());
        holder.titleTextView.setId(Integer.parseInt(list.get(position).getTaskId()));
//        holder.tvTaskDate.setText(list.get(position).getTaskTime());
        holder.tvTaskDate.setText(Util.getFormattedDate(list.get(position).getTaskTime()));


        StringTokenizer tokens =(list.get(position).getTaskBudget()!=null) ? new StringTokenizer(list.get(position).getTaskBudget(), ":") : null;
        String minBudget =(tokens!=null) ? tokens.nextToken() :"0";
        String maxBudget =(tokens!=null) ? tokens.nextToken() :"0";

        String taskLocation=list.get(position).getTaskLocation();

        String taskMetaData="Budget:"+minBudget+"RM to "+maxBudget+"RM";
        taskMetaData+="\nLocation:"+Util.shortenText(taskLocation);

        holder.tvTaskMetaData.setText(taskMetaData);

        holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
        holder.coverImageView.setTag(list.get(position).getImageResourceId());

        if(list.get(position).getTaskOwnerContact()!=null){
            holder.ivCall.setTag(list.get(position).getTaskOwnerContact());
            //holder.ivMsg.setTag(list.get(position).getTaskOwnerContact());
            holder.ivMsg.setTag(R.id.tvUserName,list.get(position).getTaskOwnerName());
            holder.ivMsg.setTag(R.id.phone,list.get(position).getTaskOwnerContact());

            holder.tvUserName.setText("created by:"+list.get(position).getTaskOwnerName());
            holder.ivCall.setVisibility(View.VISIBLE);
            holder.ivMsg.setVisibility(View.VISIBLE);
            holder.tvUserName.setVisibility(View.VISIBLE);
        }else{
            holder.ivCall.setVisibility(View.INVISIBLE);
            holder.ivMsg.setVisibility(View.INVISIBLE);
            holder.tvUserName.setVisibility(View.INVISIBLE);
        }

        if((list.get(position).getIsfav() > 0)){
            holder.likeImageView.setTag(R.drawable.ic_liked);// : R.drawable.ic_like);
            holder.likeImageView.setImageResource(R.drawable.ic_liked);
            int color = Color.parseColor("#FF0080");
            holder.likeImageView.setColorFilter(color);
        }else{
            holder.likeImageView.setTag(R.drawable.ic_like);
            holder.likeImageView.setImageResource(R.drawable.ic_like);

        }

        holder.itemView.setTag(list.get(position).getTaskId());
    }



    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
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
                    if (!Util.isUserRegistered(null)) {
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

                    if (!Util.isUserRegistered(null)) {
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
                        itent.putExtra("userId", Util.getUserId(null));
                        MainActivity.getAppContext().startService(itent);
                    } else {

                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);

                        Intent itent = new Intent(MainActivity.getAppContext(), TaskNotificationService.class);
                        itent.putExtra("action", "unLikeTask");
                        itent.putExtra("taskId", titleTextView.getId());
                        itent.putExtra("userId", Util.getUserId(null));
                        MainActivity.getAppContext().startService(itent);

                    }

                }
            });

            /*shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!MainActivity.isUserRegistered()) {
                        Toast.makeText(MainActivity.getAppContext(), " Please register first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                            + MainActivity.getAppContext().getResources().getResourcePackageName(coverImageView.getId()) + '/' + "drawable" + '/'
                            + MainActivity.getAppContext().getResources().getResourceEntryName((Integer) coverImageView.getTag()));

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/jpeg");
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    MainActivity.getAppContext().startActivity(Intent.createChooser(shareIntent, MainActivity.getAppContext().getResources().getText(R.string.send_to)));

                }
            });*/

        }
    }


    public void setData(List<TaskCard> data){
        this.list=data;
    }
}
