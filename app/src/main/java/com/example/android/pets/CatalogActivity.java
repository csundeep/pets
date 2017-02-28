/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int PET_LOADER = 0;

    private PetsCursorAdapter petsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView petListView = (ListView) findViewById(R.id.pet_list);

        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        petsCursorAdapter = new PetsCursorAdapter(this, null);
        petListView.setAdapter(petsCursorAdapter);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);


                Uri currentPetUri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    private void insertPet() {
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);
        Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED};

        return new CursorLoader(this, PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        petsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petsCursorAdapter.swapCursor(null);
    }


    //
//    private void displayDatabaseInfo() {
//
//        String[] projection = {
//                PetContract.PetEntry._Id,
//                PetContract.PetEntry.COLUMN_PET_NAME,
//                PetContract.PetEntry.COLUMN_PET_BREED,
//                PetContract.PetEntry.COLUMN_PET_GENDER,
//                PetContract.PetEntry.COLUMN_PET_WEIGHT};
//
////        // Perform a query on the pets table
////        Cursor cursor = db.query(
////                PetContract.PetEntry.TABLE_NAME,   // The table to query
////                projection,            // The columns to return
////                null,                  // The columns for the WHERE clause
////                null,                  // The values for the WHERE clause
////                null,                  // Don't group the rows
////                null,                  // Don't filter by row groups
////                null);                   // The sort order
//
//
//        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
//        ListView petListView = (ListView) findViewById(R.id.pet_list);
//
//        View emptyView = findViewById(R.id.empty_view);
//        petListView.setEmptyView(emptyView);
//
//        PetsCursorAdapter adapter = new PetsCursorAdapter(this, cursor);
//        petListView.setAdapter(adapter);
//
////        try {
////
////            int idColumnIndex = cursor.getColumnIndex(PetContract.PetEntry._ID);
////            int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
////            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
////            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
////            int weightColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);
////
////            while (cursor.moveToNext()) {
////                int currentID = cursor.getInt(idColumnIndex);
////                String currentName = cursor.getString(nameColumnIndex);
////                String currentBreed = cursor.getString(breedColumnIndex);
////                int currentGender = cursor.getInt(genderColumnIndex);
////                int currentWeight = cursor.getInt(weightColumnIndex);
////                displayView.append(("\n" + currentID + " - " +
////                        currentName + " - " +
////                        currentBreed + " - " +
////                        currentGender + " - " +
////                        currentWeight));
////            }
////        } finally {
////            cursor.close();
////        }
//    }

}
