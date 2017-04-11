package aztask.app.com.aztask.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class TaskComparatorByDate implements Comparator<TaskCard> {
    final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    final SimpleDateFormat dateFormatter=new SimpleDateFormat(TIME_FORMAT);

    @Override
    public int compare(TaskCard task1, TaskCard task2) {
        if (task1.getTaskTime() == null || task2.getTaskTime() == null)
            return 0;

        String dateOfTask1=task1.getTaskTime();
        String dateOfTask2=task2.getTaskTime();

        try {
            Date task1Date = dateFormatter.parse(dateOfTask1);
            Date task2Date = dateFormatter.parse(dateOfTask2);

            if (task1Date == null || task2Date == null)
                return 0;

            return task1Date.compareTo(task2Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
