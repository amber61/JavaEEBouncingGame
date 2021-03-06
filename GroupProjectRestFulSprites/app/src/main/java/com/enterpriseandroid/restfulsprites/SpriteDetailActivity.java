package com.enterpriseandroid.restfulsprites;
/* File: SpriteDetailActivity.java
 * Course Name: CST8277_300
 * Student Name: Zhe Huang, Shunbiao Lin, Yingmei Zhu, Fasheng Zhang
 * Professor: Stanley Pieda
 * Reference: revised based on the starter code provided by Stanley Pieda
 * Description: Details of Sprites information
 * Date: April 15, 2017
 */
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import com.enterpriseandroid.restfulsprites.data.SpritesContract;

/**
 * Details of Sprites information
 *
 * @author Modified by Zhe Huang created on 4/15/2017.
 * @version 1.0.0
 * @see android.net.Uri
 * @see android.os.AsyncTask
 * @see android.os.Bundle
 * @see android.text.TextUtils
 * @see android.view.View
 * @see android.widget.Button
 * @see android.widget.TextView
 * @see android.app.LoaderManager
 * @see android.content.ContentResolver
 * @see android.content.ContentValues
 * @see android.content.CursorLoader
 * @see android.content.Intent
 * @see android.content.Loader
 * @see android.database.Cursor
 * @see com.enterpriseandroid.restfulsprites.data.SpritesContract
 * @since 1.8.0_73
 */
public class SpriteDetailActivity extends BaseActivity
    implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String KEY_URI = "SpriteDetailActivity.SPRITE_URI";

    private static final int LOADER_ID = 58;

    private static final String[] PROJ = new String[] {
        // values taken from SpritesContract.java
        SpritesContract.Columns.ID,
        SpritesContract.Columns.COLOR,
        SpritesContract.Columns.DX,
        SpritesContract.Columns.DY,
        SpritesContract.Columns.PANEL_HEIGHT,
        SpritesContract.Columns.PANEL_WIDTH,
        SpritesContract.Columns.X,
        SpritesContract.Columns.Y,
        SpritesContract.Columns.STATUS
    };

    static class UpdateSprite extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues vals;

        public UpdateSprite(ContentResolver resolver, ContentValues vals) {
            this.resolver = resolver;
            this.vals = vals;
        }

        @Override
        protected Void doInBackground(Uri... args) {
            Uri uri = args[0];
            if (null != uri) { resolver.update(uri, vals, null, null); }
            else { resolver.insert(SpritesContract.URI, vals); }
            return null;
        }
    }

    static class DeleteSprite extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public DeleteSprite(ContentResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        protected Void doInBackground(Uri... args) {
            resolver.delete(args[0], null, null);
            return null;
        }
    }

    private View statusView;
    private TextView idView;
    private String id = "";
    private TextView colorView;
    private String color = "";
    private TextView dxView;
    private String dx = "";
    private TextView dyView;
    private String dy = "";
    private TextView panelheightView;
    private String panelheight = "";
    private TextView panelwidthView;
    private String panelwidth = "";
    private TextView xView;
    private String x = "";
    private TextView yView;
    private String y = "";
    private Uri spriteUri;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, spriteUri, PROJ, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        populateView(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        if (null == state) { state = getIntent().getExtras(); }
        String uri = null;
        if (null != state) { uri = state.getString(KEY_URI); }
        if (null != uri) {
            spriteUri = Uri.parse(uri);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        setContentView(R.layout.activity_sprite_details);

        statusView = findViewById(R.id.activity_detail_status);
        idView = (TextView) findViewById(R.id.activity_detail_id);
        colorView = (TextView) findViewById(R.id.activity_detail_color);
        dxView = (TextView) findViewById(R.id.activity_detail_dx);
        dyView = (TextView) findViewById(R.id.activity_detail_dy);
        panelheightView = (TextView) findViewById(R.id.activity_detail_panelheight);
        panelwidthView = (TextView) findViewById(R.id.activity_detail_panelwidth);
        xView = (TextView) findViewById(R.id.activity_detail_x);
        yView = (TextView) findViewById(R.id.activity_detail_y);

        ((Button) findViewById(R.id.activity_detail_update)).setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) { update(); }
            });

        ((Button) findViewById(R.id.activity_detail_delete)).setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v) { delete( ); }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        if (null != spriteUri) {
            state.putString(KEY_URI, spriteUri.toString());
        }
    }

    void delete() {
        if (null != spriteUri) {
            new DeleteSprite(getContentResolver()).execute(spriteUri);
        }
        goToSprites();
    }

    void update() {
//        String s = fnameView.getText().toString();
//        if (TextUtils.isEmpty(s)) {
//            Toast.makeText(
//                this,
//                R.string.name_required,
//                Toast.LENGTH_SHORT)
//                .show();
//            return;
//        }

        ContentValues vals = new ContentValues();
        addString(idView, id, vals, SpritesContract.Columns.ID);
        addString(colorView, color, vals, SpritesContract.Columns.COLOR);
        addString(dxView, dx, vals, SpritesContract.Columns.DX);
        addString(dyView, dy, vals, SpritesContract.Columns.DY);
        addString(panelheightView, panelheight, vals, SpritesContract.Columns.PANEL_HEIGHT);
        addString(panelwidthView, panelwidth, vals, SpritesContract.Columns.PANEL_WIDTH);
        addString(xView, x, vals, SpritesContract.Columns.X);
        addString(yView, y, vals, SpritesContract.Columns.Y);

        new UpdateSprite(getContentResolver(), vals).execute(spriteUri);

        goToSprites();
    }

    private void populateView(Cursor c) {
        if (!c.moveToNext()) { return; }

        setStatusBackground(
            c.getInt(c.getColumnIndex(SpritesContract.Columns.STATUS)),
            statusView);

        String s;
        s = getString(c, SpritesContract.Columns.ID);
        id = (TextUtils.isEmpty(s)) ? "" : s;
        idView.setText(id);
        s = getString(c, SpritesContract.Columns.COLOR);
        color = (TextUtils.isEmpty(s)) ? "" : s;
        colorView.setText(color);
        s = getString(c, SpritesContract.Columns.DX);
        dx = (TextUtils.isEmpty(s)) ? "" : s;
        dxView.setText(dx);
        s = getString(c, SpritesContract.Columns.DY);
        dy = (TextUtils.isEmpty(s)) ? "" : s;
        dyView.setText(dy);
        s = getString(c, SpritesContract.Columns.PANEL_HEIGHT);
        panelheight = (TextUtils.isEmpty(s)) ? "" : s;
        panelheightView.setText(panelheight);
        s = getString(c, SpritesContract.Columns.PANEL_HEIGHT);
        panelwidth = (TextUtils.isEmpty(s)) ? "" : s;
        panelwidthView.setText(panelwidth);
        s = getString(c, SpritesContract.Columns.X);
        x = (TextUtils.isEmpty(s)) ? "" : s;
        xView.setText(x);
        s = getString(c, SpritesContract.Columns.Y);
        y = (TextUtils.isEmpty(s)) ? "" : s;
        yView.setText(y);
    }

    private void goToSprites() {
        Intent intent = new Intent(this, SpritesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private String getString(Cursor c, String col) {
        return c.getString(c.getColumnIndex(col));
    }

    private void addString(
        TextView view,
        String oldVal,
        ContentValues vals,
        String col)
    {
        String s = view.getText().toString();
        if (!oldVal.equals(s)) { vals.put(col, s); }
    }
}
