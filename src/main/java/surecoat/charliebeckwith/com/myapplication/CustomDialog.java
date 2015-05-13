/*
 * Copyright (C) 2015 Charlie Beckwith
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

package surecoat.charliebeckwith.com.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example CustomDialog class with no title. The Dialog will end up looking like this.
 * ______________________________
 * |  _____                      |
 * | |     |                     |
 * | |  W  |    Item One         |
 * | |_____|                     |
 * |_____________________________|
 * |  _____                      |
 * | |     |                     |
 * | |  X  |    Item Two         |
 * | |_____|                     |
 * |_____________________________|
 * |  _____                      |
 * | |     |                     |
 * | |  T  |    Item Three       |
 * | |_____|                     |
 * |_____________________________|
 */
public class CustomDialog extends Dialog {
    private ListView listView;

    //List item images go here
    private final int[] icons = new int[]{ R.drawable.ic_3d_rotation_black_18dp, R.drawable.ic_accessibility_black_18dp, R.drawable.ic_account_balance_wallet_black_18dp};
    //List Item titles go here
    private static final String[] titles = new String[]{"One", "Two", "Three"};

    private ArrayList<HashMap<String, Object>> list;
    private final String TITLE = "title";
    private final String IMAGE = "image";
    //The adapter to be used for the ListView
    private MySimpleAdapter adapter;

    private final String[] from = new String[]{TITLE, IMAGE};
    int[] to = {R.id.textView, R.id.imageView};
    //Tag for class
    private static final String TAG = CustomDialog.class.getSimpleName();
    public CustomDialog(Context context) {
        super(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //We want no title for the dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("");
        //Setting the content view for the custom dialog
        setContentView(R.layout.dialog_layout);
        listView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<HashMap<String,Object>>();
        //Adding the title and image to a hashmap, which is then added to an ArrayList which will
        // be passed through into the adapter
        for(int i = 0; i < titles.length; i++){
            HashMap<String, Object> hm = new HashMap<>();
            hm.put(TITLE, titles[i]);
            hm.put(IMAGE, icons[i]);
            list.add(hm);
            Log.d(TAG, "Adding " + i);
        }
        adapter = new MySimpleAdapter(getContext(), list, R.layout.list_item, from, to);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapterListener);
        super.onCreate(savedInstanceState);
    }

    //The item listener for the adapter. Since in MySimpleAdapter we set the tag of each of the
    //options as the strings used in the title, I use a switch with Strings to determine which
    //list item has been picked
    public AdapterView.OnItemClickListener adapterListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch((String) view.getTag()){
                case "One":
                    Log.d(TAG, "One selected");
                    break;
                case "Two":
                    Log.d(TAG,"Two selected");
                    break;

                case "Three":
                    Log.d(TAG, "Three selected");
                    break;
            }
            dismiss();
        }
    };

    public class MySimpleAdapter extends SimpleAdapter{
        private List<HashMap<String, Object>> map;
        private String[] from;
        private int layout;
        private int to[];
        private Context context;
        private LayoutInflater mInflater;
        public MySimpleAdapter(Context context, List<HashMap<String, Object>> data,
                               int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            layout = resource;
            map = data;
            this.from = from;
            this.to = to;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return this.createViewFromResource(position, convertView, parent, layout);
        }

        private View createViewFromResource(int position, View convertView,
                                            ViewGroup parent, int resource) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(resource, parent, false);
            } else {
                v = convertView;
            }
            //Setting the tag of the list item.
            v.setTag(map.get(position).get("title"));
            this.bindView(position, v);

            return v;
        }


        private void bindView(int position, View view) {
            final Map dataSet = map.get(position);
            if (dataSet == null) {
                return;
            }

            final ViewBinder binder = super.getViewBinder();
            final int count = to.length;

            for (int i = 0; i < count; i++) {
                Log.d(TAG, "binding view " + i);
                final View v = view.findViewById(to[i]);
                if (v != null) {
                    final Object data = dataSet.get(from[i]);
                    String text = data == null ? "" : data.toString();
                    if (text == null) {
                        text = "";
                    }

                    boolean bound = false;
                    if (binder != null) {
                        bound = binder.setViewValue(v, data, text);
                    }

                    if (!bound) {
                        if (v instanceof Checkable) {
                            if (data instanceof Boolean) {
                                ((Checkable) v).setChecked((Boolean) data);
                            } else if (v instanceof TextView) {
                                // Note: keep the instanceof TextView check at the bottom of these
                                // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                                setViewText((TextView) v, text);
                            } else {
                                throw new IllegalStateException(v.getClass().getName() +
                                        " should be bound to a Boolean, not a " +
                                        (data == null ? "<unknown type>" : data.getClass()));
                            }
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else if (v instanceof ImageView) {
                            if (data instanceof Integer) {
                                setViewImage((ImageView) v, (Integer) data);
                            } else if (data instanceof Bitmap) {
                                setViewImage((ImageView) v, (Bitmap) data);
                            } else {
                                setViewImage((ImageView) v, text);
                            }
                        } else {
                            throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                    " view that can be bounds by this SimpleAdapter");
                        }
                    }
                }
            }
        }


        private void setViewImage(ImageView v, Bitmap bmp) {
            v.setImageBitmap(bmp);
        }
    }


}
