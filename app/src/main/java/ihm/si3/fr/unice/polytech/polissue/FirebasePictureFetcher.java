package ihm.si3.fr.unice.polytech.polissue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fecth a picture from firebase, and loads it in a imageView, handling the thumnailing and caching
 */
public class FirebasePictureFetcher {
    ImageView view;
    private Context context;
    private static final String TAG = "FirebasePictureFetcher";

    public FirebasePictureFetcher(ImageView view) {
        this.view = view;
        this.context = view.getContext();
    }

    public void fetch(StorageReference ref, File dir) throws IOException {
        fetch(ref, dir, false);
    }

    /**
     * if necesary fetch the picture from the {@link StorageReference}, stores it in the {@link File}
     * display it in the {@link ImageView}.
     * If thumbnail is true load a smaller sized picture.
     *
     * @param ref       {@link StorageReference} the picture file fire storage reference
     * @param dir       {@link File} the local dir where it should be stored
     * @param thumbnail boolean if true the picture will be scaled down to match the view size
     * @throws IOException uppon failure to open the passed files
     */
    public void fetch(StorageReference ref, File dir, boolean thumbnail) throws IOException {
        File pictureFile = new File(dir, ref.getName());
        if (!pictureFile.exists()) {
            pictureFile = File.createTempFile(ref.getName(), null, dir);
            final Uri pictureURI = FileProvider.getUriForFile(context, "fr.unice.polytech.polissue.fileprovider", pictureFile);
            ref.getFile(pictureFile).addOnSuccessListener(taskSnapshot -> {
                try {
                    setPicture(thumbnail, pictureURI);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "fetch: failed setting picture", e);
                }
            });
        } else {
            Uri pictureURI = FileProvider.getUriForFile(context, "fr.unice.polytech.polissue.fileprovider", pictureFile);
            setPicture(thumbnail, pictureURI);
        }
    }

    /**
     * set the view picture to the passed picture uri, thumbnailing it if necesary using getThumbnailBitmap
     *
     * @param thumbnail  boolean if true the picture will be thumnailed
     * @param pictureURI {@link Uri} uri of the picture to be loaded
     * @throws FileNotFoundException thrown upon thumbnailing
     */
    private void setPicture(boolean thumbnail, Uri pictureURI) throws FileNotFoundException {
        if (thumbnail) {
            Bitmap thumnbail = getThumbnailBitmap(pictureURI);
            view.setImageBitmap(thumnbail);
        } else {
            view.setImageURI(pictureURI);
        }
    }

    /**
     * Generate the thumbnailed bitmap for the passed pictureURI
     *
     * @param pictureURI uri to be thumbnailed
     * @return Bitmap Thumbnailed bitmap
     * @throws FileNotFoundException if the uri couldn't be found.
     */
    private Bitmap getThumbnailBitmap(Uri pictureURI) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream stream = context.getContentResolver().openInputStream(pictureURI);
        BitmapFactory.decodeStream(stream, null, options);
        options.inSampleSize = FirebasePictureFetcher.calculateInSampleSize(options, view.getMaxWidth(), view.getMaxHeight());
        options.inJustDecodeBounds = false;
        stream = context.getContentResolver().openInputStream(pictureURI);
        return BitmapFactory.decodeStream(stream, null, options);
    }

    /**
     * Calculate the sample size for the bitmap factory option
     * Source : https://developer.android.com/topic/performance/graphics/load-bitmap
     *
     * @param options   {@link BitmapFactory.Options} bitmapOptions
     * @param reqWidth  int wanted width
     * @param reqHeight int wanted height
     * @return int sample size needed for the bitmap loading
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
