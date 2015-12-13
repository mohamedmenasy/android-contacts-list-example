package com.mohamedmenasy.contactlist.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mohamedmenasy.contactlist.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by MAX on 12/13/2015.
 */

public class ImageCursorAdapter extends SimpleCursorAdapter {

    public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String contactName = cursor.getString(cursor.getColumnIndex(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                ContactsContract.Contacts.DISPLAY_NAME));
        String imageUriStr = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        InputStream inputStream = null;

        if (imageUriStr != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(imageUriStr));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageView contactImageView = (ImageView) view.findViewById(R.id.contact_image_IV);
            contactImageView.setImageDrawable(Drawable.createFromStream(inputStream, imageUriStr));

        }
        TextView contactTextView = (TextView) view.findViewById(R.id.contact_name_TV);
        contactTextView.setText(contactName);

    }


}
