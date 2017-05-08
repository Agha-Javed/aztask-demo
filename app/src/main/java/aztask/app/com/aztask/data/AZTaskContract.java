package aztask.app.com.aztask.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by javed.ahmed on 4/14/2017.
 */

public final class AZTaskContract {

    public static final String PATH_NEARYBY_TASKS = "nearby_tasks";
    public static final String PATH_ASSIGNED_TASKS = "assigned_tasks";
    public static final String PATH_MY_TASKS = "my_tasks";

    public static final String CONTENT_AUTHORITY = "com.aztask.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri NEARBY_TASKS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEARYBY_TASKS);
    public static final Uri ASSIGNED_TASKS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ASSIGNED_TASKS);
    public static final Uri MY_TASKS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MY_TASKS);

    private AZTaskContract(){}

/*
    public static class NearByTaskEntry implements BaseColumns{

        public static final String TABLE_NAME="near_by_tasks";


        public static final String COLUMN_NAME_ID=BaseColumns._ID;
        public static final String COLUMN_NAME_TASK_ID="task_id";
        public static final String COLUMN_NAME_TASK_DESC="task_desc";
        public static final String COLUMN_NAME_TASK_TIME="task_time";
        public static final String COLUMN_NAME_TASK_CATEGORY="task_category";
        public static final String COLUMN_NAME_TASK_LOCATION="task_location";
        public static final String COLUMN_NAME_TASK_MIN_MAX_BUDGET="task_min_max_budget";
        public static final String COLUMN_NAME_TASK_OWNER_NAME="task_owner_name";
        public static final String COLUMN_NAME_TASK_OWNER_CONTACT="task_owner_contact";
        public static final String COLUMN_NAME_TASK_LIKED="is_task_liked";

    }
*/

    public static class NearByTaskEntry implements BaseColumns{

        public static final String TABLE_NAME="near_by_tasks";

        public static final String COLUMN_NAME_ID=BaseColumns._ID;
        public static final String COLUMN_NAME_DATA ="data";

        public static final String COLUMN_NAME_TASK_ID="task_id";
        public static final String COLUMN_NAME_TASK_DESC="task_desc";
        public static final String COLUMN_NAME_TASK_TIME="task_time";
        public static final String COLUMN_NAME_TASK_CATEGORY="task_category";
        public static final String COLUMN_NAME_TASK_LOCATION="task_location";
        public static final String COLUMN_NAME_TASK_MIN_MAX_BUDGET="task_min_max_budget";
        public static final String COLUMN_NAME_TASK_OWNER_NAME="task_owner_name";
        public static final String COLUMN_NAME_TASK_OWNER_CONTACT="task_owner_contact";
        public static final String COLUMN_NAME_TASK_LIKED="is_task_liked";


    }

    public static class AssignedTaskEntry implements BaseColumns{

        public static final String TABLE_NAME="assigned_tasks";

        public static final String COLUMN_NAME_ID=BaseColumns._ID;
        public static final String COLUMN_NAME_DATA="data";
        public static final String COLUMN_NAME_TASK_ID="task_id";
        public static final String COLUMN_NAME_TASK_DESC="task_desc";
        public static final String COLUMN_NAME_TASK_TIME="task_time";
        public static final String COLUMN_NAME_TASK_CATEGORY="task_category";
        public static final String COLUMN_NAME_TASK_LOCATION="task_location";
        public static final String COLUMN_NAME_TASK_MIN_MAX_BUDGET="task_min_max_budget";
        public static final String COLUMN_NAME_TASK_OWNER_NAME="task_owner_name";
        public static final String COLUMN_NAME_TASK_OWNER_CONTACT="task_owner_contact";
        public static final String COLUMN_NAME_TASK_LIKED="is_task_liked";


    }

    public static class MyTaskEntry implements BaseColumns{

        public static final String TABLE_NAME="my_tasks";

        public static final String COLUMN_NAME_DATA="data";
        public static final String COLUMN_NAME_ID=BaseColumns._ID;
        public static final String COLUMN_NAME_TASK_ID="task_id";
        public static final String COLUMN_NAME_TASK_DESC="task_desc";
        public static final String COLUMN_NAME_TASK_TIME="task_time";
        public static final String COLUMN_NAME_TASK_CATEGORY="task_category";
        public static final String COLUMN_NAME_TASK_LOCATION="task_location";
        public static final String COLUMN_NAME_TASK_MIN_MAX_BUDGET="task_min_max_budget";
        public static final String COLUMN_NAME_TASK_OWNER_NAME="task_owner_name";
        public static final String COLUMN_NAME_TASK_OWNER_CONTACT="task_owner_contact";
        public static final String COLUMN_NAME_TASK_LIKED="is_task_liked";



    }

}
