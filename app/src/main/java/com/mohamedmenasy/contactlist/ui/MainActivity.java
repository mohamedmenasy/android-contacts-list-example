package com.mohamedmenasy.contactlist.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.mohamedmenasy.contactlist.R;
import com.mohamedmenasy.contactlist.adapter.ImageCursorAdapter;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int CONTACT_ID_INDEX = 0;
    private static final int CONTACTS_LOADER_ID = 1;
    private ImageCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLoaderManager().initLoader(CONTACTS_LOADER_ID,
                null,
                this);
        setupCursorAdapter();
        ListView listViewContacts = (ListView) findViewById(R.id.contacts_list);
        listViewContacts.setAdapter(mAdapter);
        listViewContacts.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define the columns to retrieve
        String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI};
        // Construct the loader
        // Return the loader for use
        return new CursorLoader(this,
                ContactsContract.Contacts.CONTENT_URI, // URI
                projectionFields, // projection fields
                null, // the selection criteria
                null, // the selection args
                null // the sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }

    private void setupCursorAdapter() {
        // Column data from cursor to bind views from
        String[] uiBindFrom = {Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI};
        // View IDs which will have the respective column data inserted
        int[] uiBindTo = {R.id.contact_name_TV, R.id.contact_image_IV};
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),
        mAdapter = new ImageCursorAdapter(
                this, R.layout.contacts_list_item,
                null, uiBindFrom, uiBindTo,
                0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StringBuilder stringBuilder = new StringBuilder();
        // Get the Cursor
        Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        long mContactId = cursor.getLong(CONTACT_ID_INDEX);

        //Get all phone numbers for the contact
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + mContactId, null, null);
        if ((phones != null ? phones.getCount() : 0) > 0)

            stringBuilder.append("Phones \n");

        while (phones != null && phones.moveToNext()) {
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            stringBuilder.append(number + " ");

            switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    stringBuilder.append("(Home)\n");
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    stringBuilder.append("(Mobile)\n");
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    stringBuilder.append("(Work)\n");
                    break;
            }
        }
        if (phones != null) {
            phones.close();
        }

        //Get all emails for the contact
        stringBuilder.append("\n");


        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + mContactId, null, null);
        if ((emails != null ? emails.getCount() : 0) > 0)
            stringBuilder.append("Emails \n");
        while (emails != null && emails.moveToNext()) {
            String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            stringBuilder.append(email + " \n");
        }
        if (emails != null) {
            emails.close();
        }
        showContactDetailsDialog(stringBuilder.toString());
    }

    //Display a dialog with Contact details
    private void showContactDetailsDialog(String content) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(getString(R.string.contact_details))
                .setContentText(content)
                .setConfirmText(getString(R.string.ok))
                .show();
    }

}
