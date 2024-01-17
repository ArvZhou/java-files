package script.formatdata;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FormatData {
    private ArrayList<Map<String, String>> getJsonData() {
        Type type = new TypeToken<ArrayList<Map<String, String>>>() {
        }.getType();

        return (new Gson()).fromJson(Data.data, type);
    }

    private String getNextKey(String currentKey, String[] keys) {
        int index = -1;

        for (int i = 0; i < keys.length - 1; i++) {
            if (currentKey.equals(keys[i])) {
                index = i;
            }
        }

        return index >= 0 ? keys[index + 1] : null;
    }

    private ArrayList<Object> convertArrayList(ArrayList<Map<String, String>> array, String currentKey, String[] keys) {
        String nextKey = getNextKey(currentKey, keys);
        ArrayList<Object> reslutArray = new ArrayList<>();
        Map<String, Object> tempMap = new HashMap<String, Object>();

        for (Map<String, String> item : array) {
            String name = item.get(currentKey);

            if (name == null || name.isEmpty()) {
                reslutArray.add(item);
            } else {
                Object currentMap = tempMap.get(name);

                if (currentMap == null) {
                    ArrayList<Object> typeArray = new ArrayList<>();
                    Map<String, Map<String, ArrayList<Object>>> insideMap = new HashMap<String, Map<String, ArrayList<Object>>>();
                    Map<String, ArrayList<Object>> subMap = new HashMap<String, ArrayList<Object>>();
                    typeArray.add(item);
                    subMap.put("sub_items", typeArray);
                    insideMap.put(name, subMap);
                    tempMap.put(name, insideMap);
                    reslutArray.add(insideMap);
                }

                if (currentMap instanceof HashMap) {
                    @SuppressWarnings(value = "unchecked")
                    Object subMap = ((HashMap<String, Object>) currentMap).get(name);
                    if (subMap instanceof HashMap) {
                        @SuppressWarnings(value = "unchecked")
                        ArrayList<Object> subItems = ((HashMap<String, ArrayList<Object>>) subMap).get("sub_items");

                        subItems.add(item);
                    }
                }
            }
        }

        if (nextKey != null) {
            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
                String key = entry.getKey();
                Object map = entry.getValue();

                if (map instanceof HashMap) {
                    @SuppressWarnings(value = "unchecked")
                    HashMap<String, Object> subMap = ((HashMap<String, HashMap<String, Object>>) map).get(key);
                    Object items = subMap.get("sub_items");

                    @SuppressWarnings(value = "unchecked")
                    ArrayList<Map<String, String>> subItems = (ArrayList<Map<String, String>>) items;

                    if (subItems.size() > 0) {
                        subMap.put("sub_items", convertArrayList(subItems, nextKey, keys));
                    }
                }
            }
        }

        return reslutArray;
    }

    public void generateData() {
        ArrayList<Map<String, String>> dataLists = getJsonData();
        String[] keys = { "category", "group1", "group2" };

        ArrayList<Object> reslut = convertArrayList(dataLists, keys[0], keys);

        System.out.println((new Gson()).toJson(reslut));
    }
}
