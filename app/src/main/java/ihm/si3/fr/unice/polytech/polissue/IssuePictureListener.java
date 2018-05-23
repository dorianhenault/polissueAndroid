package ihm.si3.fr.unice.polytech.polissue;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import ihm.si3.fr.unice.polytech.polissue.model.IssueModel;

public class IssuePictureListener implements OnCompleteListener<UploadTask.TaskSnapshot> {
    private final IssueModel issue;
    private static final String TAG = "IssuePictureListener";

    public IssuePictureListener(IssueModel issue) {
        this.issue = issue;
    }

    @Override
    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
        Log.d(TAG, "onComplete: completed");
        if (task.isSuccessful()) Log.d(TAG, "onComplete: successfull");
        else Log.e(TAG, "onComplete: failed", task.getException());
    }
}
