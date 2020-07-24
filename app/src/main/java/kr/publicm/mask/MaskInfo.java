package kr.publicm.mask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MaskInfo {
    private CustomLocation refPosition;
    private Store[] stores;

    public class Store {
        double lat, lng;
        String code, name, addr, type, stock_at, remain_stat, created_at;
    }

    public MaskInfo(CustomLocation location) {
        refPosition = location;
    }

    public void setLocation(double lat, double lng, int range) {
        refPosition.setLocation(lat, lng, range);
    }

    public CustomLocation getRefLocation() {
        return refPosition;
    }

    public Store[] getStores() {
        return stores;
    }

    public void refreshStores() {
        putJSON(getMaskInfo(refPosition));
    }

    private JSONObject getMaskInfo(CustomLocation location) {
        String url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json";
        JSONObject jsonObject = null;
        while(true) {
            try {
                jsonObject = new GetJSONTask("GET", url +
                        "?lat=" + location.getLat() +
                        "&lng=" + location.getLng() +
                        "&m=" + location.getRange()).execute().get();
                try {
                    if (jsonObject.getInt("count") >= 20) {
                        break;
                    } else if (location.getRange() > 5000) {
                        break;
                    } else {
                        location.setRange(location.getRange() + 300);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }
    /*TODO:리스트 상태별로 정렬*/
    private void putJSON(JSONObject jsonObject) {
        if (jsonObject == null) {
            return ;
        }
        try {
            this.stores = new Store[jsonObject.getInt("count")];
            JSONArray jsonArray = jsonObject.getJSONArray("stores");

            for (int idx = 0; idx < this.stores.length; idx++) {
                Store store = new Store();
                JSONObject json = (JSONObject) jsonArray.get(idx);

                store.lat = json.getDouble("lat");
                store.lng = json.getDouble("lng");
                store.code = json.getString("code");
                store.name = json.getString("name");
                store.addr = json.getString("addr");
                store.type = json.getString("type");
                store.stock_at = json.getString("stock_at");
                store.remain_stat = json.getString("remain_stat");
                store.created_at = json.getString("created_at");

                this.stores[idx] = store;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
