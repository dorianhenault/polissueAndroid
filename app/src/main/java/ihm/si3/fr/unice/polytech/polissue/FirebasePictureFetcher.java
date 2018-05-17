package ihm.si3.fr.unice.polytech.polissue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fecth a picture from firebase, and loads it in a imageView.
 * Handle the cache
 */
public class FirebasePictureFetcher {
    ImageView view;
    private Context context;

    public FirebasePictureFetcher(ImageView view) {
        this.view = view;
        this.context = view.getContext();
    }

    public void fetch(StorageReference ref, File dir) throws IOException {
        fetch(ref, dir, false);
    }

    public void fetch(StorageReference ref, File dir, boolean thumbnail) throws IOException {
        File imageFile = new File(dir, ref.getName());
        if (!imageFile.exists()) {
            imageFile = File.createTempFile(ref.getName(), null, dir);
            final Uri imageURI = FileProvider.getUriForFile(context, "fr.unice.polytech.polissue.fileprovider", imageFile);
            ref.getFile(imageFile).addOnSuccessListener(taskSnapshot -> {
                try {
                    setImage(thumbnail, imageURI);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else {
            Uri imageUri = FileProvider.getUriForFile(context, "fr.unice.polytech.polissue.fileprovider", imageFile);
            setImage(thumbnail, imageUri);
        }
    }

    private void setImage(boolean thumbnail, Uri imageURI) throws FileNotFoundException {
        if (thumbnail) {
            Bitmap thumnbail = getThumbnailBitmap(imageURI);
            view.setImageBitmap(thumnbail);
        } else {
            view.setImageURI(imageURI);
        }
    }

    private Bitmap getThumbnailBitmap(Uri imageURI) throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream stream = context.getContentResolver().openInputStream(imageURI);
        BitmapFactory.decodeStream(stream, null, options);
        options.inSampleSize = FirebasePictureFetcher.calculateInSampleSize(options, view.getMaxWidth(), view.getMaxHeight());
        options.inJustDecodeBounds = false;
        stream = context.getContentResolver().openInputStream(imageURI);
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
    public static int calculateInSampleSize(
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
