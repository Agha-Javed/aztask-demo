package aztask.app.com.aztask.ui;

import java.util.List;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.service.TaskNotificationService;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
        holder.coverImageView.setTag(list.get(position).getImageResourceId());
        holder.likeImageView.setTag((list.get(position).getIsfav() > 0) ? R.drawable.ic_liked : R.drawable.ic_like);
        holder.likeImageView.setImageResource((list.get(position).getIsfav() > 0) ? R.drawable.ic_liked : R.drawable.ic_like);
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }


    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView coverImageView;
        ImageView likeImageView;
        ImageView shareImageView;

        TaskViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);

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

                        Intent itent = new Intent(MainActivity.getAppContext(), TaskNotificationService.class);
                        itent.putExtra("action", "likeTask");
                        itent.putExtra("taskId", titleTextView.getId());
                        itent.putExtra("userId", MainActivity.getUserId());
                        MainActivity.getAppContext().startService(itent);
                    } else {

                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);

                        Intent itent = new Intent(MainActivity.getAppContext(), TaskNotificationService.class);
                        itent.putExtra("action", "unLikeTask");
                        itent.putExtra("taskId", titleTextView.getId());
                        itent.putExtra("userId", MainActivity.getUserId());
                        MainActivity.getAppContext().startService(itent);

                    }

                }
            });

            shareImageView.setOnClickListener(new View.OnClickListener() {
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
            });

        }
    }


    public void setData(List<TaskCard> data){
        this.list=data;
    }
}
