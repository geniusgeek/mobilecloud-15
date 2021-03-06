package vandy.mooc.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.operations.HobbitOps;

/**
 * This Activity illustrates how to use the HobbitContentProvider to
 * perform various "CRUD" (i.e., insert, query, update, and delete)
 * operations using characters from Tolkien's classic book "The
 * Hobbit."  It plays the role of the "View" in the
 * Model-View-Presenter pattern.
 */
public class HobbitActivity extends GenericActivity<HobbitOps> {

    /**
     * Uri for the "Necromancer".
     */
    private Uri mNecromancerUri;
    
    /**
     * Used to display the results of contacts queried from the
     * HobbitContentProvider.
     */
    private SimpleCursorAdapter mAdapter;


    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., initializing
     * views.
     *
     * @param savedInstanceState
     *            object that contains saved state information.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call up to the special onCreate() method in
        // GenericActivity, passing in the HobbitOps class to
        // instantiate and manage.
        super.onCreate(savedInstanceState,  HobbitOps.class);


        // Set the content view for this Activity.
        setContentView(R.layout.hobbit_activity);

        // Initialize the List View.
        /*
      ListView displays the Hobbit character information.
     */
        ListView mListView = (ListView) findViewById(R.id.list);

        // Initialize the SimpleCursorAdapter.
        mAdapter = getOps().makeCursorAdapter();

        // Connect the ListView with the SimpleCursorAdapter.
        mListView.setAdapter(mAdapter);
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  This method may not always be called
     * when the Android system kills the hosting process.
     */
    @Override
    public void onDestroy() {
        // Call up to the superclass's onDestroy() hook method.
        super.onDestroy();
        
        // Close down the HobbitOps.
        getOps().close();
    }

    /**
     * This method is run when the user clicks the "Add All" button.
     * It insert various characters from the Hobbit book into the
     * "database".
     */
    public void addAll(View v) {
        try {
            // Insert the main protagonist.
            getOps().insert("Bilbo",
                            "Hobbit");

            // Insert the main wizard.
            getOps().insert("Gandalf",
                            "Maia");

            // Insert all the dwarves.
            getOps().bulkInsert(new String[] { 
                    "Thorin", "Kili", "Fili",
                    "Balin", "Dwalin", "Oin", "Gloin",
                    "Dori", "Nori", "Ori",
                    "Bifur", "Bofur", "Bombur"
                },
                "Dwarf");

            // Insert the main antagonist.
            getOps().insert("Smaug",
                            "Dragon");

            // Insert Beorn.
            getOps().insert("Beorn",
                            "Man");

            // Insert the Master of Laketown
            getOps().insert("Master",
                            "Man");

            // Insert another antagonist.
            mNecromancerUri = 
                getOps().insert("Necromancer",
                                "Maia");

            // Display the results;
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * This method is run when the user clicks the "Modify All" button
     * to modify certain Hobbit characters from the "database."
     */
    public void modifyAll(View v) {
        try {
            // Update Beorn's race since he's a skinchanger.
            getOps().updateRaceByName("Beorn",
                                      "Bear");

            if (mNecromancerUri != null)
                // The Necromancer is really Sauron the Deceiver.
                getOps().updateByUri(mNecromancerUri,
                                     "Sauron",
                                     "Maia");

            // Delete dwarves who get killed in the Battle of Five
            // Armies.
            getOps().deleteByName(new String[] { 
                    "Thorin",
                    "Kili",
                    "Fili" 
                });

            // Delete Smaug since he gets killed by Bard the Bowman
            // and the "Master" (who's a man) since he's killed later
            // in the book.
            getOps().deleteByRace(new String[] { 
                    "Dragon",
                    "Man" 
                });

            // Display the results;
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * This method is run when the user clicks the "Delete All" button
     * to remove all Hobbit characters from the "database."
     */
    public void deleteAll(View v) {
        try {
            // Clear out the database.
            int numDeleted = getOps().deleteAll();

            // Inform the user how many characters were deleted.
            Toast.makeText(this,
                           "Deleted "
                           + numDeleted
                           + " Hobbit characters",
                           Toast.LENGTH_SHORT).show();

            // Display the results;
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * This method is run when the user clicks the "Display All"
     * button to display all races of Hobbit characters from the
     * "database."
     */
    public void displayAll(View v) {
        try {
            // Display the results.
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    public void displayCursor(Cursor cursor) {
    	// Display the designated columns in the cursor as a List in
        // the ListView connected to the SimpleCursorAdapter.
        mAdapter.changeCursor(cursor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return chooseOpsOption(item);
    }



    /**
     * Called by Android framework when menu option is clicked.
     * 
     * @param item
     * @return true
     */
    private boolean chooseOpsOption(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.contentResolver:
            getOps().setContentProviderAccessMeans
                (HobbitOps.ContentProviderAccessMeans.CONTENT_RESOLVER);
            Toast.makeText(this,
                           "ContentResolver selected",
                           Toast.LENGTH_SHORT).show();
            return true;

        case R.id.contentProviderClient:
            getOps().setContentProviderAccessMeans
                (HobbitOps.ContentProviderAccessMeans.CONTENT_PROVIDER_CLIENT); 
            Toast.makeText(this,
                           "ContentProviderClient selected",
                           Toast.LENGTH_SHORT).show();

            return true;
        }
        getOps().onConfiguration(this,true);//after the selection is performed allow the content resolver to reconnect with the
        //// content provider
        return false;//return super.onOptionsItemSelected(item);
    }

    /**
     * Inflates the Operations ("Ops") Option Menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ops_options_menu,
                         menu);
        return true;
    }
}
