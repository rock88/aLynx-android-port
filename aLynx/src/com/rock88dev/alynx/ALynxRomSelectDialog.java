/** aLynx - Atari Lynx emulator for Android OS
 * 
 * Copyright (C) 2012
 * @author: rock88
 * 
 * e-mail: rock88a@gmail.com
 * 
 * http://rock88dev.blogspot.com
 * 
 */

package com.rock88dev.alynx;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ALynxRomSelectDialog extends ListActivity {
        private static final String ITEM_KEY = "key";
        private static final String ITEM_IMAGE = "image";
        private static final String ROOT = "/";
        public static final String START_PATH = "/sdcard";
        public static final String FORMAT_FILTER = "FORMAT_FILTER";
        public static final String RESULT_PATH = "RESULT_PATH";
        public static final String SELECTION_MODE = "SELECTION_MODE";
        public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";

        private ALynxSetting set;
        private List<String> path = null;
        private TextView myPath;
        private ArrayList<HashMap<String, Object>> mList;

        private Button selectButton;

        private LinearLayout layoutSelect;
        private LinearLayout layoutCreate;
        private String parentPath;
        private String currentPath = ROOT;

        private String[] formatFilter = {".lnx",".com",".o"};

        private boolean canSelectDir = false;

        private File selectedFile;
        private HashMap<String, Integer> lastPositions = new HashMap<String, Integer>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setResult(RESULT_CANCELED, getIntent());
                
                set = new ALynxSetting(this);
                set.loadSettings();
                
                setContentView(R.layout.file_dialog_main);
                myPath = (TextView) findViewById(R.id.path);

                selectButton = (Button) findViewById(R.id.fdButtonSelect);
                selectButton.setEnabled(false);
                selectButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                                if (selectedFile != null) {
                                        getIntent().putExtra(RESULT_PATH, selectedFile.getPath());
                                        setResult(RESULT_OK, getIntent());
                                        finish();
                                }
                        }
                });
                
            	final Button backButton = (Button) findViewById(R.id.fdButtonBack);
                backButton.setOnClickListener(new OnClickListener() {
                	public void onClick(View v) {
                		finish();
                        }
                });

                canSelectDir = getIntent().getBooleanExtra(CAN_SELECT_DIR, false);

                layoutSelect = (LinearLayout) findViewById(R.id.fdLinearLayoutSelect);
                layoutCreate = (LinearLayout) findViewById(R.id.fdLinearLayoutCreate);
                layoutCreate.setVisibility(View.GONE);
                
                String startPath;
                if (set.rom_path != null) startPath=set.rom_path;
                	else startPath = "/";//getIntent().getStringExtra(START_PATH);
                
                //startPath = startPath != null ? startPath : ROOT;
                if (canSelectDir) {
                        File file = new File(startPath);
                        selectedFile = file;
                        selectButton.setEnabled(true);
                }
                getDir(startPath);
        }

        private void getDir(String dirPath) {

                boolean useAutoSelection = dirPath.length() < currentPath.length();

                Integer position = lastPositions.get(parentPath);

                getDirImpl(dirPath);

                if (position != null && useAutoSelection) {
                        getListView().setSelection(position);
                }

        }

        private void getDirImpl(final String dirPath) {

                currentPath = dirPath;

                final List<String> item = new ArrayList<String>();
                path = new ArrayList<String>();
                mList = new ArrayList<HashMap<String, Object>>();

                File f = new File(currentPath);
                File[] files = f.listFiles();
                if (files == null) {
                        currentPath = ROOT;
                        f = new File(currentPath);
                        files = f.listFiles();
                }
                myPath.setText("*.lnx, *.com, *.o\n"+currentPath);

                if (!currentPath.equals(ROOT)) {
                		/*
                        item.add(ROOT);
                        addItem(ROOT, R.drawable.folder);
                        path.add(ROOT);
						*/
                        item.add("../");
                        addItem("../", R.drawable.folder);
                        path.add(f.getParent());
                        parentPath = f.getParent();

                }

                TreeMap<String, String> dirsMap = new TreeMap<String, String>();
                TreeMap<String, String> dirsPathMap = new TreeMap<String, String>();
                TreeMap<String, String> filesMap = new TreeMap<String, String>();
                TreeMap<String, String> filesPathMap = new TreeMap<String, String>();
                for (File file : files) {
                        if (file.isDirectory()) {
                                String dirName = file.getName();
                                dirsMap.put(dirName, dirName);
                                dirsPathMap.put(dirName, file.getPath());
                        } else {
                                final String fileName = file.getName();
                                final String fileNameLwr = fileName.toLowerCase();
                                // se ha um filtro de formatos, utiliza-o
                                if (formatFilter != null) {
                                        boolean contains = false;
                                        for (int i = 0; i < formatFilter.length; i++) {
                                                final String formatLwr = formatFilter[i].toLowerCase();
                                                if (fileNameLwr.endsWith(formatLwr)) {
                                                        contains = true;
                                                        break;
                                                }
                                        }
                                        if (contains) {
                                                filesMap.put(fileName, fileName);
                                                filesPathMap.put(fileName, file.getPath());
                                        }
                                        // senao, adiciona todos os arquivos
                                } else {
                                        filesMap.put(fileName, fileName);
                                        filesPathMap.put(fileName, file.getPath());
                                }
                        }
                }
                item.addAll(dirsMap.tailMap("").values());
                item.addAll(filesMap.tailMap("").values());
                path.addAll(dirsPathMap.tailMap("").values());
                path.addAll(filesPathMap.tailMap("").values());

                SimpleAdapter fileList = new SimpleAdapter(this, mList,
                                R.layout.file_dialog_row,
                                new String[] { ITEM_KEY, ITEM_IMAGE }, new int[] {
                                                R.id.fdrowtext, R.id.fdrowimage });

                for (String dir : dirsMap.tailMap("").values()) {
                        addItem(dir, R.drawable.folder);
                }

                for (String file : filesMap.tailMap("").values()) {
                        addItem(file, R.drawable.file);
                }

                fileList.notifyDataSetChanged();

                setListAdapter(fileList);

        }

        private void addItem(String fileName, int imageId) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put(ITEM_KEY, fileName);
                item.put(ITEM_IMAGE, imageId);
                mList.add(item);
        }

        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {

                File file = new File(path.get(position));

                setSelectVisible(v);

                if (file.isDirectory()) {
                        selectButton.setEnabled(false);
                        if (file.canRead()) {
                                lastPositions.put(currentPath, position);
                                getDir(path.get(position));
                                if (canSelectDir) {
                                        selectedFile = file;
                                        v.setSelected(true);
                                        selectButton.setEnabled(true);
                                }
                        } else {
                        /*        final String msg = "[" + file.getName() + "] "
                                                + getText(R.string.cant_read_folder);
                                final String title = getString(R.string.fd_error);
                                Utils.showAlertDialog(this, title, msg, null);*/
                        }
                } else {
                        selectedFile = file;
                        v.setSelected(true);
                        selectButton.setEnabled(true);
                }
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        selectButton.setEnabled(false);

                        if (layoutCreate.getVisibility() == View.VISIBLE) {
                                layoutCreate.setVisibility(View.GONE);
                                layoutSelect.setVisibility(View.VISIBLE);
                        } else {
                                if (!currentPath.equals(ROOT)) {
                                        getDir(parentPath);
                                } else {
                                        return super.onKeyDown(keyCode, event);
                                }
                        }

                        return true;
                } else {
                        return super.onKeyDown(keyCode, event);
                }
        }

        private void setSelectVisible(View v) {
                layoutCreate.setVisibility(View.GONE);
                layoutSelect.setVisibility(View.VISIBLE);
                selectButton.setEnabled(false);
        }
} 