package com.bluelakeloop.themeaningfulnoise;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.io.File;

public class TmnMainActivity extends AppCompatActivity {
	private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;

	private static final String WORD_FILES_DIRECTORY = "wordFiles";

	private File storagePath;

	public File getStoragePath() {
		return storagePath;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tmn_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (initStoragePathFirstStage()) initPagerAdapter();
	}

	private boolean initStoragePathFirstStage() {
		boolean useExternalStorage = false;
		if (checkIfExternalStorageIsAvailable()) {
			if (!checkForExternalStoragePermission()) return false;
			useExternalStorage = true;
		}
		return initStoragePathSecondStage(useExternalStorage);
	}

	private boolean initStoragePathSecondStage(final boolean useExternalStorage) {
		if (useExternalStorage)
			storagePath = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + getPackageName() + File.separator + WORD_FILES_DIRECTORY);
		else
			storagePath = new File(getApplicationContext().getApplicationInfo().dataDir + File.separator + WORD_FILES_DIRECTORY);
		return storagePath.exists() || storagePath.mkdirs();
	}

	private boolean checkForExternalStoragePermission() {
		boolean permissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		if (permissionGranted) return true;
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
		return false;
	}

	private boolean checkIfExternalStorageIsAvailable() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}

	private void initPagerAdapter() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		final ViewPager viewPager = findViewById(R.id.container);
		viewPager.setAdapter(sectionsPagerAdapter);

		TabLayout tabLayout = findViewById(R.id.tabs);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.length > 0) {
			boolean useExternalStorage = true;
			if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					Snackbar.make(findViewById(R.id.main_layout), R.string.permission_external_storage_rationale, Snackbar.LENGTH_INDEFINITE)
									.setAction(R.string.ok, new OnClickListener() {

										@Override
										public void onClick(View view) {
											ActivityCompat.requestPermissions(TmnMainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
										}
									})
									.show();
					return;
				} else {
					useExternalStorage = false;
				}
			}
			if (initStoragePathSecondStage(useExternalStorage)) initPagerAdapter();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_tmn_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Fragment for tab 1 (word list)
	 */
	public static class WordListFragment extends Fragment {

		public WordListFragment() {
		}

		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_tmn_word_list, container, false);
		}
	}

	/**
	 * Fragment for tab 2 (word quiz)
	 */
	public static class WordQuizFragment extends Fragment {

		public WordQuizFragment() {
		}

		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_tmn_word_quiz, container, false);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) return new WordListFragment();
			return new WordQuizFragment();
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}
	}
}
