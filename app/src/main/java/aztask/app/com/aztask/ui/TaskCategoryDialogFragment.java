package aztask.app.com.aztask.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aztask.app.com.aztask.R;

/**
 * Created by javed.ahmed on 3/1/2017.
 */

public class TaskCategoryDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String selectedOptions);
        public void onDialogNegativeClick();
    }

    private List<String> mSelectedItems;
    private String[] taskCategoriesArray = {"Food", "Photography", "Educational", "Transport", "LifeStyle", "Entertainment", "Health", "Household", "Hobbies","Other"};
    private NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(mListener.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Please Choose Category")
                .setMultiChoiceItems(taskCategoriesArray, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int seletedItem,
                                                boolean isChecked) {
                                if (isChecked) {
                                    mSelectedItems.add(taskCategoriesArray[seletedItem]);
                                } else if (mSelectedItems.contains(seletedItem)) {
                                    mSelectedItems.remove(Integer.valueOf(seletedItem));
                                }
                            }
                        })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(convertListIntoString(mSelectedItems));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick();
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private String convertListIntoString(List<String> mSelectedItems){
        StringBuilder convertedStringBuilder=new StringBuilder("");
        for (String selectedItem: mSelectedItems){
            convertedStringBuilder.append(selectedItem).append(",");
        }
        return convertedStringBuilder.toString();
    }
}
