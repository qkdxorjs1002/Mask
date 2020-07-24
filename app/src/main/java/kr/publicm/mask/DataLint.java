package kr.publicm.mask;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DataLint {

    Activity mContext;

    public DataLint(Activity context) {
        this.mContext = context;
    }

    public class Stock {
        private String stockText;
        private int color;

        public Stock() {

        }

        public Stock(String stockText, int color) {
            setStockText(stockText);
            setColor(color);
        }

        public String getStockText() {
            return stockText;
        }

        public void setStockText(String stockText) {
            this.stockText = stockText;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

    }

    private String getStringFromR(int resourceId) {

        return mContext.getString(resourceId);
    }

    public Long getDistanceFromTo(CustomLocation from, CustomLocation to) {
        return Math.round(
                Haversine.haversine(
                        from.getLat(),
                        from.getLng(),
                        to.getLat(),
                        to.getLng()
                ) * 1000);
    }

    public String getTimeOffset(String datetime) {
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA);
        String offsetText = getStringFromR(R.string.TIME_OLD);

        try {
            dateTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            dateTime.parse(datetime);
            long offset = (today.getTimeInMillis() - dateTime.getCalendar().getTimeInMillis()) / 60000;
            if(offset < 60) {
                offsetText = Long.toString(offset) + getStringFromR(R.string.TIME_MIN);
            } else if (offset < 1440) {
                offsetText = Long.toString(offset / 60) + getStringFromR(R.string.TIME_HOUR);
            } else {
                offsetText = Long.toString(offset / 1440) + getStringFromR(R.string.TIME_DAY);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return offsetText + getStringFromR(R.string.TIME_BEFORE);
    }

    public Stock getStockText(String remainStat) {
        Stock stock;

        switch (remainStat) {
            case "plenty":
                stock = new Stock(getStringFromR(R.string.STOCK_PLENTY), R.color.colorStockPlenty);
                break;
            case "some":
                stock = new Stock(getStringFromR(R.string.STOCK_SOME), R.color.colorStockSome);
                break;
            case "few":
                stock = new Stock(getStringFromR(R.string.STOCK_FEW), R.color.colorStockFew);
                break;
            case "empty":
                stock = new Stock(getStringFromR(R.string.STOCK_EMPTY), R.color.colorStockEmpty);
                break;
            default:
                stock = new Stock(getStringFromR(R.string.STOCK_OUT_OF), R.color.colorStockEmpty);
        }

        return stock;
    }

    /*TODO: API 사용불가로 임시 하드 코딩, 적절한 API로 대체해야함*/
    public ZeroPayInfo.SearchOptions getLocalCode(String address, boolean type) {
        String jsonLocalCode = null;
        String tryCode = "01", skkCode = "", pobs = "";

        String[] address_split = address.split(" ", 3);

        switch (address_split[0]) {
            case "서울시":
            case "서울특별시":
                jsonLocalCode = getStringFromR(R.string.seoul);
                break;
            case "부산시":
            case "부산광역시":
                jsonLocalCode = getStringFromR(R.string.busan);
                break;
            case "울산시":
            case "울산광역시":
                jsonLocalCode = getStringFromR(R.string.ulsan);
                break;
            case "인천시":
            case "인천광역시":
                jsonLocalCode = getStringFromR(R.string.incheon);
                break;
            case "대구시":
            case "대구광역시":
                jsonLocalCode = getStringFromR(R.string.daegu);
                break;
            case "대전시":
            case "대전관영식":
                jsonLocalCode = getStringFromR(R.string.daejeon);
                break;
            case "광주시":
            case "광주광역시":
                jsonLocalCode = getStringFromR(R.string.gwangju);
                break;
            case "경기":
            case "경기도":
                jsonLocalCode = getStringFromR(R.string.gyeonggi);
                break;
            case "강원":
            case "강원도":
                jsonLocalCode = getStringFromR(R.string.gangwon);
                break;
            case "충북":
            case "충청북도":
                jsonLocalCode = getStringFromR(R.string.chungbuk);
                break;
            case "충남":
            case "충청남도":
                jsonLocalCode = getStringFromR(R.string.chungnam);
                break;
            case "경북":
            case "경상북도":
                jsonLocalCode = getStringFromR(R.string.gyeongbuk);
                break;
            case "경남":
            case "경상남도":
                jsonLocalCode = getStringFromR(R.string.gyeongnam);
                break;
            case "전북":
            case "전라북도":
                jsonLocalCode = getStringFromR(R.string.jeonbuk);
                break;
            case "전남":
            case "전라남도":
                jsonLocalCode = getStringFromR(R.string.jeonnam);
                break;
            case "제주도":
            case "제주특별자치도":
                jsonLocalCode = getStringFromR(R.string.jeju);
                break;
            case "세종시":
            case "세종측별자치시":
                jsonLocalCode = getStringFromR(R.string.sejong);
                break;
            }

        try {
            JSONObject jsonObject = new JSONObject(new JSONTokener(jsonLocalCode));
            JSONArray jsonArray = jsonObject.getJSONArray("list");

            for (int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject skk = jsonArray.getJSONObject(idx);
                if (skk.getString("skkName").equals(address_split[1])) {
                    tryCode = String.format("%02d", skk.getInt("tryCode"));
                    switch (address_split.length) {
                        case 2:
                            skkCode = String.format("%02d", skk.getInt("skkCode"));
                            break;
                        case 3:
                            skkCode = String.format("%02d", skk.getInt("skkCode"));
                            pobs = address_split[2];
                            break;
                    }

                    return new ZeroPayInfo.SearchOptions(tryCode, skkCode, pobs, type);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
