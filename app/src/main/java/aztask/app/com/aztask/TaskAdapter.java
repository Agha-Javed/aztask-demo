package aztask.app.com.aztask;

import java.util.List;
import aztask.app.com.aztask.data.TaskCard;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
	private List<TaskCard> list;

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
		holder.likeImageView.setTag((list.get(position).getIsfav()>0) ? R.drawable.ic_liked : R.drawable.ic_like);
		holder.likeImageView.setImageResource((list.get(position).getIsfav()>0) ? R.drawable.ic_liked : R.drawable.ic_like);
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
}
