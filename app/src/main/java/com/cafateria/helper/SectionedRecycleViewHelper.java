package com.cafateria.helper;

import android.content.Context;
import android.util.Log;

import com.cafateria.adapters.FoodAdapter;
import com.cafateria.model.Cats;
import com.cafateria.model.Food;
import com.cafateria.model.SectionedRecycleViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ahmed on 3/20/2018.
 * *  *
 */

public class SectionedRecycleViewHelper {

    private static final String TAG = "SectRecViewHelp";
    private static Map<Integer, List<Food>> map = new HashMap<>();
    private static Map<Integer, Integer> indices = new HashMap<>();

    public static void setSections(Context context, FoodAdapter mAdapter, List<SectionedRecycleViewModel> sections, List<Food> list) {
        List<Food> temp = new ArrayList<>();
        for (SectionedRecycleViewModel srvm : sections) {
            temp.clear();
            if (srvm.num_return > 0) {
                if (isInitItems(srvm.cat_id)) {//add header and footer
                    Log.e(TAG, "first adding for category:" + getHeaderName(context, srvm.cat_id));
                    temp.add(new Food(srvm.items.size(), 0, 0, "header", getHeaderName(context, srvm.cat_id), null, 0, null, 0, null));
                    temp.addAll(srvm.items);
                    temp.add(new Food(0, 0, 0, "more", null, null, 0, null, srvm.cat_id, null));
                    map.put(srvm.cat_id, temp);
                    list.addAll(temp);
                    Log.e(TAG, "last index is" + (list.size() - 1) + " for category:" + getHeaderName(context, srvm.cat_id));
                    indices.put(srvm.cat_id, list.size() - 1);
                } else {//append without header or footer in specific index
                    Log.e(TAG, "adding new items from index" + (indices.get(srvm.cat_id)) + " for category:" + getHeaderName(context, srvm.cat_id));
                    list.addAll(indices.get(srvm.cat_id), srvm.items);
                    updateIndices(srvm.cat_id, srvm.items.size());
                    Log.e(TAG, "indices:" + indices);
                }
            }
            //update last id
            Global.LAST_IDS.put(srvm.cat_id, srvm.last_item_id);
        }

        //handel delete more


        mAdapter.notifyDataSetChanged();
    }

    private static void updateIndices(int cat_id, int size) {
        int old_index = indices.get(cat_id);

        //for each item index if item index > my index so increse it as mine
        for (Map.Entry<Integer, Integer> entity : indices.entrySet()) {
            if (entity.getValue() > old_index) {
                indices.put(entity.getKey(), entity.getValue() + size);
            }
        }
        indices.put(cat_id, indices.get(cat_id) + size);
        Log.e(TAG, "increse from " + old_index + " to" + indices.get(cat_id) + " (+=)" + size);

    }

    private static String getHeaderName(Context context, int cat_id) {
        for (Cats cat : Global.CATS) {
            if (cat.getId() == cat_id)
                return cat.getName(context);
        }
        return "header";
    }

    private static boolean isInitItems(int cat_id) {
        Log.e(TAG, "key:" + cat_id);
        Log.e(TAG, "map:" + map);
        return !map.containsKey(cat_id);
    }
}
