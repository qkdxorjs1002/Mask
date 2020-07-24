package kr.publicm.mask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZeroPayInfo {
    private Store[] stores;
    private SearchOptions searchOptions;
    private String result;
    private int totalCnt;

    public class Store {
        String bztypName, pobsAfstrName, pobsBaseAddr, pobsDtlAddr;
    }
    /*TODO: 현재 위치를 기점으로 검색할 수 있도록 좌표를 주소로 변환*/
    /*TODO: 페이지 처리 어떻게 할 건지*/
    public static class SearchOptions {
        static final int TYPE_NAME = 0, TYPE_ADDR = 1;

        int pageIndex, recordCountPerPage, firstIndex, lastIndex;
        String tryCode, skkCode, pobsAfstrName, pobsBaseAddr;

        public SearchOptions() {
            this.pageIndex = 1;
            this.recordCountPerPage = 10;
            this.firstIndex = 1;
            this.lastIndex = 10;
            this.tryCode = "01";
            this.skkCode = "";
            this.pobsAfstrName = "치킨";
            this.pobsBaseAddr = "";
        }

        public SearchOptions(String tryCode, String skkCode, String pobs, boolean type) {
            this.pageIndex = 1;
            this.recordCountPerPage = 10;
            this.firstIndex = 1;
            this.lastIndex = 10;
            this.tryCode = tryCode;
            this.skkCode = skkCode;
            if (type) {
                this.pobsAfstrName = pobs;
                this.pobsBaseAddr = "";
            } else {
                this.pobsAfstrName = "";
                this.pobsBaseAddr = pobs;
            }
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getRecordCountPerPage() {
            return recordCountPerPage;
        }

        public void setRecordCountPerPage(int recordCountPerPage) {
            this.recordCountPerPage = recordCountPerPage;
        }

        public int getFirstIndex() {
            return firstIndex;
        }

        public void setFirstIndex(int firstIndex) {
            this.firstIndex = firstIndex;
        }

        public int getLastIndex() {
            return lastIndex;
        }

        public void setLastIndex(int lastIndex) {
            this.lastIndex = lastIndex;
        }
    }

    public ZeroPayInfo() {
        this.searchOptions = new SearchOptions();
    }

    public ZeroPayInfo(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public void setSearchOptions(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }

    public SearchOptions getSearchOptions() {
        return searchOptions;
    }

    public Store[] getStores() {
        return stores;
    }

    public String getResult() {
        return result;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void refreshStores() {
        putJSON(getZeroPayInfo(searchOptions));
    }

    public void clearStores() { stores = null; }

    public boolean increasePage() {
        int maxPageIdx = (totalCnt / 10) + 1;

        if (searchOptions.getPageIndex() == maxPageIdx) {
            return false;
        } else if (searchOptions.getPageIndex() == (maxPageIdx - 1)) {
            int lastPageSize = totalCnt % 10;
            searchOptions.setRecordCountPerPage(searchOptions.getRecordCountPerPage() + lastPageSize);
            searchOptions.setPageIndex(searchOptions.getPageIndex() + 1);
            searchOptions.setFirstIndex(searchOptions.getLastIndex() + 1);
            searchOptions.setLastIndex(searchOptions.getLastIndex() + lastPageSize);

            return false;
        } else {
            searchOptions.setPageIndex(searchOptions.getPageIndex() + 1);
            searchOptions.setRecordCountPerPage(searchOptions.getRecordCountPerPage() + 10);
            searchOptions.setFirstIndex(searchOptions.getLastIndex() + 1);
            searchOptions.setLastIndex(searchOptions.getLastIndex() + 10);
        }

        return true;

    }

    public boolean decreasePage() {
        if (searchOptions.getPageIndex() > 1) {
            searchOptions.setRecordCountPerPage(searchOptions.getRecordCountPerPage() - (searchOptions.getLastIndex() - searchOptions.getFirstIndex() + 1));
            searchOptions.setLastIndex(searchOptions.getFirstIndex() - 1);
            searchOptions.setFirstIndex(searchOptions.getFirstIndex() - 10);
            searchOptions.setPageIndex(searchOptions.getPageIndex() - 1);
            if (searchOptions.getPageIndex() == 1) {
                return false;
            }

            return true;
        }
        return false;
    }

    private JSONObject getZeroPayInfo(SearchOptions options) {
        String url = "https://www.zeropay.or.kr/intro/frncSrchList_json.do";
        JSONObject jsonObject = null;
        try {
            jsonObject = new GetJSONTask("GET", url +
                    "?pageIndex=" + options.pageIndex +
                    "&recordCountPerPage=" + options.recordCountPerPage +
                    "&firstIndex=" + options.firstIndex +
                    "&lastIndex=" + options.lastIndex +
                    "&searchCondition=" +
                    "&tryCode=" + options.tryCode +
                    "&skkCode=" + options.skkCode +
                    "&pobsAfstrName=" + options.pobsAfstrName +
                    "&bztypName=" +
                    "&bztypSlt=" +
                    "&bztypTxt=" +
                    "&pobsBaseAddr=" + options.pobsBaseAddr +
                    "&_csrf=").execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void putJSON(JSONObject jsonObject) {
        if (jsonObject == null) {
            return ;
        }
        try {
            this.result = jsonObject.getString("result");
            this.totalCnt = jsonObject.getInt("totalCnt");
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            this.stores = new Store[jsonArray.length()];

            for (int idx = 0; idx < this.stores.length; idx++) {
                Store store = new Store();
                JSONObject json = (JSONObject) jsonArray.get(idx);

                store.bztypName = json.getString("bztypName");
                store.pobsAfstrName = json.getString("pobsAfstrName");
                store.pobsBaseAddr = json.getString("pobsBaseAddr");
                store.pobsDtlAddr = json.getString("pobsDtlAddr");

                this.stores[idx] = store;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
