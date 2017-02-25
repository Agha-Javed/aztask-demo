package aztask.app.com.aztask;

import aztask.app.com.aztask.service.TaskAcceptedNotificationService;
import aztask.app.com.aztask.ui.MainActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskViewHolder extends RecyclerView.ViewHolder{

	public TextView titleTextView;
	public ImageView coverImageView;
	public ImageView likeImageView;
	public ImageView shareImageView;

	public TaskViewHolder(View v) {
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
					Toast.makeText(MainActivity.getAppContext(), " Please register first.",Toast.LENGTH_SHORT).show();
					return;
				} 

				if (id == R.drawable.ic_like) {

					likeImageView.setTag(R.drawable.ic_liked);
					likeImageView.setImageResource(R.drawable.ic_liked);

					Intent itent = new Intent(MainActivity.getAppContext(), TaskAcceptedNotificationService.class);
					itent.putExtra("taskId", titleTextView.getId());
					itent.putExtra("userId", MainActivity.getUserId());
					MainActivity.getAppContext().startService(itent);
				} else {

					likeImageView.setTag(R.drawable.ic_like);
					likeImageView.setImageResource(R.drawable.ic_like);
					Toast.makeText(MainActivity.getAppContext(), titleTextView.getText() + " removed from favourites",
							Toast.LENGTH_SHORT).show();

				}

			}
		});

		shareImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!MainActivity.isUserRegistered()) {
					Toast.makeText(MainActivity.getAppContext(), " Please register first.",Toast.LENGTH_SHORT).show();
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
