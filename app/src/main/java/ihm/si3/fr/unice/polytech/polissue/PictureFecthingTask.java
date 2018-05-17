package ihm.si3.fr.unice.polytech.polissue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Fetches the remote picture and stores it in a imageView
 */
public class PictureFecthingTask extends AsyncTask<URL, Void, Bitmap> {
    private static final String TAG = "Picture Fectcher";
    private final ImageView imageView;

    PictureFecthingTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        if (urls.length == 0) return null;
        URL url = urls[0];

        return fetchPicture(url);

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) this.imageView.setImageBitmap(bitmap);

    }

    private Bitmap fetchPicture(URL url) {
        try {
            InputStream stream = (InputStream) url.getContent();
            return BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
        }
        return null;
    }

}
