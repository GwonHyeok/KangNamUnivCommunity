package com.yscn.knucommunity.Util;

import com.yscn.knucommunity.Items.LibrarySeatItems;
import com.yscn.knucommunity.Items.MajorSimpleListItems;
import com.yscn.knucommunity.Items.StudentCouncilListItems;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 14. 11. 19..
 */
public class NetworkUtil {
    private static NetworkUtil instance;

    private HttpClient httpClient = new DefaultHttpClient();

    private NetworkUtil() {

    }

    public static NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    public ArrayList<LibrarySeatItems> getLibrarySeatInfo() throws IOException {
        ArrayList<LibrarySeatItems> itemses = new ArrayList<LibrarySeatItems>();
        URL url = new URL(UrlList.LIBRARY_SEAT_URL);
        Document document = Jsoup.parse(url, 10000);
        Elements elements = document.getElementsByTag("tr");
        /*
            (0 || 1 || 2) 은 무시할 정보 -- for 문 index 를 2부터
         */
        for (int i = 3; i < elements.size(); i++) {
            Elements innerElements = elements.get(i).getElementsByTag("td");
            int total = parseInt(innerElements.get(2).text());
            int useSeat = parseInt(innerElements.get(3).text());
            int emptySeat = total - useSeat;
            itemses.add(new LibrarySeatItems(total, useSeat, emptySeat));
        }
        return itemses;
    }

    public ArrayList<MajorSimpleListItems> getMajorSimpleInfo() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        ArrayList<MajorSimpleListItems> itemses = new ArrayList<MajorSimpleListItems>();
        InputStream inputStream = postData(UrlList.MAJOR_GET_SIMPLE_INFO, null);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream));
        if (checkResultData(jsonObject)) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            for (Object object : jsonArray) {
                JSONObject jObject = (JSONObject) object;
                String majorType = URLDecoder.decode(jObject.get("majorType").toString(), "UTF-8");
                String majorName = URLDecoder.decode(jObject.get("majorName").toString(), "UTF-8");
                String majorHomepage = URLDecoder.decode(jObject.get("majorHomepage").toString(), "UTF-8");
                itemses.add(new MajorSimpleListItems(majorName, majorHomepage, majorType));
            }
        }
        return itemses;
    }

    public HashMap<String, ArrayList<StudentCouncilListItems>> getCouncilInfo() throws IOException, ParseException {
        HashMap<String, ArrayList<StudentCouncilListItems>> dataMap = new HashMap<String, ArrayList<StudentCouncilListItems>>();
        ArrayList<StudentCouncilListItems> riffleItem = new ArrayList<StudentCouncilListItems>();
        ArrayList<StudentCouncilListItems> dragItem = new ArrayList<StudentCouncilListItems>();
        InputStream inputStream = postData(UrlList.STUDENT_GET_COUNCIL_INFO, null);
        JSONParser jsonParser = new JSONParser();
        JSONObject object = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream));

        if (!checkResultData(object)) {
            return null;
        }
        JSONArray jsonArray = (JSONArray) object.get("data");
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String type = jsonObject.get("type").toString();
            String title = jsonObject.get("title").toString();
            String message = jsonObject.get("message").toString();

            if (type.equals("riffle")) {
                riffleItem.add(new StudentCouncilListItems(title, message));
            } else if (type.equals("drag")) {
                dragItem.add(new StudentCouncilListItems(title, message));
            }
        }
        dataMap.put("riffle", riffleItem);
        dataMap.put("drag", dragItem);
        return dataMap;
    }

    private boolean checkResultData(JSONObject jsonObject) {
        return jsonObject.get("result").equals("success");
    }

    private InputStream postData(String URL, HashMap<String, String> parameter) throws IOException {
        HttpPost httpPost = new HttpPost(URL);

        if (parameter != null) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            Object[] key = parameter.keySet().toArray();
            for (Object aKey : key) {
                entityBuilder.addTextBody(aKey.toString(), parameter.get(aKey));
            }
            httpPost.setEntity(entityBuilder.build());
        }

        return httpClient.execute(httpPost).getEntity().getContent();
    }

    private static int parseInt(String str) {
        return Integer.parseInt(str.replace(" ", ""));
    }
}
