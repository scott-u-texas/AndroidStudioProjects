package examples.scottm.smsreminder;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

/* TODO
 1. HANDLE CHECKBOXES!!!!!
    just like country examples, sparse boolean array?
 2. RETURN RESULT
 3. SWITCH TO LOADER?
 */

public class GetMultipleContactActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_multiple_contact);
        populateContactListView();
    }

    private void populateContactListView() {

        String[] columns = {
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts._ID};

        int[] listViewIDs = {R.id.name};

        String selectionClause
                = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?";

        String[] selectionArgs
                = {Integer.toString(1)};

        Cursor contactData = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                columns,
                selectionClause,
                selectionArgs,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);


        ListAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.contact_list_view,
                contactData, columns, listViewIDs);

        setListAdapter(adapter);
    }



}
